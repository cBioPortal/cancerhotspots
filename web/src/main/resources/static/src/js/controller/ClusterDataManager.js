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
function ClusterDataManager(options)
{
    var _dispatcher = {};

    var _data = {

    };

    var _defaultOpts = {

    };

    var _state = {
        highlighted: [],
        selected: [],
        filtered: [],
        filteredClusters: [],
        pdb: []
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    function updateData(data)
    {
        _data = jQuery.extend(true, {}, _data, data);
    }

    function setData(data)
    {
        _data = data;
    }

    function getData()
    {
        return _data;
    }

    function highlightResidues(residues)
    {
        // add given residues to the set of highlighted residues
        _state.highlighted = _.union(_state.highlighted, residues);

        // trigger a custom event
        $(_dispatcher).trigger(EventUtils.CLUSTER_RESIDUE_HIGHLIGHT, _state);
    }

    function unHighlightResidues(residues)
    {
        if (residues == null)
        {
            // reset all highlights
            _state.highlighted = [];
        }
        else
        {
            // remove given residues from the set of highlighted residues
            _state.highlighted = _.difference(_state.highlighted, residues);
        }

        // trigger a custom event
        $(_dispatcher).trigger(EventUtils.CLUSTER_RESIDUE_HIGHLIGHT, _state);
    }

    function filterResidues(residues)
    {
        // add given residues to the set of filtered residues
        _state.filtered = _.union(_state.filtered, residues);

        // trigger a custom event
        $(_dispatcher).trigger(EventUtils.CLUSTER_RESIDUE_FILTER, _state);
    }

    function unfilterResidues(residues)
    {
        if (residues == null)
        {
            // reset all filters
            _state.filtered = [];
        }
        else
        {
            // remove given residues from the set of filtered residues
            _state.filtered = _.difference(_state.filtered, residues);
        }

        // trigger a custom event
        $(_dispatcher).trigger(EventUtils.CLUSTER_RESIDUE_FILTER, _state);
    }

    function filterClusters(clusters)
    {
        // add given residues to the set of filtered residues
        _state.filteredClusters = _.union(_state.filteredClusters, clusters);

        // filter residues corresponding to the filtered clusters
        if (_data && _data.clusters)
        {
            var residuesToFilter = findResidues(_state.filteredClusters);

            // check if we really need to filter any residue
            if (!_.isEmpty(_.difference(residuesToFilter, _state.filtered)))
            {
                filterResidues(residuesToFilter);
            }
        }
    }

    function unfilterClusters(clusters)
    {
        if (clusters == null)
        {
            // reset all filters
            _state.filteredClusters = [];
            unfilterResidues();
        }
        else
        {
            // remove given residues from the set of filtered residues
            _state.filteredClusters = _.difference(_state.filteredClusters, clusters);

            // this means nothing to filter, so show everything
            if (_.isEmpty(_state.filteredClusters))
            {
                unfilterResidues();
            }
            else
            {
                // filter residues corresponding to the filtered clusters
                var residuesToFilter = findResidues(_state.filteredClusters);
                var residuesToUnfilter = _.difference(_state.filtered, residuesToFilter);

                // check if we really need to unfilter any residue
                if (!_.isEmpty(residuesToUnfilter))
                {
                    unfilterResidues(residuesToUnfilter);
                }
            }
        }
    }

	/**
     * Finds all residues within the given cluster set.
     *
     * @param clusters  an array of cluster instances
     * @returns {Array} set of residues within the given clusters
     */
    function findResidues(clusters)
    {
        var filteredClusters = _.filter(_data.clusters, function(cluster) {
            return _.contains(clusters, cluster.clusterId);
        });

        var residues = [];

        _.each(filteredClusters, function(cluster) {
            residues = _.union(residues, _.keys(cluster.residues));
        });

        return residues;
    }

    function selectResidues(residues)
    {
        // add given residues to the set of selected residues
        _state.selected = _.union(_state.selected, residues);

        // trigger a custom event
        $(_dispatcher).trigger(EventUtils.CLUSTER_RESIDUE_SELECT, _state);
    }

    function unSelectResidues(residues)
    {
        // remove given residues from the list of selected residues
        _state.selected = _.difference(_state.selected, residues);

        // trigger a custom event
        $(_dispatcher).trigger(EventUtils.CLUSTER_RESIDUE_SELECT, _state);
    }

    function selectPdbChain(pdbChain)
    {
        _state.pdb = [pdbChain];

        // trigger a custom event
        $(_dispatcher).trigger(EventUtils.CLUSTER_PDB_SELECT, _state);
    }

    this.updateData = updateData;
    this.setData = setData;
    this.getData = getData;
    this.highlightResidues = highlightResidues;
    this.unHighlightResidues = unHighlightResidues;
    this.filterResidues = filterResidues;
    this.unfilterResidues = unfilterResidues;
    this.filterClusters = filterClusters;
    this.unfilterClusters = unfilterClusters;
    this.selectResidues = selectResidues;
    this.unSelectResidues = unSelectResidues;
    this.selectPdbChain = selectPdbChain;
    this.dispatcher = _dispatcher;
}
