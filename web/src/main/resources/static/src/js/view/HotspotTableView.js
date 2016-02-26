function HotspotTableView(options)
{
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
        // default rendering function for map data structure
        noWrapRender: function(data) {
            var templateFn = _.template($("#no_text_wrap").html());
            return templateFn({text: data});
        }
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    function render()
    {
        var dataTableOpts = {
            //sDom: "pftil",
            data: _options.data,
            columns: [
                {title: "Hugo Symbol",
                    data: "hugoSymbol"},
                {title: "Codon",
                    data: "codon"},
                {title: "Alt Common Codon Usage *",
                    data: "altCommonCodonUsage"},
                {title: "Variant Amino Acid",
                    data: "variantAminoAcid",
                    render: _options.mapRender},
                {title: _options.noWrapRender("Q-value"),
                    data: "qValue",
                    render: _options.noWrapRender},
                {title: "Tumor Count",
                    data: "tumorCount"},
                {title: "Tumor Type Count",
                    data: "tumorTypeCount"},
                {title: "Validation Level [a]",
                    data: "validationLevel"},
                {title: "Tumor Type Composition",
                    data: "tumorTypeComposition",
                    render: _options.mapRender}
            ]
        };

        $(_options.el).DataTable(dataTableOpts);
    }

    this.render = render;
}
