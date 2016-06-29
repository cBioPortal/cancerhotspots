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
function ResidueController(residueView, dataManager)
{
    function init()
    {
        $(dataManager.dispatcher).on(EventUtils.CLUSTER_RESIDUE_HIGHLIGHT, function(event, data) {
            var diagram = mutationDiagram();

            if (diagram)
            {
                if (diagram.isHighlighted()) {
                    diagram.clearHighlights();
                }

                // clear all residue highlights
                residueView.unHighlightResidue();

                // highlight mutations corresponding to each residue
                _.each(data.highlighted, function (residue)
                {
                    diagram.highlightMutation(defaultMutationSid(residue));
                    residueView.highlightResidue(residue);
                });

                // selected mutations should always remain highlighted!
                _.each(data.selected, function (residue)
                {
                    diagram.highlightMutation(defaultMutationSid(residue));
                    residueView.highlightResidue(residue);
                });
            }
        });

        $(dataManager.dispatcher).on(EventUtils.CLUSTER_RESIDUE_SELECT, function(event, data) {
            var diagram = mutationDiagram();

            if (diagram)
            {
                if (diagram.isHighlighted()) {
                    diagram.clearHighlights();
                }

                // clear all residue highlights
                residueView.unHighlightResidue();

                // highlight mutations corresponding to each residue
                _.each(data.selected, function(residue) {
                    diagram.highlightMutation(defaultMutationSid(residue));
                    residueView.highlightResidue(residue);
                });
            }
        });

        $(dataManager.dispatcher).on(EventUtils.CLUSTER_PDB_SELECT, function(event, data) {
            var parts = data.pdb[0].split(":");
            residueView.getMutationMapper().getController().get3dController().reset3dView(
                parts[0], parts[1]);
        });
    }

    function defaultMutationSid(residue)
    {
        var gene = dataManager.getData().gene;
        return gene + "_" + residue + "_" + "1";
    }

    function mutationDiagram()
    {
        var gene = dataManager.getData().gene;
        var mainView = residueView.getMutationMapper().getController()
            .getMainView(gene).mainMutationView;

        if (mainView &&
            mainView.diagramView &&
            mainView.diagramView.mutationDiagram)
        {
            return mainView.diagramView.mutationDiagram;
        }
    }

    this.init = init;
}
