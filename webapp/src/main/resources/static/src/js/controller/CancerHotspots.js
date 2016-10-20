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
    var _hotspotProxy = null;
    var _clusterProxy = null;
    var _metadataProxy = null;

    var _defaultOpts = {
        pageLoaderDelay: 50,
        tableLoaderDelay: 500,
        appContent: "#app_content",
        appTemplateId: "main_page",
        pageContent: "#page_content",
        pageLoader: "#page_loader",
        mainView: "#main_view",
        mainContent: "#main_content",
        homePage: "#home",
        homeTemplateId: "home_page",
        aboutPage: "#about",
        downloadPage: "#download",
        downloadTemplateId: "download_page",
        aboutTemplateId: "about_page",
        residuePage: "#residue",
        residueTemplateId: "residue_page",
        content: {
            app: {
                tagline: "A resource for statistically significant mutations in cancer",
                title: "Cancer Hotspots",
                logoStyle: "hotspot-fire"
            },
            home: {
                mutationInfo: _.template($("#default_mutation_info").html())()
            },
            download: {
                links: [
                    {href: 'href="files/hotspots.xls"',
                        text: "Hotspot Results V1"},
                    {href: 'href="https://github.com/taylor-lab/hotspots/blob/master/LINK_TO_MUTATIONAL_DATA"',
                        text: "V1 Mutational Data (11K MAF)"}
                ]
            }
        },
        // TODO view & render options...
        view: {
            hotspotTable: {}
        }
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    function switchContent(routeFn, params)
    {
        // hide everything first
        $(_options.pageContent).children().hide();

        // show the loader image before starting the transition
        $(_options.pageLoader).show();

        setTimeout(function() {
            routeFn(params);

            // hide the loader image after transition completed
            $(_options.pageLoader).hide();
        }, _options.pageLoaderDelay);
    }

    function home(params)
    {
        function initWithData(metadata)
        {
            var proxy = new HotspotDataProxy();

            // get all hotspot data
            proxy.getAllHotspots(function(data) {
                var mainTemplateFn = _.template($(_options.mainView).html());
                $(_options.mainContent).html(mainTemplateFn());

                var options = jQuery.extend(true, {}, _options.view.hotspotTable, {
                    metadata: metadata,
                    data: data
                });

                // init the table view with the hotspot data
                var tableView = new HotspotTableView(options);

                // render the table
                tableView.render();
            });
        }

        function initWithAjax(metadata)
        {
            var mainTemplateFn = _.template($(_options.mainView).html());
            $(_options.mainContent).html(mainTemplateFn());

            // init the table view with the hotspot data retrieval function
            var options = jQuery.extend(true, {}, _options.view.hotspotTable, {
                metadata: metadata,
                "ajax": function (data, callback, settings) {
                    _hotspotProxy.getAllHotspots(function(hotspotData) {
                        // defer rendering of the table a few miliseconds
                        // for a smoother rendering of the loader
                        setTimeout(function(){
                            callback({data: hotspotData});
                        }, _options.tableLoaderDelay);
                    });
                }
            });

            var tableView = new HotspotTableView(options);

            // render the table
            tableView.render();
        }

        // init section if not initialized yet
        if (!$(_options.homePage).length)
        {
            var templateFn = _.template($("#" + _options.homeTemplateId).html());
            $(_options.pageContent).append(templateFn(params));

            // initial AJAX call to determine which profile is active
            _metadataProxy.getMetadata(function(data) {
                //initWithData(data);
                initWithAjax(data);
            });
        }

        $(_options.homePage).show();
    }

    function download(params)
    {
        // init section if not initialized yet
        if (!$(_options.downloadPage).length)
        {
            var templateFn = _.template($("#" + _options.downloadTemplateId).html());
            $(_options.pageContent).append(templateFn(downloadTemplateVars(params)));
        }

        $(_options.downloadPage).show();
    }

    function downloadTemplateVars(params)
    {
        var templateFn = _.template($("#download_link_basic_template").html());
        var links = [];

        links.push("<ul>");

        _.each(params.links, function(link) {
            links.push(templateFn(link));
        });

        links.push("</ul>");

        return {
            links: links.join("\n")
        };
    }

    function about(params)
    {
        // init section if not initialized yet
        if (!$(_options.aboutPage).length)
        {
            var templateFn = _.template($("#" + _options.aboutTemplateId).html());
            $(_options.pageContent).append(templateFn());
        }

        $(_options.aboutPage).show();
    }

    function cluster(params)
    {
        // init section if not initialized yet
        if (!$(_options.residuePage).length)
        {
            var templateFn = _.template($("#" + _options.residueTemplateId).html());
            $(_options.pageContent).append(templateFn());
        }

        $(_options.residuePage).show();

        var dataManager = new ClusterDataManager();
        dataManager.updateData({
            gene: params.hugoSymbol,
            residue: params.residue
        });

        var viewOpts = {
            dataManager: dataManager,
            ajax: function (data, callback, settings) {
                _clusterProxy.getCluster(params.hugoSymbol, params.residue, function(clusterData) {
                    _hotspotProxy.getHotspots([params.hugoSymbol], function(hotspotData) {
                        dataManager.updateData({
                            clusters: clusterData,
                            mutations: hotspotData
                        });
                        // defer rendering of the table a few miliseconds
                        // for a smoother rendering of the loader
                        setTimeout(function(){
                            callback({data: clusterData});
                        }, _options.tableLoaderDelay);
                    });
                });
            }
            //TODO pValueThreshold: _options.pValueThreshold
        };

        if (params.residue == null)
        {
            viewOpts.templateId = "gene_view";
        }

        var residueView = new ResidueView(viewOpts);

        var residueController = new ResidueController(residueView, dataManager);
        residueController.init();
        residueView.render();
    }

    function init()
    {
        // init router
        var router = new Router({
            '/home': function() {
                switchContent(home, _options.content.home);
            },
            '/about': function() {
                switchContent(about);
            },
            '/download': function() {
                switchContent(download, _options.content.download);
            },
            '/residue/:hugoSymbol/:residue': function(hugoSymbol, residue) {
                switchContent(cluster, {hugoSymbol: hugoSymbol, residue: residue});
            },
            '/gene/:hugoSymbol/': function(hugoSymbol) {
                switchContent(cluster, {hugoSymbol: hugoSymbol});
            }
        });

        router.configure({notfound: function() {
            // TODO switch to the not found page! (a static error page)
            //switchContent(unknown);
            $(_options.pageContent).children().hide();
        }});

        // init data proxies
        _metadataProxy = new MetadataProxy();

        _metadataProxy.getMetadata(function(metadata) {
            var hotspotProxyOptions = {};

            if (metadata.profile.toLowerCase().indexOf("3d") != -1)
            {
                hotspotProxyOptions.serviceUrl = "api/hotspots/3d";
                _options.content = {
                    app: {
                        tagline: "A resource for statistically significant mutations clustering " +
                                 "in 3D protein structures in cancer",
                        title: "3D Hotspots",
                        logoStyle: "hotspot-fire hotspot-3d-fire"
                    },
                    home: {
                        mutationInfo: "Mutations clustering in 3D protein structures identified " +
                                      "in 11,119 tumor samples across 41 tumor types"
                    },
                    download: {
                        links: [
                            {href: 'href="files/3d_hotspots.xls"', text: "3D Hotspot Results"}
                        ]
                    }
                };

                // update page title as well
                document.title = "3D Hotspots";
            }
            else
            {
                // TODO update options in a safer way...

                _options.view.hotspotTable.renderer = {
                    residue: {
                        templateId: "residue_column_single"
                    },
                    gene: {
                        templateId: "gene_column_single"
                    }
                };

                if (metadata.profile.toLowerCase() === "internalsingleresidue")
                {
                    _options.content.home.mutationInfo =
                        _.template($("#internal_mutation_info").html())();

                    // 2 more internal links in addition to the public ones
                    _options.content.download.links.unshift(
                        {href: 'href="files/internal_hotspots.xls"',
                            text: "Hotspot results V2"},
                        {href: "",
                            text: "V2 Mutational Data (24K MAF) will be available upon publication"}
                    );
                }
            }

            _hotspotProxy = new HotspotDataProxy(hotspotProxyOptions);
            _clusterProxy = new ClusterDataProxy();

            // init static content
            var templateFn = _.template($("#" + _options.appTemplateId).html());
            $(_options.appContent).append(templateFn(_options.content.app));

            // TODO temporarily hiding about page link for now
            if (metadata.profile.toLowerCase() === "3d")
            {
                $(_options.appContent).find(".about-nav").hide();
                $(".footer .footer-about-link").hide();
            }

            // load home page content initially
            router.init("/home");
        });
    }

    this.init = init;
}
