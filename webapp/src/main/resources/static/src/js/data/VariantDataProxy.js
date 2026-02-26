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
 * Designed to retrieve variant data from server.
 *
 * @param options   proxy options
 * @author Selcuk Onur Sumer
 */
function VariantDataProxy(options)
{
    var _defaultOpts = {
        serviceUrl: "api/variants"
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    function getTumorTypeComposition(hugoSymbol, aminoAcidChange, callback)
    {
        var url = _options.serviceUrl + ProxyUtils.requestParams({
            hugoSymbol: hugoSymbol,
            version: "v3"
        });

        var aminoAcidChanges = [].concat(aminoAcidChange);

        // retrieve data from the server
        $.ajax(ProxyUtils.ajaxOpts(url, aminoAcidChanges, callback));
    }

    this.getTumorTypeComposition = getTumorTypeComposition;
}

