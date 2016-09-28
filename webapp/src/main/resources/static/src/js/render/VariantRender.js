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
 * @author Selcuk Onur Sumer
 */
function VariantRender(options)
{
    var _defaultOpts = {
        templateId: "basic_content",
        variantColors: ViewUtils.getDefaultVariantColors(),
        tumorColors: ViewUtils.getDefaultTumorTypeColors(),
        tooltipStackHeight: 14,
        tooltipStackRange: [4, 150]
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    function render(data, type)
    {
        if (type === 'sort')
        {
            return _.size(data);
        }
        else
        {
            var templateFn = _.template($("#" + _options.templateId).html());
            return templateFn({value: ""});
        }
    }

    function postRender(td, cellData, rowData, row, col) {
        var target = $(td).find(".basic-content");
        var qTipTarget;

        if (_.isEmpty(cellData))
        {
            // nothing to render
            return;
        }

        // do not show stacked bar for indel mutations, only show count
        if (rowData.type != null &&
            rowData.type.toLowerCase() === "indel")
        {
            qTipTarget = target.find(".basic-content-text");
            qTipTarget.text(_.size(cellData));
        }
        // show stacked bar for other mutations
        else
        {
            var stackedBar = new StackedBar({
                el: target,
                // assign a fixed color for each amino acid value
                colors: _options.variantColors
            });

            stackedBar.init(cellData);
            qTipTarget = target.find('svg');
        }

        var variantTipRender = new VariantTipRender({
            variantColors: _options.variantColors
        });

        var variantTipCountRender = new VariantTipCountRender({
            tumorColors: _options.tumorColors,
            tooltipStackHeight: _options.tooltipStackHeight
        });

        var viewOpts = {
            templateId: '#variant_composition',
            dataTableTarget: ".variant-composition",
            dom: "t",
            columns: [
                {title: "Variant",
                    data: "type",
                    render: variantTipRender.render,
                    createdCell: variantTipRender.postRender},
                {title: "Count",
                    data: function(data) {
                        return {
                            count: data.count,
                            hugoSymbol: rowData.hugoSymbol,
                            residue: rowData.residue,
                            variantHelper: variantHelper(cellData)
                        };
                    },
                    render: variantTipCountRender.render,
                    createdCell: variantTipCountRender.postRender}
            ]
        };

        cbio.util.addTargetedQTip(qTipTarget,
                                  TooltipUtils.tooltipOptions(cellData, viewOpts));
    }

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

    this.render = render;
    this.postRender = postRender;
}
