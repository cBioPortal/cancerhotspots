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
 * @author Selcuk Onur Sumer
 */
function ClassificationRender(options)
{
    var _defaultOpts = {
        templateId: "span_class",
        displayValue: {
            "LL": "L<sub>L</sub>",
            "LH": "L<sub>H</sub>",
            "Cluster-exclusive": "Not coupled to hotspot",
            "Hotspot-linked": "Coupled to hotspot",
            "Hotspot": "Single-residue hotspot"
        },
        tooltipValue: {
            "Cluster-exclusive": "3D cluster residue NOT coupled to a single-residue hotspot",
            "Hotspot-linked": "3D cluster residue coupled to a single-residue hotspot",
            "Hotspot": "Single-residue hotspot"
        },
        style: {}
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    function render(data, type)
    {
        if (data == null) {
            return "";
        }

        var value = data.value || data;
        var classification = data.classification || data;

        var templateFn = _.template($("#" + _options.templateId).html());
        var display = _options.displayValue[value] || value;
        var style = _options.style[classification] ||
                    ViewUtils.getClassStyle(classification) ||
                    classification || "";

        return templateFn({display: display, style: style});
    }

    function postRender(td, cellData, rowData, row, col)
    {
        var tooltipOpts = TooltipUtils.defaultTooltipOpts();
        tooltipOpts.content = _options.tooltipValue[cellData];

        if (!_.isEmpty(tooltipOpts.content))
        {
            cbio.util.addTargetedQTip(
                $(td).find('.hotspot-classification'), tooltipOpts);
        }
    }

    this.render = render;
    this.postRender = postRender;
}
