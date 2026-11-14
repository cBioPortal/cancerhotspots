#!/usr/bin/env python3
"""
Normalize v3 data and split into v2-only and v3 (v2+v3 combined) files.

Steps:
1. For residue file: restore v2 rows from old file, normalize v3 rows, create split files
2. For variant file: restore v2 rows from git master, normalize v3 rows, create split files
3. Drop exact duplicate variant rows (keep the first occurrence) so a repeated
   data drop cannot be written through into v3_multi_type_variant_file.txt

Check the published tables without rewriting them:

    python3 normalize_data.py --check
"""

import re
import shutil
import subprocess
import os
import sys

DATA_DIR = "webapp/src/main/resources/data"
BACKUP_DIR = os.path.join(DATA_DIR, "v3_original_backup")

# Tumor type name mapping: v3 name -> v2 name
TUMOR_TYPE_MAP = {
    "esophagusstomach": "stomach",
    "cnsbrain": "brain",
    "headandneck": "head_neck",
    "ovaryfallopiantube": "ovary",
    "uterine": "uterus",
    "lymphoid": "blood",
    "myeloid": "blood",
    "softtissue": "soft_tissue",
    "biliarytract": "biliary_tract",
    "adrenalgland": "adrenal_gland",
    "peripheralnervoussystem": "soft_tissue",
    "vulvavagina": "vulva",
    "ampullaofvater": "ampulla_of_vater",
}


def normalize_tumor_types(composition_str):
    """Replace v3 tumor type names with v2 names in a tumor type composition string.

    Also merges counts for types that map to the same v2 name (e.g. lymphoid + myeloid -> blood).
    """
    if not composition_str or composition_str.strip() == "":
        return composition_str

    # Parse into dict of type:count
    counts = {}
    for part in composition_str.split("|"):
        if ":" not in part:
            continue
        tumor_type, count = part.rsplit(":", 1)
        tumor_type = tumor_type.strip()
        count = int(count.strip())

        # Map v3 name to v2 name
        mapped_type = TUMOR_TYPE_MAP.get(tumor_type, tumor_type)
        counts[mapped_type] = counts.get(mapped_type, 0) + count

    # Reconstruct in original order (but merged)
    seen = set()
    parts = []
    for part in composition_str.split("|"):
        if ":" not in part:
            continue
        tumor_type = part.rsplit(":", 1)[0].strip()
        mapped_type = TUMOR_TYPE_MAP.get(tumor_type, tumor_type)
        if mapped_type not in seen:
            seen.add(mapped_type)
            parts.append(f"{mapped_type}:{counts[mapped_type]}")

    return "|".join(parts)


def normalize_qvalue(val):
    """Normalize a q-value: 0.0 -> 0, otherwise leave as-is."""
    if val is None:
        return val
    val = val.strip()
    if val == "0.0":
        return "0"
    return val


def dedupe_preserve_order(lines):
    """Drop exact duplicate records, keeping the first occurrence.

    Blank lines are skipped. The trailing newline is not part of the
    comparison, and every returned line ends with a newline.
    """
    seen = set()
    unique = []
    for line in lines:
        key = line.rstrip("\r\n")
        if key == "" or key in seen:
            continue
        seen.add(key)
        unique.append(key + "\n")
    return unique


def fix_integer_field(val):
    """Convert float-like integers (e.g. '10.0') to integers."""
    if val is None:
        return val
    val = val.strip()
    if val == "":
        return val
    try:
        f = float(val)
        if f == int(f):
            return str(int(f))
    except ValueError:
        pass
    return val


