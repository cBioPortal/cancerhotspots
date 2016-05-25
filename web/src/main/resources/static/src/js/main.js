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
 * Main flow starts here
 *
 * @author Selcuk Onur Sumer
 */
$(document).ready(function() {
    function initWithData(metadata)
    {
        var proxy = new HotspotDataProxy();

        // get all hotspot data
        proxy.getAllHotspots(function(data) {
            var mainTemplateFn = _.template($("#main_view").html());
            $("#main_content").html(mainTemplateFn());

            // init the table view with the hotspot data
            var tableView = new HotspotTableView({
                metadata: metadata,
                data: data
            });

            // render the table
            tableView.render();
        });
    }

    function initWithAjax(metadata)
    {
        var mainTemplateFn = _.template($("#main_view").html());
        $("#main_content").html(mainTemplateFn());

        // init the table view with the hotspot data retrieval function
        var tableView = new HotspotTableView({
            metadata: metadata,
            "ajax": function (data, callback, settings) {
                var proxyOptions = {};

                if (metadata.profile.toLowerCase() === "3d")
                {
                    proxyOptions.serviceUrl = "api/hotspots/3d";
                }

                var proxy = new HotspotDataProxy(proxyOptions);

                proxy.getAllHotspots(function(hotspotData) {
                    // defer rendering of the table a few miliseconds
                    // for a smoother rendering of the loader
                    setTimeout(function(){
                        callback({data: hotspotData});
                    }, 500);
                });
            }
        });

        // render the table
        tableView.render();
    }

    // TODO initial AJAX call to determine which data fields are available
    // this way we can hide/show columns when we initialize the data table

    var metadataProxy = new MetadataProxy();

    metadataProxy.getMetadata(function(data) {
        //initWithData(data);
        initWithAjax(data);
    });
});
