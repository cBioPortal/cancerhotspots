#!/usr/bin/env python3
"""Add a 'Variant_Samples' sheet to hotspots_v3.xlsx with per-variant rows.

The existing Sheet/Old tabs give codon-level statistics and a residue-level
Samples (tumor-type) breakdown. They don't show which samples are mutated
to which alternate amino acid. v2's hotspots_v2.xls had one row per
(gene, codon, ref_AA, variant_AA) with a per-variant Samples breakdown.
This script adds the same information as a new sheet.

Source: webapp/src/main/resources/data/v3_multi_type_variant_file.txt
        (every row currently appears 3x; we dedupe).
"""
import os
from collections import OrderedDict

import openpyxl
from openpyxl.styles import Font

VARIANT_FILE = "webapp/src/main/resources/data/v3_multi_type_variant_file.txt"
XLSX = "webapp/src/main/resources/static/files/hotspots_v3.xlsx"
NEW_SHEET_NAME = "Variant_Samples"


def load_variants():
    """Return OrderedDict (Hugo, Residue) -> list[(ref, alt, composition, count)]."""
    by_residue = OrderedDict()
    seen = set()
    with open(VARIANT_FILE) as f:
        header = f.readline().rstrip("\n").split("\t")
        ci = {h: i for i, h in enumerate(header)}
        for line in f:
            if line in seen:
                continue
            seen.add(line)
            fs = line.rstrip("\n").split("\t")
            key = (fs[ci["Hugo_Symbol"]], fs[ci["Residue"]])
            ref = fs[ci["Reference_Amino_Acid"]]
            alt = fs[ci["Variant_Amino_Acid"]]
            comp = fs[ci["Tumor_Type_Composition"]]
            count = sum(int(p.rsplit(":", 1)[1]) for p in comp.split("|") if ":" in p)
            by_residue.setdefault(key, []).append((ref, alt, comp, count))
    return by_residue


def main():
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    variants = load_variants()
    wb = openpyxl.load_workbook(XLSX)

    # Pull the (Hugo, Codon, Codon_Position) triples from the existing Sheet tab
    # so we only write variants for the 164 new hotspots, in the same order.
    sheet = wb["Sheet"]
    hotspots = []
    for r in range(2, sheet.max_row + 1):
        hugo = sheet.cell(row=r, column=1).value
        codon = sheet.cell(row=r, column=2).value
        pos = sheet.cell(row=r, column=3).value
        if hugo is None:
            continue
        hotspots.append((hugo, codon, int(pos)))

    if NEW_SHEET_NAME in wb.sheetnames:
        del wb[NEW_SHEET_NAME]
    ws = wb.create_sheet(NEW_SHEET_NAME)

    headers = [
        "Hugo_Symbol",
        "Codon",
        "Codon_Position",
        "Reference_Amino_Acid",
        "Variant_Amino_Acid",
        "Mutation_Count",
        "Samples",
    ]
    ws.append(headers)
    for c in ws[1]:
        c.font = Font(bold=True)

    written = 0
    for hugo, codon, pos in hotspots:
        vlist = variants.get((hugo, codon))
        if not vlist:
            raise KeyError(f"No variant rows for {(hugo, codon)}")
        # Sort by count desc, then alt AA for stable output
        for ref, alt, comp, count in sorted(vlist, key=lambda v: (-v[3], v[1])):
            ws.append([hugo, codon, pos, ref, alt, count, comp])
            written += 1

    wb.save(XLSX)
    print(f"Wrote {NEW_SHEET_NAME} with {written} variant rows across {len(hotspots)} codons.")


if __name__ == "__main__":
    main()