def process_residue_file():
    """Process the residue file: split into v2-only and v3 (combined) files."""
    old_file = os.path.join(DATA_DIR, "v2_multi_type_residue_old.txt")
    current_file = os.path.join(DATA_DIR, "v2_multi_type_residue.txt")

    # Read old file (original v2 data) - these are the authoritative v2 rows
    with open(old_file, "r") as f:
        old_lines = f.readlines()

    old_header = old_lines[0]
    old_data_lines = old_lines[1:]  # 1165 v2 rows

    # Read current file (v2 + v3 combined with modified q-values)
    with open(current_file, "r") as f:
        current_lines = f.readlines()

    current_header = current_lines[0]
    num_v2_rows = len(old_data_lines)  # 1165

    # v3-only rows are the new rows appended after the original v2 rows
    v3_only_lines = current_lines[num_v2_rows + 1:]  # skip header + v2 rows

    print(f"Residue file: {num_v2_rows} v2 rows, {len(v3_only_lines)} v3-only rows")

    # Parse header to find column indices
    header_cols = old_header.strip().split("\t")
    col_idx = {name: i for i, name in enumerate(header_cols)}

    qvalue_col = col_idx["Q-value"]
    qvalue_pancan_col = col_idx["Qvalue_Pancan"]
    qvalue_ct_col = col_idx["QvalueCT"]
    tumor_count_col = col_idx["Tumor_Count"]
    tumor_type_count_col = col_idx["Tumor_Type_Count"]
    composition_col = col_idx["Tumor_Type_Composition"]

    # Process v3-only rows: normalize tumor types, q-values, counts
    normalized_v3_lines = []
    for line in v3_only_lines:
        if line.strip() == "":
            continue
        fields = line.rstrip("\n").split("\t")

        # Normalize tumor type composition
        if len(fields) > composition_col:
            fields[composition_col] = normalize_tumor_types(fields[composition_col])

        # Normalize q-values
        for qi in [qvalue_col, qvalue_pancan_col, qvalue_ct_col]:
            if len(fields) > qi:
                fields[qi] = normalize_qvalue(fields[qi])

        # Fix integer fields
        if len(fields) > tumor_count_col:
            fields[tumor_count_col] = fix_integer_field(fields[tumor_count_col])
        if len(fields) > tumor_type_count_col:
            fields[tumor_type_count_col] = fix_integer_field(fields[tumor_type_count_col])

        normalized_v3_lines.append("\t".join(fields) + "\n")

    print(f"  Normalized {len(normalized_v3_lines)} v3 rows")

    # Write v2-only file (restore from old file)
    v2_output = os.path.join(DATA_DIR, "v2_multi_type_residue.txt")
    with open(v2_output, "w") as f:
        f.write(old_header)
        f.writelines(old_data_lines)
    print(f"  Wrote v2 file: {v2_output} ({num_v2_rows} data rows)")

    # Write v3 file (old v2 rows + normalized v3 rows)
    v3_output = os.path.join(DATA_DIR, "v3_multi_type_residue.txt")
    with open(v3_output, "w") as f:
        f.write(old_header)
        f.writelines(old_data_lines)
        f.writelines(normalized_v3_lines)
    print(f"  Wrote v3 file: {v3_output} ({num_v2_rows + len(normalized_v3_lines)} data rows)")


def process_variant_file():
    """Process the variant file: split into v2-only and v3 (combined) files."""
    current_file = os.path.join(DATA_DIR, "v2_multi_type_variant_file.txt")

    # Get original v2 variant file from git master
    result = subprocess.run(
        ["git", "show", "master:webapp/src/main/resources/data/v2_multi_type_variant_file.txt"],
        capture_output=True, text=True, check=True
    )
    old_content = result.stdout
    old_lines = old_content.split("\n")
    # Remove trailing empty line if present
    while old_lines and old_lines[-1] == "":
        old_lines.pop()

    old_header = old_lines[0] + "\n"
    old_data_lines = [line + "\n" for line in old_lines[1:]]
    num_v2_rows = len(old_data_lines)

    # Read current file
    with open(current_file, "r") as f:
        current_lines = f.readlines()

    current_header = current_lines[0]

    # Parse header
    header_cols = current_header.strip().split("\t")
    col_idx = {name: i for i, name in enumerate(header_cols)}
    composition_col = col_idx["Tumor_Type_Composition"]

    # v3-only rows are lines in the current file that are not exact v2 rows.
    # The original v3 drop repeated most of those lines; exact copies are
    # removed below, keeping the first occurrence.
    old_data_set = set(line.strip() for line in old_data_lines)

    v3_only_lines = []
    for line in current_lines[1:]:  # skip header
        if line.strip() == "":
            continue
        if line.strip() not in old_data_set:
            v3_only_lines.append(line)

    print(f"Variant file: {num_v2_rows} v2 rows, {len(v3_only_lines)} v3-only rows (new or modified)")

    # Normalize v3 rows
    normalized_v3_lines = []
    for line in v3_only_lines:
        fields = line.rstrip("\n").split("\t")

        # Normalize tumor type composition
        if len(fields) > composition_col:
            fields[composition_col] = normalize_tumor_types(fields[composition_col])

        normalized_v3_lines.append("\t".join(fields) + "\n")

    # The pre-normalization variant drop repeated 524 v3-only rows three
    # times (exact copies). Keep the first copy so the published file
    # matches one listing of each variant.
    before = len(normalized_v3_lines)
    old_data_lines = dedupe_preserve_order(old_data_lines)
    normalized_v3_lines = dedupe_preserve_order(normalized_v3_lines)
    num_v2_rows = len(old_data_lines)
    dropped = before - len(normalized_v3_lines)
    print(f"  Normalized {len(normalized_v3_lines)} v3 rows")
    if dropped:
        print(f"  Dropped {dropped} exact duplicate v3-only rows (kept first occurrence)")

    # Write v2-only file (restore from master)
    v2_output = os.path.join(DATA_DIR, "v2_multi_type_variant_file.txt")
    with open(v2_output, "w") as f:
        f.write(old_header)
        f.writelines(old_data_lines)
    print(f"  Wrote v2 file: {v2_output} ({num_v2_rows} data rows)")

    # Write v3 file (original v2 rows + normalized v3 rows)
    v3_output = os.path.join(DATA_DIR, "v3_multi_type_variant_file.txt")
    with open(v3_output, "w") as f:
        f.write(old_header)
        f.writelines(old_data_lines)
        f.writelines(normalized_v3_lines)
    print(f"  Wrote v3 file: {v3_output} ({num_v2_rows + len(normalized_v3_lines)} data rows)")


