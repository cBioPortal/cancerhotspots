function HotspotTableView(options)
{
    var _defaultOpts = {
        // default target DOM element
        el: "#hotspots_table",
        // default rendering function for map data structure
        mapRender: function(data) {
            var view = [];

            _.each(_.keys(data).sort(), function(key) {

                view.push(key + ":" + data[key]);
            });

            return view.join("<br>");
        }
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    function render(data)
    {
        var dataTableOpts = {
            data: data,
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
                {title: "Q-value",
                    data: "qValue"},
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
