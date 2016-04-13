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
 * Stacked bar implemented with D3.
 *
 * @param options   customization options
 * @author Selcuk Onur Sumer
 */
function StackedBar(options)
{
    var _defaultOpts = {
        el: "#stack_bar", // id of the container
        elWidth: 150,     // width of the container
        elHeight: 20, // height of the container
        barHeight: 20,
        rectBorderColor: "#666666",
        defaultColor: "#ebebeb",
        // an array or a map of colors
        colors: ["#3366cc","#dc3912","#ff9900","#109618",
            "#990099","#0099c6","#dd4477","#66aa00",
            "#b82e2e","#316395","#994499","#22aa99",
            "#aaaa11","#6633cc","#e67300","#8b0707",
            "#651067","#329262","#5574a6","#3b3eac",
            "#b77322","#16d620","#b91383","#f4359e",
            "#9c5935","#a9c413","#2a778d","#668d1c",
            "#bea413","#0c5922","#743411"],
        rectBorderWidth: 0.5,
        rectOpacity: 1,
        threshold: 10,
        disableText: false,
        font: "sans-serif",   // font of the text
        fontColor: "#FFFFFF", // font color of the text
        fontSize: "12px",     // font size of the text
        textAnchor: "middle" // text anchor (alignment) for the label
    };

    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    // reference to the main svg element
    var _svg = null;

    function init(data)
    {
        // selecting using jQuery node to support both string and jQuery selector values
        var node = $(_options.el)[0];
        var container = d3.select(node);

        // create svg element & update its reference
        var svg = createSvg(container,
                            _options.elWidth,
                            _options.elHeight);

        _svg = svg;
        var bounds = {
            x: 0,
            y: _options.elWidth,
            width: _options.elWidth,
            height: _options.barHeight
        };

        var sum = _.reduce(data, function(memo, num) {
            return memo + (num || 0);
        }, 0);

        //var sum = _.size(data);

        drawBarRectangles(svg, data, xScaleFn(bounds, sum), 0);
    }

    /**
     * Creates the main svg element.
     *
     * @param container target container (html element)
     * @param width     width of the svg
     * @param height    height of the svg
     * @return {object} svg instance (D3)
     */
    function createSvg(container, width, height)
    {
        var svg = container.append("svg");

        svg.attr('width', width);
        svg.attr('height', height);

        return svg;
    }

    /**
     * Generates an x-scale function for the current bounds
     * and the max value of the x-axis.
     *
     * @param bounds    bounds of the plot area {width, height, x, y}
     *                  x, y is the actual position of the origin
     * @param max       maximum value for the x-axis
     * @return {function} scale function for the x-axis
     */
    function xScaleFn(bounds, max)
    {
        return d3.scale.linear()
            .domain([0, max])
            .range([bounds.x, bounds.x + bounds.width]);
    }

    /**
     * Draws a group of rectangles for the given model.
     *
     * @param svg       target svg element (D3)
     * @param model     model object (with key,value pairs)
     * @param scaleFn   scale function for the x-axis
     * @param y         y coordinate of the rectangle group
     * @return {object} group for the bar stack (svg element)
     */
    function drawBarRectangles(svg, model, scaleFn, y)
    {
        var gBar = svg.append("g")
            .attr("class", "stacked-bar-group")
            .attr("opacity", 1);

        var height = _options.barHeight;
        var x = 0;
        var dataArray = _.pairs(model);

        dataArray.sort(function (a, b) {
            if (a[1] === b[1]) {
                // sort alphabetically (a-z)
                if (a[0] > b[0])
                    return 1;
                else
                    return -1;
            }
            else {
                // sort descending
                return (b[1] - a[1]) || -1;
            }
        });

        _.each(dataArray, function(pair, idx) {
            var key = pair[0];
            var value = pair[1];

            var width = scaleFn(value);
            var color = _options.defaultColor;

            // assign a different color to each rectangle

            if (_.isObject(_options.colors))
            {
                color = _options.colors[key] || _options.colors.defaultColor;

                if (_options.colors[key] == null)
                {
                    console.log("[warning] no color mapping for: " + key);
                }
            }
            else if (_.isArray(_options.colors))
            {
                color = _options.colors[idx % _options.colors.length];
            }

            // draw a line (instead of a rectangle) for a gap
            //if (segment.type == LINE)
            //{
            //    var line = gBar.append('line')
            //        .attr('stroke', options.rectBorderColor)
            //        .attr('stroke-width', options.rectBorderWidth)
            //        .attr('x1', x)
            //        .attr('y1', y + height/2)
            //        .attr('x2', x + width)
            //        .attr('y2', y + height/2);
            //}

            // draw a rectangle

            var rect = gBar.append('rect')
                .attr('class', "")
                .attr('fill', color)
                .attr('opacity', _options.rectOpacity)
                //.attr('stroke', _options.rectBorderColor)
                //.attr('stroke-width', options.rectBorderWidth)
                .attr('x', x)
                .attr('y', y)
                .attr('width', width)
                .attr('height', height);

            drawStackText(key, gBar, width, x);

            // update x for the next rectangle
            x += width;
        });


        return gBar;
    }

    /**
     * Draws the text for the given svg group (and rectangle).
     * Returns null if the text does not fit into the rectangle.
     *
     * @param label     text contents
     * @param group     target svg group to append the text
     * @param width     width of the target rectangle
     * @param x         x coordinate of the target rectangle
     * @return {object} region text (svg element)
     */
    function drawStackText(label, group, width, x)
    {
        // do not add the text if the width is less than the threshold value
        // or text is disabled
        if (_options.disableText ||
            _options.threshold > width)
        {
            return null;
        }

        x = x || 0.0;

        var xText = x + width/2;
        var height = _options.barHeight;

        if (_options.textAnchor === "start")
        {
            xText = x;
        }
        else if (_options.textAnchor === "end")
        {
            xText = x + width;
        }

        // init text
        var text = group.append('text')
            .style("font-size", _options.fontSize)
            .style("font-family", _options.font)
            .style("font-weight", "bold")
            .text(label)
            .attr("text-anchor", _options.textAnchor)
            .attr("fill", _options.fontColor)
            .attr("x", xText)
            .attr("y", 2*height/3)
            .attr("class", "stacked-bar-text");

        return text;
    }

    this.init = init;
}
