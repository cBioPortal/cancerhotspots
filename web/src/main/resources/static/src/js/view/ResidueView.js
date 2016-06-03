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
 * Cancer Hotspots Residue View.
 * Designed to visualize residue data with a DataTable.
 *
 * @param options   view options
 * @author Selcuk Onur Sumer
 */
function ResidueView(options)
{
    var _defaultOpts = {
        // default target DOM element
        el: "#residue_content",
        templateId: "residue_view",
        // no data by default, must be provided by the client
        data: {},
        // delay amount before applying the user entered filter query
        filteringDelay: 500,
        // threshold for pValue, any value below this will be shown as >threshold
        pValueThreshold: 0.001
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    function render()
    {
        var templateFn = _.template($("#" + _options.templateId).html());
        $(_options.el).html(templateFn(_options.data));

        var pValueRender = new PValueRender({
            threshold: _options.pValueThreshold
        });

        var pdbChainsRender = new PdbChainsRender({
            pValueThreshold: _options.pValueThreshold
        });

        var noWrapRender = new NoWrapRender();

        var dataTableOpts = {
            dom: "<'row'<'col-sm-8 residue-table-title'><'col-sm-4'f>>t" +
                 "<'row'<'col-sm-8'i><'col-sm-4 right-align table-button-group'>>",
            paging: false,
            scrollY: "500px",
            scrollCollapse: true,
            language: {
                loadingRecords: '<img src="lib/images/loader.gif"> Loading...'
            },
            order: [[2, "asc"], [1, "desc"]],
            columns: [
                {id: "residues",
                    title: "Residues",
                    data: "residues"},
                {id: "pdbChains",
                    title: "PDB Chains",
                    data: "pdbChains",
                    render: pdbChainsRender.render,
                    createdCell: pdbChainsRender.postRender},
                {id: "pValue",
                    title: noWrapRender.render("P-value"),
                    data: "pValue",
                    render: pValueRender.render}
            ],
            initComplete: function(settings) {
                var dataTable = this;

                // add a delay to the filter
                if (_options.filteringDelay > 0)
                {
                    dataTable.fnSetFilteringDelay(_options.filteringDelay);
                }
            }
        };

        if (_.isFunction(_options.ajax))
        {
            dataTableOpts.ajax = _options.ajax;
        }
        else
        {
            dataTableOpts.data = _options.data.tableData;
        }

        $(_options.el).find("#residue_table").DataTable(dataTableOpts);

        //$("div.residue-table-title").html(
        //    _.template($("#residue_table_info").html())({}));
    }

    this.render = render;

}

