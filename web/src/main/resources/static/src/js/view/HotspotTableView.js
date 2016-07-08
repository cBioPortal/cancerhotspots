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
 * Cancer Hotspots Table View.
 * Designed to visualize hotspots data in a DataTable.
 *
 * @param options   view options
 * @author Selcuk Onur Sumer
 */
function HotspotTableView(options)
{
    var _defaultOpts = {
        // default target DOM element
        el: "#hotspots_table",
        metadata: false,
        // no data by default, must be provided by the client
        data: {},
        // delay amount before applying the user entered filter query
        filteringDelay: 500,
        // threshold for pValue, any value below this will be shown as >threshold
        pValueThreshold: 0.0001,
        variantColors: ViewUtils.getDefaultVariantColors(),
        tumorColors: ViewUtils.getDefaultTumorTypeColors(),
        tooltipStackHeight: 14,
        tooltipStackRange: [4, 150],
        paging: true,
        pageLength: 20,
        lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "All"]],
        //scrollY: "500px",
        //scrollCollapse: true,
        scrollY: false,
        scrollCollapse: false,
        renderer: {
            noWrap: {},
            variant: {
                variantColors: ViewUtils.getDefaultVariantColors(),
                tumorColors: ViewUtils.getDefaultTumorTypeColors(),
                tooltipStackHeight: 14,
                tooltipStackRange: [4, 150]
            },
            pValue: {
                threshold: 0.0001
            },
            tumorCount: {},
            classification: {},
            gene: {},
            residue: {}
        },
        // default rendering function for map data structure
        mapRender: function(data) {
            var view = [];

            _.each(_.keys(data).sort(), function(key) {

                view.push(key + ":" + data[key]);
            });

            return view.join("<br>");
        },
        sampleData: function(row) {
            return {
                tumorCount: row["tumorCount"],
                tumorTypeCount: row["tumorTypeCount"],
                composition: row["tumorTypeComposition"]
            };
        },
        clusterData: function(row) {
            return {
                clusterCount: row["clusterCount"],
                hugoSymbol: row["hugoSymbol"],
                residue: row["residue"]
            };
        },
        residueData: function(row) {
            return {
                hugoSymbol: row["hugoSymbol"],
                residue: row["residue"],
                classification: row["classification"]
            };
        },
        pValueData: function(row) {
            var data = row["pValue"];

            // if no pValue field exists then extract it from the clusters
            if (data == null)
            {
                var map = {};

                var clusters = row["clusters"];
                var pValues = _.map(clusters, function(cluster) {
                    var pValue = parseFloat(cluster.pValue);
                    map[pValue] = cluster.pValue;
                    return pValue;
                });

                data = map[_.min(pValues)];
            }

            return data;
        }
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    function variantHelper(variantAminoAcid)
    {
        var values = _.values(variantAminoAcid);
        var max = _.max(values);
        var scaleFn = d3.scale.linear()
            .domain([0, max])
            .range(_options.tooltipStackRange);

        return {
            scaleFn: scaleFn
        };
    }

    function render()
    {
        // TODO allow customization of renderer instances
        var noWrapRender = new NoWrapRender(_options.renderer.noWrap);
        var pValueRender = new PValueRender(_options.renderer.pValue);
        var variantRender = new VariantRender(_options.renderer.variant);
        var tumorCountRender = new TumorCountRender(_options.renderer.tumorCount);
        var classRender = new ClassificationRender(_options.renderer.classification);
        var residueRender = new ResidueRender(_options.renderer.residue);
        var geneRender = new GeneRender(_options.renderer.gene);

        //var clustersRender = new ClustersRender({
        //    pValueThreshold: _options.pValueThreshold
        //});

        var dataTableOpts = {
            //sDom: '<"hotspot-table-controls"f>ti',
            //dom: '<".left-align"i>ft<".right-align"B>',
            //dom: "<'row'<'col-sm-2'B><'col-sm-6 center-align'i><'col-sm-4'f>>t",
            dom: "<'row'<'col-sm-8 hotspot-table-title'><'col-sm-4'f>>t" +
                 "<'row'<'col-sm-6'i><'col-sm-6 right-align'p>>" +
                 "<'row'<'col-sm-6'l><'col-sm-6 right-align table-button-group'B>>",
            paging: _options.paging,
            pageLength: _options.pageLength,
            lengthMenu: _options.lengthMenu,
            deferRender: true,
            scrollY: _options.scrollY,
            scrollCollapse: _options.scrollCollapse,
            language: {
                loadingRecords: '<img src="lib/images/loader.gif"> Loading...',
                lengthMenu: "Show _MENU_ mutations per page",
                info: "Showing _START_ to _END_ of _TOTAL_ mutations",
                infoFiltered: "(filtered from _MAX_ total mutations)"
            },
            //order: [[4 , "asc" ], [5, "asc"], [6, "desc"]],
            // do not sort the table by default, use the initial ordering
            order: [],
            buttons: [{
                text: "Download",
                className: "btn-sm",
                action: function(e, dt, node, config) {
                    // get the file data (formatted by 'fnCellRender' function)
                    //var content = this.fnGetTableData(oConfig);
                    var columns = [
                        {title: "Gene",
                            data: "hugoSymbol"},
                        {title: "Residue",
                            data: "residue"},
                        //{title: "Alt Common Codon Usage *",
                        //    data: "altCommonCodonUsage"},
                        {title: "Variants",
                            data: "variantAminoAcid"},
                        {title: "Q-value",
                            data: "qValue"},
                        {title: "P-value",
                            data: _options.pValueData},
                        {title: "Samples",
                            data: "tumorCount"},
                        {title: "Tumor Type Composition",
                            data: "tumorTypeComposition"}
                        //{title: "Validation Level [a]",
                        //    data: "validationLevel"}
                    ];

                    var dataUtils = new DataUtils(columns);
                    var content = dataUtils.stringify(dt.rows({filter: 'applied'}).data());

                    var downloadOpts = {
                        filename: "cancer_hotspots.txt",
                        contentType: "text/plain;charset=utf-8",
                        preProcess: false
                    };

                    // send download request with filename & file content info
                    cbio.download.initDownload(content, downloadOpts);
                }
            }],
            columns: [
                {id: "hugoSymbol",
                    title: "Gene",
                    data: "hugoSymbol",
                    render: geneRender.render},
                {id: "residue",
                    title: "Residue",
                    type: "num",
                    data: _options.residueData,
                    render: residueRender.render},
                //{id: "altCodon",
                //    title: "Alt Common Codon Usage *",
                //    data: "altCommonCodonUsage"},
                //{id: "clusters",
                //    title: "3D Clusters",
                //    data: _options.clusterData,
                //    render: clustersRender.render},
                {id: "classification",
                    title: "Class",
                    data: "classification",
                    render: classRender.render},
                {id: "variant",
                    title: "Variants <sup>&#8224;</sup>",
                    data: "variantAminoAcid",
                    type: "num",
                    render: variantRender.render,
                    createdCell: variantRender.postRender},
                {id: "qValue",
                    title: noWrapRender.render("Q-value"),
                    data: "qValue",
                    render: noWrapRender.render},
                {id: "pValue",
                    title: noWrapRender.render("P-value"),
                    data: _options.pValueData,
                    render: pValueRender.render},
                {id: "sampleCount",
                    title: "Samples <sup>&#8224;</sup>",
                    data: _options.sampleData,
                    render: tumorCountRender.render,
                    createdCell: tumorCountRender.postRender}
                //{id: "validationLevel"
                //    title: "Validation Level [a]",
                //    data: "validationLevel"}
            ],
            initComplete: function(settings) {
                var dataTable = this;

                // add a delay to the filter
                if (_options.filteringDelay > 0)
                {
                    dataTable.fnSetFilteringDelay(_options.filteringDelay);

                    // alternative method for filter delaying
                    // (https://datatables.net/reference/api/%24.fn.dataTable.util.throttle%28%29)

                    //var search = $.fn.dataTable.util.throttle(
                    //    function (val) {
                    //        dataTable.search(val).draw();
                    //    },
                    //    _options.filteringDelay
                    //);
                    //
                    //$('.hotspot-table-controls input').off('keyup search input')
                    //    .on('keyup search input', function () {
                    //    search(this.value);
                    //});
                }
            }
        };

        ViewUtils.determineVisibility(dataTableOpts.columns, _options.metadata);

        if (_.isFunction(_options.ajax))
        {
            dataTableOpts.ajax = _options.ajax;
        }
        else
        {
            dataTableOpts.data = _options.data;
        }

        $(_options.el).DataTable(dataTableOpts);

        //$("div.single-residue-title").html(
        //    _.template($("#single_residue_title").html())({}));

        $("div.hotspot-table-title").html(
            _.template($("#hotspot_table_title").html())({}));
    }

    this.render = render;
}