def verify_no_v3_names(filepath):
    """Verify that no v3 tumor type names remain in a file."""
    with open(filepath, "r") as f:
        content = f.read()

    issues = []
    for v3_name in TUMOR_TYPE_MAP:
        # Use word boundary-like check: the v3 name should not appear as a standalone tumor type
        # Check for pattern like "v3name:" which indicates it's a tumor type key
        pattern = re.compile(r'(?:^|\|)' + re.escape(v3_name) + r':', re.MULTILINE)
        matches = pattern.findall(content)
        if matches:
            issues.append(f"  Found {len(matches)} occurrences of '{v3_name}:'")

    if issues:
        print(f"  WARNING - v3 names still found in {filepath}:")
        for issue in issues:
            print(issue)
    else:
        print(f"  OK - No v3 tumor type names in {filepath}")


def verify_qvalues(filepath):
    """Verify no '0.0' q-values remain."""
    with open(filepath, "r") as f:
        lines = f.readlines()

    header = lines[0].strip().split("\t")
    col_idx = {name: i for i, name in enumerate(header)}

    qvalue_cols = []
    for name in ["Q-value", "Qvalue_Pancan", "QvalueCT"]:
        if name in col_idx:
            qvalue_cols.append(col_idx[name])

    count_0_dot_0 = 0
    for line in lines[1:]:
        fields = line.strip().split("\t")
        for qi in qvalue_cols:
            if len(fields) > qi and fields[qi] == "0.0":
                count_0_dot_0 += 1

    if count_0_dot_0 > 0:
        print(f"  WARNING - Found {count_0_dot_0} '0.0' q-values in {filepath}")
    else:
        print(f"  OK - No '0.0' q-values in {filepath}")


# Published tables loaded by the multi-type webapp. The v3 original backup
# is the pre-normalization drop and is not part of this check.
CHECKED_FILES = [
    "v2_multi_type_residue.txt",
    "v3_multi_type_residue.txt",
    "v2_multi_type_variant_file.txt",
    "v3_multi_type_variant_file.txt",
]

VARIANT_KEY = [
    "Hugo_Symbol",
    "Residue",
    "Reference_Amino_Acid",
    "Variant_Amino_Acid",
    "Amino_Acid_Position",
]
RESIDUE_KEY = ["Hugo_Symbol", "Residue"]


def identity_key_columns(header_cols):
    """Columns that identify one record. The other columns are the payload."""
    if all(name in header_cols for name in VARIANT_KEY) and "Q-value" not in header_cols:
        return VARIANT_KEY
    if all(name in header_cols for name in RESIDUE_KEY):
        return RESIDUE_KEY
    return None


