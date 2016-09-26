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

import com.univocity.parsers.conversions.Conversion;
import org.cmo.cancerhotspots.data.IntegerRange;

/**
 * @author Selcuk Onur Sumer
 */
public class RangeConversion implements Conversion<String, IntegerRange>
{
    private final String rangeSeparator;

    public RangeConversion(String... args) {
        String rangeSeparator = Config.RANGE_ITEM_SEPARATOR;

        if (args.length > 0) {
            rangeSeparator = args[0];
        }

        this.rangeSeparator = rangeSeparator;
    }

    public RangeConversion(String rangeSeparator)
    {
        this.rangeSeparator = rangeSeparator;
    }

    @Override
    public  IntegerRange execute(String input) {
        IntegerRange range = new IntegerRange();

        if (input == null) {
            return range;
        }

        String[] parts = input.trim().split(rangeSeparator);

        if (parts.length >= 1 &&
            parts[0].matches("\\d+"))
        {
            range.setStart(
                Integer.parseInt(parts[0]));
        }

        if (parts.length >= 2 &&
            parts[1].matches("\\d+"))
        {
            range.setEnd(
                Integer.parseInt(parts[1]));
        }

        return range;
    }

    @Override
    public String revert(IntegerRange input)
    {
        if (input == null) {
            return null;
        }

        StringBuilder out = new StringBuilder();

        if (input.getStart() != null)
        {
            out.append(input.getStart());
        }

        if (input.getEnd() != null)
        {
            out.append(rangeSeparator.replaceAll("\\\\", ""));
            out.append(input.getEnd());
        }

        return out.toString();
    }
}
