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
        variantRender: function (data) {
            var templateFn = _.template($("#basic_content").html());
            return templateFn({value: _.size(data)});
        },
        variantPostRender: function (td, cellData, rowData, row, col) {
            var target = $(td).find(".basic-content");
            target.empty();

            var stackedBar = new StackedBar({
                el: target
            });

            stackedBar.init(cellData);

            var viewOpts = {
                templateId: '#variant_composition',
                dataTableTarget: ".variant-composition",
                paging: false,
                columns: [
                    {title: "Variant",
                        data: "type"},
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
            sDom: '<".left-align"i>ft',
            data: _options.data,
            paging: false,
            scrollY: "600px",
            scrollCollapse: true,
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
                    createdCell: _options.tumorTypePostRender},
                {title: "Validation Level [a]",
                    data: "validationLevel"}
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

        $(_options.el).DataTable(dataTableOpts);
    }

    this.render = render;
}
