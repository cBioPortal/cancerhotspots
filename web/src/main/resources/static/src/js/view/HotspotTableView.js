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
    function defaultTooltipOpts()
    {
        return {
            content: {text: 'NA'},
            show: {event: 'mouseover'},
            hide: {fixed: true, delay: 100, event: 'mouseout'},
            style: {classes: 'cancer-hotspots-tooltip qtip-shadow qtip-light qtip-rounded'},
            position: {my:'top left', at:'bottom right', viewport: $(window)}
        };
    }

    function tooltipOpts(colData, viewOpts)
    {
        var tooltipOpts = defaultTooltipOpts();

        // this will overwrite the default content
        tooltipOpts.events = {
            render: function(event, api) {
                var tableData = [];

                var defaultViewOpts = {
                    el: $(this).find('.qtip-content'),
                    colData: colData,
                    data: tableData
                };

                var map = colData.composition || colData;

                _.each(_.pairs(map), function(pair) {
                    tableData.push({type: pair[0], count: pair[1]});
                });

                var opts = jQuery.extend(true, {}, defaultViewOpts, viewOpts);
                var tableView = new CompositionView(opts);

                tableView.render();

                // this is a workaround for the misaligned table headers
                // due to the scroll bar feature
                setTimeout(function() {
                    tableView.getDataTable().columns.adjust();
                }, 0);
            }
        };

        return tooltipOpts;
    }

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
        // default rendering function for no-wrap text
        noWrapRender: function(data) {
            var templateFn = _.template($("#no_text_wrap").html());
            return templateFn({text: data});
        },
        // default rendering function for tip enabled text
        sampleRender: function(data) {
            var templateFn = _.template($("#samples_column").html());
            return templateFn(data);
        },
        sampleData: function(row) {
            return {
                tumorCount: row["tumorCount"],
                tumorTypeCount: row["tumorTypeCount"],
                composition: row["tumorTypeComposition"]
            };
        },
        pValueRender: function(data, type) {
            // sort value should be the data value
            if (type === 'sort')
            {
                return data;
            }
            // type == 'display' || 'filter' || 'type'
            else if (data < _options.pValueThreshold)
            {
                return _options.noWrapRender("<" + _options.pValueThreshold);
            }
            else
            {
                return _options.noWrapRender(data);
            }
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
        },
        variantRender: function(data, type) {
            if (type === 'sort')
            {
                return _.size(data);
            }
            else
            {
                var templateFn = _.template($("#basic_content").html());
                return templateFn({value: ""});
            }
        },
        variantTipRender: function(data)
        {
            if (_options.variantColors[data.toString().trim().toUpperCase()] != null)
            {
                var templateFn = _.template($("#variant_cell").html());
                return templateFn({value: data});
            }
            else
            {
                return data;
            }
        },
        variantTipPostRender: function(td, cellData, rowData, row, col) {
            var bgColor = _options.variantColors[cellData.toString().trim().toUpperCase()];

            if (bgColor != null)
            {
                $(td).find(".variant-cell").css({"background-color": bgColor});
            }
        },
        variantTipCountRender: function(data)
        {
            var templateFn = _.template($("#variant_count_cell").html());
            return templateFn({value: data.count});
        },
        variantTipCountPostRender: function(td, cellData, rowData, row, col) {
            var proxy = new VariantDataProxy();
            var gene = cellData.hugoSymbol;
            var aaChange = cellData.residue + rowData.type;
            var helper = cellData.variantHelper;

            proxy.getTumorTypeComposition(gene, aaChange, function(compositionData) {
                var target = $(td).find(".variant-tumor-type-composition");
                target.empty();

                if (compositionData.length > 0)
                {
                    var stackedBar = new StackedBar({
                        el: target,
                        elWidth: helper.scaleFn(rowData.count),
                        elHeight: _options.tooltipStackHeight,
                        disableText: true,
                        // assign a fixed color for each tumor type
                        colors: _options.tumorColors
                    });

                    var tumorTypeComposition = compositionData[0]["tumorTypeComposition"];

                    var tooltipData = {
                        tumorCount: rowData.count,
                        tumorTypeCount: _.size(tumorTypeComposition),
                        composition: tumorTypeComposition
                    };

                    stackedBar.init(tumorTypeComposition);

                    cbio.util.addTargetedQTip($(td).find(".variant-count-cell-content"),
                                              tooltipOpts(tooltipData));
                }
            });

            //$(td).find(".variant-tumor-type-composition-cell").css({"background-color": bgColor});
        },
        variantPostRender: function(td, cellData, rowData, row, col) {
            var target = $(td).find(".basic-content");

            if (_.isEmpty(cellData))
            {
                // nothing to render
                return;
            }

            var stackedBar = new StackedBar({
                el: target,
                // assign a fixed color for each amino acid value
                colors: _options.variantColors
            });

            stackedBar.init(cellData);

            var viewOpts = {
                templateId: '#variant_composition',
                dataTableTarget: ".variant-composition",
                dom: "t",
                columns: [
                    {title: "Variant",
                        data: "type",
                        render: _options.variantTipRender,
                        createdCell: _options.variantTipPostRender},
                    {title: "Count",
                        data: function(data) {
                            return {
                                count: data.count,
                                hugoSymbol: rowData.hugoSymbol,
                                residue: rowData.residue,
                                variantHelper: variantHelper(cellData)
                            };
                        },
                        render: _options.variantTipCountRender,
                        createdCell: _options.variantTipCountPostRender}
                ]
            };

            cbio.util.addTargetedQTip(target.find('svg'),
                                      tooltipOpts(cellData, viewOpts));
        },
        tumorTypePostRender: function (td, cellData, rowData, row, col) {
            if (cellData.tumorCount > 0 &&
                !_.isEmpty(cellData.composition))
            {
                cbio.util.addTargetedQTip($(td).find(".qtipped-text"),
                                          tooltipOpts(cellData));
            }
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
            order: [[3 , "asc" ], [4, "asc"], [5, "desc"]],
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
                {id: "variant",
                    title: "Variant Amino Acid <sup>&#8224;</sup>",
                    data: "variantAminoAcid",
                    render: _options.variantRender,
                    createdCell: _options.variantPostRender},
                {id: "qValue",
                    title: _options.noWrapRender("Q-value"),
                    data: "qValue",
                    render: _options.noWrapRender},
                {id: "pValue",
                    title: _options.noWrapRender("P-value"),
                    data: _options.pValueData,
                    render: _options.pValueRender},
                {id: "sampleCount",
                    title: "Sample Count <sup>&#8224;</sup>",
                    data: _options.sampleData,
                    render: _options.sampleRender,
                    createdCell: _options.tumorTypePostRender}
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
