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

import com.univocity.parsers.conversions.*;

import java.util.*;

/**
 * @author Selcuk Onur Sumer
 */
public class ListConversion implements Conversion<String, List<String>>
{
    private final String itemSeparator;

    public ListConversion(String... args) {
        String itemSeparator = Config.LIST_SEPARATOR;

        if (args.length > 0) {
            itemSeparator = args[0];
        }

        this.itemSeparator = itemSeparator;
    }

    public ListConversion(String itemSeparator)
    {
        this.itemSeparator = itemSeparator;
    }

    @Override
    public  List<String> execute(String input) {
        if (input == null) {
            return Collections.emptyList();
        }
        else {
            return Arrays.asList(input.split(itemSeparator));
        }
    }

    @Override
    public String revert(List<String> input)
    {
        if (input == null || input.isEmpty()) {
            return null;
        }

        StringBuilder out = new StringBuilder();
        String separator = itemSeparator.replaceAll("\\\\", "");

        for (String item : input) {
            if (item == null) {
                continue;
            }

            if (out.length() > 0) {
                out.append(separator);
            }

            out.append(item);
        }

        if (out.length() == 0) {
            return null;
        }

        return out.toString();
    }
}
