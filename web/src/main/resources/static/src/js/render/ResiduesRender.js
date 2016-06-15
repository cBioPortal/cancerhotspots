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
function ResiduesRender(options)
{
    var _defaultOpts = {
        templateId: "residues_column"
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    function render(data, type)
    {
        if (type === 'sort')
        {
            return _.size(data.residues);
        }
        else
        {
            var residues = [];

            // convert map into an array and sort by count
            _.each(_.keys(data.residues), function(residue) {
                residues.push({residue: residue, count: data.residues[residue]});
            });

            residues = _.sortBy(residues, function(residue) {
                // sort by residue position
                var matched = residue.residue.match(/[0-9]+/g);

                if (matched && matched.length > 0) {
                    return parseInt(matched[0]);
                }
                else {
                    return residue.residue;
                }
            });

            // create an array of display values
            var values = [];

            _.each(residues, function(residue) {
                // highlight the current residue
                if (residue.residue === data.residue) {
                    values.push('<b>' + residue.residue + '</b>');
                }
                else {
                    values.push(residue.residue);
                }
            });

            var templateFn = _.template($("#" + _options.templateId).html());
            return templateFn({residues: values.join(", ")});
        }
    }

    function postRender(td, cellData, rowData, row, col)
    {
        if (_.isEmpty(cellData))
        {
            // nothing to render
            return;
        }

        // convert map into a list
        var residueList = [];

        _.each(_.pairs(cellData.residues), function(parts) {
            residueList.push({
                residue: parts[0],
                sampleCount: parts[1]
            });
        });

        var viewOpts = {
            //templateId: '#residues_composition',
            //dataTableTarget: ".residues-composition",
            templateId: '#variant_composition',
            dataTableTarget: ".variant-composition",
            dom: "t",
            data: residueList,
            order: [[1 , "desc" ], [0, "asc"]],
            columns: [
                {title: "Residue",
                    data: "residue",
                    render: function render(data, type) {
                        if (data === cellData.residue) {
                            return '<b>' + data + '</b>';
                        }
                        else {
                            return data;
                        }
                    }},
                {title: "Mutations",
                    data: "sampleCount"}
            ]
        };

        cbio.util.addTargetedQTip($(td).find('.qtipped-text'),
                                  TooltipUtils.tooltipOptions(cellData, viewOpts));
    }

    this.render = render;
    this.postRender = postRender;
}
