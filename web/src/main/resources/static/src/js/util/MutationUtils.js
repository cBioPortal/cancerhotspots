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
 * Mutation Data Utils.
 *
 * @author Selcuk Onur Sumer
 */
var MutationUtils = (function() {

	/**
     * Generates mutation mapper data for the given hotspot mutation list.
     *
     * @param mutations an array of hotspot mutations
     * @returns {Array} an array of mutations for MutationMapper
     */
    function generateMutationMapperData(mutations)
    {
        var mutationData = {};

        _.each(mutations, function(mutation) {
            var counter = 0;
            var residueClass = findResidueClass(mutation.residue, mutations);

            _.each(_.keys(mutation.variantAminoAcid), function(variant) {
                _.times(mutation.variantAminoAcid[variant], function() {
                    counter++;
                    var id = mutation.hugoSymbol + "_" + mutation.residue + "_" + counter;
                    // index by id instead of adding into an array
                    // this will prevent duplicates
                    mutationData[id] = {
                        mutationId: id,
                        mutationSid: id,
                        proteinChange: mutation.residue + variant,
                        geneSymbol: mutation.hugoSymbol,
                        residueClass: residueClass
                    };
                });
            })

        });

        return _.values(mutationData);
    }

    function findResidueClass(residue, mutations)
    {
        // any mutation with the current residue is enough to determine the class
        var mutation = _.find(mutations, function(mutation) {
            return mutation.residue === residue;
        });

        return mutation.classification;
    }

    return {
        generateMutationMapperData: generateMutationMapperData,
        findResidueClass: findResidueClass
    }
})();
