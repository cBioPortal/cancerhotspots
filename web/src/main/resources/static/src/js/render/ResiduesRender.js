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
        templateId: "residues_column",
        linkTemplateId: "cluster_residue_link",
        dataManager: false
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
                residues.push({residue: residue,
                    count: data.residues[residue],
                    classification: data.classifications[residue]});
            });

            residues = _.sortBy(residues, MutationUtils.defaultResidueSortValue);

            // create an array of display values
            var values = [];

            _.each(residues, function(residue) {
                var templateFn = _.template($("#" + _options.linkTemplateId).html());
                var vars = {
                    gene: data.gene,
                    style: residue.classification
                };

                var value;
                // highlight the current residue
                if (residue.residue === data.residue) {
                    vars.residue = '<b>' + residue.residue + '</b>';
                }
                else {
                    vars.residue = residue.residue;
                }

                value = templateFn(vars);
                values.push(value);
            });

            var templateFn = _.template($("#" + _options.templateId).html());
            return templateFn({residues: values.join(" ")});
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

        // add tooltip for the entire cell
        cbio.util.addTargetedQTip($(td).find('.qtipped-text'),
                                  TooltipUtils.tooltipOptions(cellData, viewOpts));

        addEventListeners(td, cellData);
    }

    function addEventListeners(td, cellData)
    {
        var residueElem = $(td).find(".cluster-residue");

        residueElem.on('mouseenter', function() {
            // highlight the residue!
            if (_options.dataManager)
            {
                _options.dataManager.highlightResidues([$(this).text()]);
            }
        });

        residueElem.on('mouseleave', function() {
            // remove highlights!
            if (_options.dataManager)
            {
                _options.dataManager.unHighlightResidues([$(this).text()]);
            }
        });

        residueElem.on('click', function() {
            // select the residue! TODO selection disabled for now
            if (_options.dataManager)
            {
                //_options.dataManager.selectResidues([$(this).text()]);
            }
        });
    }

    this.render = render;
    this.postRender = postRender;
}

