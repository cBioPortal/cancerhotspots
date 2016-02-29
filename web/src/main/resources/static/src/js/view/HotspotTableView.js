function HotspotTableView(options)
{
    function defaultTooltipOpts()
    {
        return {
            content: {text: 'NA'},
            show: {event: 'mouseover'},
            hide: {fixed: true, delay: 100, event: 'mouseout'},
            style: {classes: 'mutation-details-tooltip qtip-shadow qtip-light qtip-rounded'},
            position: {my:'top left', at:'bottom right', viewport: $(window)}
        };
    }

    function tumorTypeTooltipOpts(colData, viewOpts)
    {
        var tooltipOpts = defaultTooltipOpts();

        // this will overwrite the default content
        tooltipOpts.events = {
            render: function(event, api) {
                var tableData = [];

                var defaultViewOpts = {
                    el: $(this).find('.qtip-content'),
                    colData: colData,
                    data: tableData,
                    order: [[0 , "asc" ]]
                };

                _.each(_.pairs(colData.composition), function(pair) {
                    tableData.push({tumorType: pair[0], count: pair[1]});
                });

                var opts = jQuery.extend(true, {}, defaultViewOpts, viewOpts);
                var tableView = new TumorTypeCompositionView(opts);

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
        tumorTypeCompositionTip: function (td, cellData, rowData, row, col) {
            var viewOpts = {};
            cbio.util.addTargetedQTip($(td).find(".qtipped-text"),
                                      tumorTypeTooltipOpts(cellData));
        }
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    function render()
    {
        var dataTableOpts = {
            //sDom: "pftil",
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
                {title: "Variant Amino Acid",
                    data: "variantAminoAcid",
                    render: _options.mapRender},
                {title: _options.noWrapRender("Q-value"),
                    data: "qValue",
                    render: _options.noWrapRender},
                {title: "Sample Count",
                    data: _options.sampleData,
                    render: _options.sampleRender,
                    createdCell: _options.tumorTypeCompositionTip},
                {title: "Validation Level [a]",
                    data: "validationLevel"}
            ]
        };

        $(_options.el).DataTable(dataTableOpts);
    }

    this.render = render;
}
