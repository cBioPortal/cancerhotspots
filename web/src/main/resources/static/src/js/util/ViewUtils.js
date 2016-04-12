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
        "acc": {color: "purple"},
        "blca": {color: "yellow"},
        "brca": {color: "hotpink"},
        "cesc": {color: "teal"},
        "chol": {color: "green"},
        "coad": {color: "saddlebrown"},
        "coadread": {color: "saddlebrown"},
        "dlbc": {color: "limegreen"},
        "esca": {color: "lightskyblue"},
        "gbm": {color: "gray"},
        "hnsc": {color: "darkred"},
        "kich": {color: "orange"},
        "kirc": {color: "orange"},
        "kirp": {color: "orange"},
        "laml": {color: "orange"},
        "lgg": {color: "gray"},
        "lihc": {color: "mediumseagreen"},
        "luad": {color: "gainsboro"},
        "lusc": {color: "gainsboro"},
        "meso": {color: "blue"},
        "ov": {color: "lightblue"},
        "paad": {color: "purple"},
        "pcpg": {color: "gray"},
        "prad": {color: "cyan"},
        "read": {color: "saddlebrown"},
        "sarc": {color: "lightyellow"},
        "skcm": {color: "black"},
        "stad": {color: "lightskyblue"},
        "stes": {color: "lightskyblue"},
        "tgct": {color: "red"},
        "thca": {color: "teal"},
        "thym": {color: "purple"},
        "ucec": {color: "peachpuff"},
        "ucs": {color: "peachpuff"},
        "uvm": {color: "green"}
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

    return {
        getDefaultTumorTypeColors: getDefaultTumorTypeColors,
        getDefaultVariantColors: getDefaultVariantColors
    };

})();