def check_duplicate_rows(filepath):
    """Report exact duplicate lines and same-key rows that are not identical.

    Exact duplicates are repeated full lines. Near-duplicates share the
    identity columns but differ in some other field; this check only reports
    them. Returns a list of problem strings (empty when the file is clean).
    """
    problems = []
    with open(filepath, "r") as f:
        raw_lines = f.read().splitlines()

    if not raw_lines:
        message = f"{filepath}: empty file"
        print(f"  WARNING - {message}")
        return [message]

    header_cols = raw_lines[0].split("\t")
    data = [line for line in raw_lines[1:] if line.strip()]

    counts = {}
    order = []
    for line in data:
        if line not in counts:
            order.append(line)
            counts[line] = 0
        counts[line] += 1

    exact = [(line, n) for line, n in counts.items() if n > 1]
    if exact:
        extra = sum(n - 1 for _, n in exact)
        message = (
            f"{filepath}: {len(exact)} exact duplicate rows "
            f"({extra} extra copies); first: {exact[0][0][:120]}"
        )
        problems.append(message)

    key_names = identity_key_columns(header_cols)
    if key_names is None:
        message = f"{filepath}: could not determine identity columns"
        problems.append(message)
    else:
        index = [header_cols.index(name) for name in key_names]
        groups = {}
        for line in order:
            fields = line.split("\t")
            if len(fields) != len(header_cols):
                message = (
                    f"{filepath}: row has {len(fields)} fields, "
                    f"header has {len(header_cols)}: {line[:120]}"
                )
                problems.append(message)
                continue
            key = tuple(fields[i] for i in index)
            groups.setdefault(key, []).append(line)
        near = [(key, rows) for key, rows in groups.items() if len(rows) > 1]
        if near:
            key, _rows = near[0]
            message = (
                f"{filepath}: {len(near)} near-duplicate groups share "
                f"{key_names} but differ in another column; example key {key}"
            )
            problems.append(message)

    if problems:
        for problem in problems:
            print(f"  WARNING - {problem}")
    else:
        print(f"  OK - no exact or near-duplicate rows in {filepath} ({len(data)} data rows)")
    return problems


def check_published_data_files():
    """Check the multi-type tables the webapp ships. Returns problem strings."""
    print("Checking hotspot tables for duplicate rows...")
    problems = []
    for name in CHECKED_FILES:
        path = os.path.join(DATA_DIR, name)
        if not os.path.exists(path):
            message = f"missing {path}"
            print(f"  WARNING - {message}")
            problems.append(message)
            continue
        problems.extend(check_duplicate_rows(path))
    return problems


def backup_original_files():
    """Back up the original (non-normalized) v3 data files before any modifications."""
    os.makedirs(BACKUP_DIR, exist_ok=True)

    files_to_backup = [
        "v2_multi_type_residue.txt",
        "v2_multi_type_variant_file.txt",
    ]

    for filename in files_to_backup:
        src = os.path.join(DATA_DIR, filename)
        dst = os.path.join(BACKUP_DIR, filename)
        if os.path.exists(src):
            shutil.copy2(src, dst)
            print(f"  Backed up {filename} -> v3_original_backup/{filename}")


if __name__ == "__main__":
    os.chdir(os.path.dirname(os.path.abspath(__file__)))

    if len(sys.argv) > 1 and sys.argv[1] == "--check":
        found = check_published_data_files()
        if found:
            print(f"FAILED: {len(found)} duplicate-row problem(s)")
            sys.exit(1)
        print("OK")
        sys.exit(0)

    print("=" * 60)
    print("Backing up original (non-normalized) v3 data...")
    print("=" * 60)
    backup_original_files()

    print()
    print("=" * 60)
    print("Processing residue file...")
    print("=" * 60)
    process_residue_file()

    print()
    print("=" * 60)
    print("Processing variant file...")
    print("=" * 60)
    process_variant_file()

    print()
    print("=" * 60)
    print("Verification")
    print("=" * 60)

    for f in [
        os.path.join(DATA_DIR, "v2_multi_type_residue.txt"),
        os.path.join(DATA_DIR, "v3_multi_type_residue.txt"),
        os.path.join(DATA_DIR, "v2_multi_type_variant_file.txt"),
        os.path.join(DATA_DIR, "v3_multi_type_variant_file.txt"),
    ]:
        verify_no_v3_names(f)

    print()
    for f in [
        os.path.join(DATA_DIR, "v2_multi_type_residue.txt"),
        os.path.join(DATA_DIR, "v3_multi_type_residue.txt"),
    ]:
        verify_qvalues(f)

    print()
    print("Line counts:")
    for f in [
        "v2_multi_type_residue.txt",
        "v3_multi_type_residue.txt",
        "v2_multi_type_variant_file.txt",
        "v3_multi_type_variant_file.txt",
    ]:
        path = os.path.join(DATA_DIR, f)
        if os.path.exists(path):
            with open(path, "r") as fh:
                count = sum(1 for line in fh if line.strip())
            print(f"  {f}: {count} lines (header + {count - 1} data rows)")

    print()
    problems = check_published_data_files()
    if problems:
        sys.exit(1)
