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
    var _dispatcher = {};

    var _defaultOpts = {
        // default target DOM element
        el: "#residue_content",
        templateId: "residue_view",
        legendTemplateId: "class_legend",
        // no data manager by default, must be provided by the client
        dataManager: {},
        // delay amount before applying the user entered filter query
        filteringDelay: 500,
        // threshold for pValue, any value below this will be shown as >threshold
        pValueThreshold: 0.0001,
        classColors: function (pileup) {
            var colors = {
                LH: "#09BCD3",
                LL: "#DEBA24",
                H: "#4CAE4E",
                "Hotspot-linked": "#09BCD3",
                "Cluster-exclusive": "#DEBA24",
                "Hotspot": "#4CAE4E"
            };

            var mutation = _.first(pileup.mutations);
            return colors[mutation.get("residueClass")];
        },
        residuesData: function(row) {
            var residue = _options.dataManager.getData().residue;
            var residues = row["residues"];
            var classifications = {};

            _.each(_.keys(residues), function(residue) {
                classifications[residue] = MutationUtils.findResidueClass(
                    residue, _options.dataManager.getData().mutations);
            });

            return {
                gene: _options.dataManager.getData().gene,
                residue: residue,
                residues: residues,
                classifications: classifications
            };
        },
        tumorCountData: function(row) {
            var composition = {};

            // combine all the tumor type composition maps in one map
            _.each(_options.dataManager.getData().mutations, function(mutation) {
                // select the mutation if only it's residue is in this cluster
                if (_.contains(_.keys(row["residues"]), mutation.residue))
                {
                    _.each(_.keys(mutation.tumorTypeComposition), function (tumorType) {
                        var count = mutation.tumorTypeComposition[tumorType];

                        // init if not initialized yet
                        if (composition[tumorType] == null) {
                            composition[tumorType] = 0;
                        }

                        composition[tumorType] += count;
                    });
                }
            });

            // tumor count is the sum of all values
            var tumorCount = _.reduce(_.values(composition), function(memo, value) {
                return memo + value;
            });

            // tumor type count is the total number of keys
            var tumorTypeCount = _.size(_.keys(composition));

            return {
                tumorCount: tumorCount,
                tumorTypeCount: tumorTypeCount,
                composition: composition
            };
        }
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    var _mutationMapper = null;

    function render()
    {
        var templateFn = _.template($("#" + _options.templateId).html());
        $(_options.el).html(templateFn(_options.dataManager.getData()));

        var pValueRender = new DecimalValueRender({
            threshold: _options.pValueThreshold
        });

        var pdbChainsRender = new PdbChainsRender({
            pValueThreshold: _options.pValueThreshold,
            dataManager: _options.dataManager
        });

        var residuesRender = new ResiduesRender({
            dataManager: _options.dataManager
        });

        var clusterRender = new ClusterRender({
            dataManager: _options.dataManager
        });

        var tumorCountRender = new TumorCountRender();
        var noWrapRender = new NoWrapRender();

        var dataTableOpts = {
            dom: "<'row'<'col-sm-6 residue-table-title'><'col-sm-6'f>>t" +
                 "<'row'<'col-sm-8'i><'col-sm-4 right-align table-button-group'>>",
            paging: false,
            scrollY: "500px",
            scrollCollapse: true,
            language: {
                loadingRecords: '<img src="lib/images/loader.gif"> Loading...'
            },
            order: [[3, "asc"], [4, "desc"]],
            columns: [
                {id: "cluster",
                    title: noWrapRender.render("Cluster"),
                    data: "clusterId",
                    type: "num",
                    render: clusterRender.render,
                    createdCell: clusterRender.postRender},
                {id: "residues",
                    title: "Residues",
                    data: _options.residuesData,
                    render: residuesRender.render,
                    type: "num",
                    createdCell: residuesRender.postRender},
                {id: "pdbChains",
                    title: "PDB Chains",
                    data: "pdbChains",
                    render: pdbChainsRender.render,
                    type: "num",
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
                //var mutationData = generateMutationData(dataTable.api().data());
                var mutationData = MutationUtils.generateMutationMapperData(
                    _options.dataManager.getData().mutations);

                // TODO also generate PDB data?

                // init the mutation mapper
                _mutationMapper = initMutationMapper(mutationData);
                $(_dispatcher).trigger(EventUtils.MUTATION_MAPPER_INIT, _mutationMapper);

                $(_options.el).find(".class-legend-container").html(
                    _.template($("#" + _options.legendTemplateId).html())());
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

        // update the title
        if (_options.dataManager.getData().residue != null)
        {
            $("div.residue-table-title").html(
                _.template($("#residue_table_title").html())(
                    {residue: _options.dataManager.getData().residue}));
        }
    }

    function highlightResidue(residue)
    {
        _.each($(_options.el).find(".cluster-residue"), function(span) {
            if($(span).text() === residue)
            {
                $(span).addClass("highlighted");
            }
        });
    }

    function unHighlightResidue(residue)
    {
        if (residue == null)
        {
            // remove highlights for all residues
            $(_options.el).find(".cluster-residue").removeClass("highlighted");
        }
        else
        {
            // only remove the provided residue's highlight
            _.each($(_options.el).find(".cluster-residue"), function(span) {
                if($(span).text() === residue)
                {
                    $(span).removeClass("highlighted");
                }
            });
        }
    }

	/**
     * Generates mutation mapper data for the provided cluster data.
     *
     * @param clusterData
     * @returns {Array} array of mutation data
     */
    function generateMutationMapperData(clusterData)
    {
        var mutationData = {};

        _.each(clusterData, function(cluster) {
            _.each(_.keys(cluster.residues), function(residue) {
                var counter = 0;
                var residueClass = MutationUtils.findResidueClass(
                    residue, _options.dataManager.getData().mutations);

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
                        residueClass: residueClass
                    };
                });
            });
        });

        return _.values(mutationData);
    }

    function initMutationMapper(mutationData)
    {
        var cluster = _.first(_options.dataManager.getData().clusters);
        var pdbChains = MutationUtils.convertToPdbList(cluster.pdbChains);

        var options = {
            el:  $(_options.el).find('.mutation-mapper-container'),
            data: {
                geneList: [_options.dataManager.getData().gene]
            },
            view: {
                mutationDiagram: {
                    lollipopFillColor: _options.classColors
                },
                pdbPanel: {
                    labelY: false
                },
                mutationTable: false,
                mutationSummary: false,
                pdbTable: false,
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
                pdbPanel: {
                    autoExpand: false
                },
                mainMutation: {
                    loaderImage: "lib/images/ajax-loader.gif"
                },
                mutationDetails: {
                    loaderImage: "lib/images/ajax-loader.gif",
                    coreTemplate: "custom_mutation_details_template",
                    activate3dOnInit: {
                        pdbId: pdbChains[0].pdbId,
                        chain: pdbChains[0].chain
                    },
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
                        servletName: 'proxy/www.cbioportal.org/getPfamSequence.json'
                    }
                },
                mutationAlignerProxy: {
                    options: {
                        servletName: 'proxy/www.cbioportal.org/getMutationAligner.json'
                    }
                },
                pdbProxy: {
                    options: {
                        servletName: 'proxy/www.cbioportal.org/get3dPdb.json',
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
    this.highlightResidue = highlightResidue;
    this.unHighlightResidue = unHighlightResidue;
    this.dispatcher = _dispatcher;

    this.getMutationMapper = function(){
        return _mutationMapper;
    };
}

