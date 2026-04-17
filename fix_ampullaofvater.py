#!/usr/bin/env python3
"""Normalize stray 'ampullaofvater' -> 'ampulla_of_vater' in Tumor_Type_Composition.

normalize_data.py added this mapping to TUMOR_TYPE_MAP, but it is a one-shot
script that rebuilds the v3 files from a v2 baseline + backup. Running it
again would be destructive, so this targeted fix only rewrites the
Tumor_Type_Composition column in the v3 data files in place.
"""
import os

from normalize_data import normalize_tumor_types

DATA_DIR = "webapp/src/main/resources/data"
TARGETS = [
    ("v3_multi_type_residue.txt", "Tumor_Type_Composition"),
    ("v3_multi_type_variant_file.txt", "Tumor_Type_Composition"),
]


def fix_file(filename, column_name):
    path = os.path.join(DATA_DIR, filename)
    with open(path) as f:
        lines = f.readlines()
    header = lines[0].rstrip("\n").split("\t")
    col = header.index(column_name)
    changed = 0
    for i, line in enumerate(lines[1:], start=1):
        fields = line.rstrip("\n").split("\t")
        if len(fields) <= col:
            continue
        new_val = normalize_tumor_types(fields[col])
        if new_val != fields[col]:
            fields[col] = new_val
            lines[i] = "\t".join(fields) + "\n"
            changed += 1
    with open(path, "w") as f:
        f.writelines(lines)
    print(f"{filename}: rewrote {changed} rows")


def main():
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    for filename, col in TARGETS:
        fix_file(filename, col)


if __name__ == "__main__":
    main()
