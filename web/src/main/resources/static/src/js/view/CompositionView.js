function CompositionView(options)
{
    var _defaultOpts = {
        // default target DOM element
        el: '#tumor_type_composition_view',
        templateId: '#tumor_type_composition',
        dataTableTarget: ".tumor-type-composition",
        // no data by default, must be provided by the client
        data: {},
        colData: {},
        // default ordering
        order: [[1 , "desc" ]],
        paging: true,
        columns: [
            {title: "Tumor Type",
                data: "type"},
            {title: "Count",
                data: "count"}
        ],
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
        var templateFn = _.template($(_options.templateId).html());
        $(_options.el).html(templateFn(_options.colData));

        var dataTableOpts = {
            sDom: 'st<"composition-paginate"p>',
            paging: _options.paging,
            data: _options.data,
            order: _options.order,
            columns: _options.columns
        };

        $(_options.el).find(_options.dataTableTarget).DataTable(dataTableOpts);
    }

    this.render = render;
}
