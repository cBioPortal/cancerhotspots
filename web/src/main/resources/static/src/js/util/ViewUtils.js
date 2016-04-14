/*
 * Copyright (c) 2016 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal Cancer Hotspots.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @author Selcuk Onur Sumer
 */
var ViewUtils = (function() {

    var _tumorType = {
        "all": {color: "", name: ""},
        "acc": {color: "purple", name: "Adrenocortical Carcinoma"},
        "acyc": {color: "darkred", name: "Adenoid Cystic Carcinoma"},
        "blca": {color: "yellow", name: "Bladder Urothelial Carcinoma"},
        "brca": {color: "hotpink", name: "Invasive Breast Carcinoma"},
        "cesc": {color: "teal", name: "Cervical Squamous Cell Carcinoma"},
        "chol": {color: "green", name: "Cholangiocarcinoma"},
        "cll": {color: "lightsalmon", name: "Chronic Lymphocytic Leukemia"},
        "coad": {color: "saddlebrown", name: "Colon Adenocarcinoma"},
        "coadread": {color: "saddlebrown", name: "Colorectal Adenocarcinoma"},
        "cscc": {color: "black", name: "Cutaneous Squamous Cell Carcinoma"},
        "dlbc": {color: "limegreen", name: "Diffuse Large B-Cell Lymphoma"},
        "esca": {color: "lightskyblue", name: "Esophageal Adenocarcinoma"},
        "gbc": {color: "green", name: "Gallbladder Cancer"},
        "gbm": {color: "gray", name: "Glioblastoma Multiforme"},
        "hgg": {color: "gray", name: "High-Grade Glioma"},
        "hnsc": {color: "darkred", name: "Head and Neck Squamous Cell Carcinoma"},
        "kich": {color: "orange", name: ""},
        "kirc": {color: "orange", name: ""},
        "kirp": {color: "orange", name: ""},
        "laml": {color: "orange", name: ""},
        "lgg": {color: "gray", name: "Low-Grade Glioma"},
        "lihc": {color: "mediumseagreen", name: ""},
        "luad": {color: "gainsboro", name: "Lung Adenocarcinoma"},
        "lusc": {color: "gainsboro", name: "Lung Squamous Cell Carcinoma"},
        "lusm": {color: "", name: ""},
        "lymbc": {color: "", name: ""},
        "mbl": {color: "gray", name: "Medulloblastoma"},
        "mcl": {color: "limegreen", name: "Mantle Cell Lymphoma"},
        "mds": {color: "lightsalmon", name: "Myelodysplasia"},
        "meso": {color: "blue", name: ""},
        "mmyl": {color: "", name: ""},
        "nbl": {color: "gray", name: "Neuroblastoma"},
        "npc": {color: "darkred", name: "Nasopharyngeal Carcinoma"},
        "ov": {color: "lightblue", name: "Ovarian Cancer"},
        "paad": {color: "purple", name: "Pancreatic Adenocarcinoma"},
        "pcpg": {color: "gray", name: "Miscellaneous Neuroepithelial Tumor"}, // ?
        "pias": {color: "", name: ""},
        "prad": {color: "cyan", name: "Prostate Adenocarcinoma"},
        "read": {color: "saddlebrown", name: "Rectal Adenocarcinoma"},
        "sarc": {color: "lightyellow", name: "Soft Tissue"}, // ?
        "skcm": {color: "black", name: "Cutaneous Melanoma"},
        "stad": {color: "lightskyblue", name: "Stomach Adenocarcinoma"},
        "stes": {color: "lightskyblue", name: "Esophagus / Stomach"}, // ?
        "tgct": {color: "lightyellow", name: "Tenosynovial Giant Cell Tumor Diffuse Type"},
        "thca": {color: "teal", name: ""},
        "thym": {color: "purple", name: "Thymoma"},
        "ucec": {color: "peachpuff", name: "Endometrial Carcinoma"},
        "ucs": {color: "peachpuff", name: "Uterine Carcinosarcoma / Uterine Malignant Mixed Mullerian Tumor"},
        "uvm": {color: "green", name: "Uveal Melanoma"}
    };

    var _variantType = {
        "A": {color: "#3366cc"},
        "R": {color: "#dc3912"},
        "N": {color: "#dc3912"},
        "D": {color: "#ff9900"},
        "B": {color: "#109618"},
        "C": {color: "#990099"},
        "E": {color: "#0099c6"},
        "Q": {color: "#dd4477"},
        "Z": {color: "#66aa00"},
        "G": {color: "#b82e2e"},
        "H": {color: "#316395"},
        "I": {color: "#994499"},
        "L": {color: "#22aa99"},
        "K": {color: "#aaaa11"},
        "M": {color: "#6633cc"},
        "F": {color: "#e67300"},
        "P": {color: "#8b0707"},
        "S": {color: "#651067"},
        "T": {color: "#329262"},
        "W": {color: "#5574a6"},
        "Y": {color: "#3b3eac"},
        "V": {color: "#b77322"},
        "X": {color: "#16d620"},
        "*": {color: "#090303"}
    };

    function getDefaultTumorTypeColors()
    {
        var map = {};

        _.each(_.keys(_tumorType), function (key) {
            map[key] = _tumorType[key].color;
        });

        return map;
    }

    function getDefaultVariantColors()
    {
        var map = {};

        _.each(_.keys(_variantType), function (key) {
            map[key] = _variantType[key].color;
        });

        return map;
    }

    function getTumorTypeNames()
    {
        var map = {};

        _.each(_.keys(_tumorType), function (key) {
            map[key] = _tumorType[key].name;
        });

        return map;
    }

    return {
        getTumorTypeNames: getTumorTypeNames,
        getDefaultTumorTypeColors: getDefaultTumorTypeColors,
        getDefaultVariantColors: getDefaultVariantColors
    };

})();
