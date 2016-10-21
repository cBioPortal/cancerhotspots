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
function PdbChainsRender(options)
{
    var _defaultOpts = {
        templateId: "pdb_chains_column",
        linkTemplateId: "cluster_pdb_link",
        threshold: 2,
        pValueThreshold: 0.0001,
        dataManager: false,
        pdbIdData: function(row) {
            return {
                pdbId: row["pdbId"],
                chain: row["chain"]
            };
        }
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    function render(data, type)
    {
        if (type === 'sort')
        {
            return _.size(data);
        }
        else
        {
            // convert map into an array and sort by count
            var pdbChains = MutationUtils.convertToPdbList(data);

            // create an array of display values
            var values = [];
            var linkTemplateFn = _.template($("#" + _options.linkTemplateId).html());

            _.each(_.first(pdbChains, _options.threshold), function(pdbChain) {

                values.push(linkTemplateFn({
                    pdbChain: pdbChain.pdbId + ":" + pdbChain.chain
                }));
            });

            if (_.size(pdbChains) > _options.threshold)
            {
                values.push('and ' + (_.size(pdbChains) - _options.threshold) + ' more');
            }

            var templateFn = _.template($("#" + _options.templateId).html());
            return templateFn({pdbChains: values.join(", ")});
        }

    }

    function postRender(td, cellData, rowData, row, col)
    {
        if (_.isEmpty(cellData))
        {
            // nothing to render
            return;
        }

        var pdbIdRender = new PdbIdRender({
            dataManager: _options.dataManager
        });

        var pValueRender = new DecimalValueRender({
            threshold: _options.pValueThreshold
        });

        var noWrapRender = new NoWrapRender();

        // convert map into a list
        var pdbList = MutationUtils.convertToPdbList(cellData);

        var viewOpts = {
            //templateId: '#pdb_chain_composition',
            //dataTableTarget: ".pdb-chain-composition",
            templateId: '#variant_composition',
            dataTableTarget: ".variant-composition",
            dom: "t",
            data: pdbList,
            order: [[2 , "asc" ], [1, "asc"]],
            columns: [
                {title: "PDB Id",
                    data: _options.pdbIdData,
                    render: pdbIdRender.render,
                    createdCell: pdbIdRender.postRender},
                {title: "Chain",
                    data: "chain"},
                {id: "pValue",
                    title: noWrapRender.render("P-value"),
                    data: "pValue",
                    render: pValueRender.render}
            ]
        };

        addEventListeners(td);

        cbio.util.addTargetedQTip($(td).find('.qtipped-text'),
                                  TooltipUtils.tooltipOptions(cellData, viewOpts));
    }

    function addEventListeners(td)
    {
        var pdbElem = $(td).find(".cluster-pdb");

        pdbElem.on('click', function() {
            // show the 3D vis
            if (_options.dataManager)
            {
                // select the pdb!
                _options.dataManager.selectPdbChain($(this).text());
            }
        });
    }

    this.render = render;
    this.postRender = postRender;
}

