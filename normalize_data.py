#!/usr/bin/env python3
"""
Normalize v3 data and split into v2-only and v3 (v2+v3 combined) files.

Steps:
1. For residue file: restore v2 rows from old file, normalize v3 rows, create split files
2. For variant file: restore v2 rows from git master, normalize v3 rows, create split files
"""

import re
import shutil
import subprocess
import os

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

    # Find v3-only rows: rows in current file that are NOT in old file
    # Since the old file has 3499 data rows and current has 5075 data rows,
    # the v3 rows are the additional ones.
    # We need to identify them - they contain v3 tumor type names
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

    print(f"  Normalized {len(normalized_v3_lines)} v3 rows")

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
