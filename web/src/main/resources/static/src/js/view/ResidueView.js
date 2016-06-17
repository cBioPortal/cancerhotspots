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
 * Cancer Hotspots Residue View.
 * Designed to visualize residue data with a DataTable.
 *
 * @param options   view options
 * @author Selcuk Onur Sumer
 */
function ResidueView(options)
{
    var _defaultOpts = {
        // default target DOM element
        el: "#residue_content",
        templateId: "residue_view",
        // no data manager by default, must be provided by the client
        dataManager: {},
        // delay amount before applying the user entered filter query
        filteringDelay: 500,
        // threshold for pValue, any value below this will be shown as >threshold
        pValueThreshold: 0.001,
        residuesData: function(row) {
            return {
                residue: _options.dataManager.getData().residue,
                residues: row["residues"]
            };
        },
        tumorCountData: function(row) {
            var tumorCount = 0;
            var tumorTypeCount = 0;
            var composition = {};

            _.each(_options.dataManager.getData().mutations, function(mutation) {
                // select the mutation if only it's residue is in this cluster

                // TODO this is not correct, we need to merge all data!
                if (_.contains(_.keys(row["residues"]), mutation.residue))
                {
                    tumorCount = mutation.tumorCount;
                    composition = mutation.tumorTypeComposition;
                    tumorTypeCount = mutation.tumorTypeCount;
                }
            });

            return {
                tumorCount: tumorCount,
                tumorTypeCount: tumorTypeCount,
                composition: composition
            };
        }
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    function render()
    {
        var templateFn = _.template($("#" + _options.templateId).html());
        $(_options.el).html(templateFn(_options.dataManager.getData()));

        var pValueRender = new PValueRender({
            threshold: _options.pValueThreshold
        });

        var pdbChainsRender = new PdbChainsRender({
            pValueThreshold: _options.pValueThreshold
        });

        var residuesRender = new ResiduesRender({
            dataManager: _options.dataManager
        });

        var clusterRender = new ClusterRender();
        var tumorCountRender = new TumorCountRender();
        var noWrapRender = new NoWrapRender();

        var dataTableOpts = {
            dom: "<'row'<'col-sm-8 residue-table-title'><'col-sm-4'f>>t" +
                 "<'row'<'col-sm-8'i><'col-sm-4 right-align table-button-group'>>",
            paging: false,
            scrollY: "500px",
            scrollCollapse: true,
            language: {
                loadingRecords: '<img src="lib/images/loader.gif"> Loading...'
            },
            order: [[3, "asc"], [2, "desc"]],
            columns: [
                {id: "cluster",
                    title: noWrapRender.render("Cluster"),
                    data: "clusterId",
                    render: clusterRender.render},
                {id: "residues",
                    title: "Residues",
                    data: _options.residuesData,
                    render: residuesRender.render,
                    createdCell: residuesRender.postRender},
                {id: "pdbChains",
                    title: "PDB Chains",
                    data: "pdbChains",
                    render: pdbChainsRender.render,
                    createdCell: pdbChainsRender.postRender},
                {id: "pValue",
                    title: noWrapRender.render("P-value"),
                    data: "pValue",
                    render: pValueRender.render},
                {id: "tumorCount",
                    title: "Sample Count",
                    data: _options.tumorCountData,
                    render: tumorCountRender.render,
                    createdCell: tumorCountRender.postRender}
            ],
            initComplete: function(settings) {
                var dataTable = this;

                // add a delay to the filter
                if (_options.filteringDelay > 0)
                {
                    dataTable.fnSetFilteringDelay(_options.filteringDelay);
                }

                // generate mutation mapper data
                var mutationData = generateMutationData(dataTable.api().data());

                // TODO also generate PDB data?

                // init the mutation mapper
                var mutationMapper = initMutationMapper(_.values(mutationData));
            }
        };

        if (_.isFunction(_options.ajax))
        {
            dataTableOpts.ajax = _options.ajax;
        }
        else
        {
            dataTableOpts.data = _options.dataManager.getData().clusters;
        }

        // init the residue table
        $(_options.el).find("#residue_table").DataTable(dataTableOpts);

        $("div.residue-table-title").html(
            _.template($("#residue_table_title").html())(
                {residue: _options.dataManager.getData().residue}));
    }

    function generateMutationData(clusterData)
    {
        var mutationData = {};

        _.each(clusterData, function(cluster) {
            _.each(_.keys(cluster.residues), function(residue) {
                var counter = 0;
                _.times(cluster.residues[residue], function() {
                    counter++;
                    var id = _options.dataManager.getData().gene + "_" + residue + "_" + counter;
                    // index by id instead of adding into an array
                    // this will prevent duplicates
                    mutationData[id] = {
                        mutationId: id,
                        mutationSid: id,
                        proteinChange: residue,
                        geneSymbol: _options.dataManager.getData().gene,
                        mutationType: "missense_mutation"
                    };
                });
            });
        });

        return mutationData;
    }

    function initMutationMapper(mutationData)
    {
        var options = {
            el:  $(_options.el).find('.mutation-mapper-container'),
            data: {
                geneList: [_options.dataManager.getData().gene]
            },
            view: {
                mutationTable: false,
                mutationSummary: false,
                infoPanel: false
            },
            render: {
                mutation3dVis: {
                    loaderImage: "lib/images/ajax-loader.gif",
                    helpImage: "lib/images/help.png",
                    border: {
                        top: "120px"
                    }
                },
                mainMutation: {
                    loaderImage: "lib/images/ajax-loader.gif"
                },
                mutationDetails: {
                    loaderImage: "lib/images/ajax-loader.gif",
                    coreTemplate: "custom_mutation_details_template",
                    init: function(mutationDetailsView) {
                        // hide loader image
                        mutationDetailsView.$el.find(".mutation-details-loader").hide();
                    },
                    format: function(mutationDetailsView) {
                        mutationDetailsView.dispatcher.trigger(
                            MutationDetailsEvents.GENE_TABS_CREATED);
                    }
                }
            },
            proxy: {
                mutationProxy: {
                    options: {
                        initMode: "full",
                        data: mutationData
                    }
                },
                pfamProxy: {
                    options: {
                        servletName: 'http://www.cbioportal.org/getPfamSequence.json'
                    }
                },
                mutationAlignerProxy: {
                    options: {
                        servletName: 'http://www.cbioportal.org/getMutationAligner.json'
                    }
                },
                pdbProxy: {
                    options: {
                        servletName: 'http://www.cbioportal.org/get3dPdb.json',
                        subService: false,
                        listJoiner: ' '
                    }
                }
            }
        };

        var mutationMapper = new MutationMapper(options);
        mutationMapper.init();
        return mutationMapper;
    }

    this.render = render;

}

