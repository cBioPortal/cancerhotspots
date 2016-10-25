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
function DecimalValueRender(options)
{
    var _defaultOpts = {
        threshold: 0,
        digits: 4,
        precision: 0
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    function render (data, type)
    {
        var noWrapRender = new NoWrapRender();
        var value = Number(data);

        // sort value should be the data value
        if (type === 'sort')
        {
            return data;
        }
        // type == 'display' || 'filter' || 'type'
        else if (data < _options.threshold)
        {
            if (_options.precision > 0) {
                if (value > 0) {
                    value = value.toPrecision(_options.precision);
                }
            }
            else {
                value = "<" + _options.threshold;
            }

            return noWrapRender.render(value);
        }
        else
        {
            if (data == null || _.isNaN(value)) {
                value = data;
            }
            else {
                value = value.toFixed(_options.digits);
            }

            return noWrapRender.render(value);
        }
    }


    this.render = render;
}
