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
function CancerHotspots(options)
{
    // TODO defaultOpts

    function switchContent(routeFn, params)
    {
        $("#page_content").children().hide();

        // TODO show a loader image here!!!

        setTimeout(function() {
            routeFn(params);

            // TODO and hide the loader image here!!!
        }, 50);
    }

    function home()
    {
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

        // init section if not initialized yet
        if (!$("#home").length)
        {
            var templateFn = _.template($("#home_page").html());
            $("#page_content").append(templateFn());

            // initial AJAX call to determine which profile is active
            var metadataProxy = new MetadataProxy();

            metadataProxy.getMetadata(function(data) {
                //initWithData(data);
                initWithAjax(data);
            });
        }

        $("#home").show();
    }

    function about()
    {
        // init section if not initialized yet
        if (!$("#about").length)
        {
            var templateFn = _.template($("#about_page").html());
            $("#page_content").append(templateFn());
        }

        $("#about").show();
    }

    function cluster(params)
    {
        var proxy = new ClusterDataProxy();

        // init section if not initialized yet
        if (!$("#residue").length)
        {
            var templateFn = _.template($("#residue_page").html());
            $("#page_content").append(templateFn());
        }

        $("#residue").show();

        proxy.getCluster(params.hugoSymbol, params.residue, function(data) {
            var clusterData = {
                tableData: data,
                gene: params.hugoSymbol,
                residue: params.residue
            };

            var residueView = new ResidueView({
                data: clusterData
                //TODO pValueThreshold: _options.pValueThreshold
            });

            residueView.render();
        });
    }

    function init()
    {
        // init router
        var router = new Router({
            '/home': function() {
                switchContent(home);
            },
            '/about': function() {
                switchContent(about);
            },
            '/residue/:hugoSymbol/:residue': function(hugoSymbol, residue) {
                switchContent(cluster, {hugoSymbol: hugoSymbol, residue: residue});
            }
        });

        router.configure({notfound: function() {
            // TODO switch to the not found page! (a static error page)
            //switchContent(unknown);
        }});

        // load home page content initially
        router.init("/home");
    }

    this.init = init;
}
