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
 * Composition View.
 * Designed to display additional information in a tooltip.
 *
 * @param options   view options
 * @author Selcuk Onur Sumer
 */
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
        order: [[1 , "desc" ], [0, "asc"]],
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
