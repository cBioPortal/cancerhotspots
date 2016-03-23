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

                tableView.render()
            }
        };

        return tooltipOpts;
    }

    var _defaultOpts = {
        // default target DOM element
        el: "#hotspots_table",
        // no data by default, must be provided by the client
        data: {},
        // delay amount before applying the user entered filter query
        filteringDelay: 500,
        variantColors: {
            "A": "#3366cc",
            "R": "#dc3912",
            "N": "#dc3912",
            "D": "#ff9900",
            "B": "#109618",
            "C": "#990099",
            "E": "#0099c6",
            "Q": "#dd4477",
            "Z": "#66aa00",
            "G": "#b82e2e",
            "H": "#316395",
            "I": "#994499",
            "L": "#22aa99",
            "K": "#aaaa11",
            "M": "#6633cc",
            "F": "#e67300",
            "P": "#8b0707",
            "S": "#651067",
            "T": "#329262",
            "W": "#5574a6",
            "Y": "#3b3eac",
            "V": "#b77322",
            "X": "#16d620",
            "*": "#090303"
        },
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
        variantRender: function(data) {
            var templateFn = _.template($("#basic_content").html());
            return templateFn({value: _.size(data)});
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
        variantPostRender: function(td, cellData, rowData, row, col) {
            var target = $(td).find(".basic-content");
            target.empty();

            var stackedBar = new StackedBar({
                el: target,
                // assign a fixed color for each amino acid value
                colors: _options.variantColors
            });

            stackedBar.init(cellData);

            var viewOpts = {
                templateId: '#variant_composition',
                dataTableTarget: ".variant-composition",
                paging: false,
                columns: [
                    {title: "Variant",
                        data: "type",
                        render: _options.variantTipRender,
                        createdCell: _options.variantTipPostRender},
                    {title: "Count",
                        data: "count"}
                ]
            };

            cbio.util.addTargetedQTip(target.find('svg'),
                                      tooltipOpts(cellData, viewOpts));
        },
        tumorTypePostRender: function (td, cellData, rowData, row, col) {
            cbio.util.addTargetedQTip($(td).find(".qtipped-text"),
                                      tooltipOpts(cellData));
        }
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    function render()
    {
        var dataTableOpts = {
            //sDom: '<"hotspot-table-controls"f>ti',
            //dom: '<".left-align"i>ft<".right-align"B>',
            //dom: "<'row'<'col-sm-2'B><'col-sm-6 center-align'i><'col-sm-4'f>>t",
            dom: "<'row'<'col-sm-8 single-residue-title'><'col-sm-4'f>>t" +
                 "<'row'<'col-sm-8'i><'col-sm-4 right-align table-button-group'B>>",
            paging: false,
            scrollY: "500px",
            scrollCollapse: true,
            language: {
                loadingRecords: '<img src="lib/images/loader.gif"> Loading...'
            },
            order: [[3 , "asc" ], [4, "desc"]],
            buttons: [{
                text: "Download",
                className: "btn-sm",
                action: function(e, dt, node, config) {
                    // get the file data (formatted by 'fnCellRender' function)
                    //var content = this.fnGetTableData(oConfig);
                    var columns = [
                        {title: "Hugo Symbol",
                            data: "hugoSymbol"},
                        {title: "Codon",
                            data: "codon"},
                        //{title: "Alt Common Codon Usage *",
                        //    data: "altCommonCodonUsage"},
                        {title: "Variant Amino Acid",
                            data: "variantAminoAcid"},
                        {title: "Q-value",
                            data: "qValue"},
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
                {title: "Hugo Symbol",
                    data: "hugoSymbol"},
                {title: "Codon",
                    data: "codon"},
                //{title: "Alt Common Codon Usage *",
                //    data: "altCommonCodonUsage"},
                {title: "Variant Amino Acid <sup>&#8224;</sup>",
                    data: "variantAminoAcid",
                    render: _options.variantRender,
                    createdCell: _options.variantPostRender},
                {title: _options.noWrapRender("Q-value"),
                    data: "qValue",
                    render: _options.noWrapRender},
                {title: "Sample Count <sup>&#8224;</sup>",
                    data: _options.sampleData,
                    render: _options.sampleRender,
                    createdCell: _options.tumorTypePostRender}
                //{title: "Validation Level [a]",
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

        if (_.isFunction(_options.ajax))
        {
            dataTableOpts.ajax = _options.ajax;
        }
        else
        {
            dataTableOpts.data = _options.data;
        }

        $(_options.el).DataTable(dataTableOpts);

        $("div.single-residue-title").html(
            _.template($("#single_residue_title").html())({}));
    }

    this.render = render;
}
