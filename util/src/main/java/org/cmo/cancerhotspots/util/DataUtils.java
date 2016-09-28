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

package org.cmo.cancerhotspots.util;

import org.cmo.cancerhotspots.data.IntegerRange;

import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
public class DataUtils
{
    public static Map<String, Integer> mergeCompositions(
        Map<String, Integer> target,
        Map<String, Integer> source)
    {
        for(String key: source.keySet())
        {
            Integer value = target.get(key);

            // if no value yet, just copy from the source
            if (value == null)
            {
                target.put(key, source.get(key));
            }
            // if already exists add to the current value
            else
            {
                target.put(key, value + source.get(key));
            }
        }

        return target;
    }

    public static String mutationResidue(IntegerRange position, String reference, Integer indelSize)
    {
        String residue = null;

        // indel mutation with a range: set residue to the range value
        if (position.getStart() != null &&
            position.getEnd() != null)
        {
            residue = position.getStart() +
                      Config.RANGE_ITEM_SEPARATOR +
                      position.getEnd();
        }
        // indel mutation with start position only: set residue to the start pos
        else if (indelSize != null &&
                 position.getStart() != null)
        {
            residue = position.getStart().toString();
        }
        // single residue mutation: set residue to ref + start pos
        else if (position.getStart() != null)
        {
            residue = reference + position.getStart();
        }

        return residue;
    }
}
