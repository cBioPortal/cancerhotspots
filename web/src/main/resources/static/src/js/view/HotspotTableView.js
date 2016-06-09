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
        pValueThreshold: 0.001,
        variantColors: ViewUtils.getDefaultVariantColors(),
        tumorColors: ViewUtils.getDefaultTumorTypeColors(),
        tooltipStackHeight: 14,
        tooltipStackRange: [4, 150],
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
        var noWrapRender = new NoWrapRender();

        var pValueRender = new PValueRender({
            threshold: _options.pValueThreshold
        });

        var variantRender = new VariantRender({
            variantColors: _options.variantColors,
            tumorColors: _options.tumorColors,
            tooltipStackHeight: _options.tooltipStackHeight,
            tooltipStackRange: _options.tooltipStackRange
        });

        var tumorCountRender = new TumorCountRender();

        var clustersRender = new ClustersRender({
            pValueThreshold: _options.pValueThreshold
        });

        var dataTableOpts = {
            //sDom: '<"hotspot-table-controls"f>ti',
            //dom: '<".left-align"i>ft<".right-align"B>',
            //dom: "<'row'<'col-sm-2'B><'col-sm-6 center-align'i><'col-sm-4'f>>t",
            dom: "<'row'<'col-sm-8 hotspot-table-title'><'col-sm-4'f>>t" +
                 "<'row'<'col-sm-8'i><'col-sm-4 right-align table-button-group'B>>",
            paging: false,
            scrollY: "500px",
            scrollCollapse: true,
            language: {
                loadingRecords: '<img src="lib/images/loader.gif"> Loading...'
            },
            order: [[5 , "asc" ], [6, "asc"], [7, "desc"]],
            buttons: [{
                text: "Download",
                className: "btn-sm",
                action: function(e, dt, node, config) {
                    // get the file data (formatted by 'fnCellRender' function)
                    //var content = this.fnGetTableData(oConfig);
                    var columns = [
                        {title: "Hugo Symbol",
                            data: "hugoSymbol"},
                        {title: "Residue",
                            data: "residue"},
                        //{title: "Alt Common Codon Usage *",
                        //    data: "altCommonCodonUsage"},
                        {title: "Variant Amino Acid",
                            data: "variantAminoAcid"},
                        {title: "Q-value",
                            data: "qValue"},
                        {title: "P-value",
                            data: _options.pValueData},
                        {title: "Sample Count",
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
                    title: "Hugo Symbol",
                    data: "hugoSymbol"},
                {id: "residue",
                    title: "Residue",
                    data: "residue"},
                //{id: "altCodon",
                //    title: "Alt Common Codon Usage *",
                //    data: "altCommonCodonUsage"},
                {id: "clusters",
                    title: "3D Clusters",
                    data: "clusterCount",
                    render: clustersRender.render,
                    createdCell: clustersRender.postRender},
                {id: "classification",
                    title: "Class",
                    data: "classification"},
                {id: "variant",
                    title: "Variant Amino Acid <sup>&#8224;</sup>",
                    data: "variantAminoAcid",
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
                    title: "Sample Count <sup>&#8224;</sup>",
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
            _.template($("#table_hover_info").html())({}));
    }

    this.render = render;
}
