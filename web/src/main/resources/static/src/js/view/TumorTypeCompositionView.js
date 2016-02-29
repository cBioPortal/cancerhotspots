function TumorTypeCompositionView(options)
{
    var _defaultOpts = {
        // default target DOM element
        el: '#tumor_type_composition_view',
        // no data by default, must be provided by the client
        data: {},
        colData: {},
        // default ordering
        order: [[0 , "asc" ]],
        // default rendering function for map data structure
        noWrapRender: function(data) {
            var templateFn = _.template($('#no_text_wrap').html());
            return templateFn({text: data});
        }
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    function render()
    {
        var templateFn = _.template($('#tumor_type_composition').html());
        $(_options.el).html(templateFn(_options.colData));

        var dataTableOpts = {
            sDom: "stp",
            data: _options.data,
            order: _options.order,
            columns: [
                {title: "Tumor Type",
                    data: "tumorType"},
                {title: "Count",
                    data: "count"}
            ]
        };

        $(_options.el).find('.tumor-type-composition').DataTable(dataTableOpts);
    }

    this.render = render;
}
