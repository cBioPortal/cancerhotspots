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
import org.cmo.cancerhotspots.util.Config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
public class MapConversion implements Conversion<String, Map<String, Integer>>
{
    private final String itemSeparator;
    private final String mappingSeparator;

    public MapConversion(String... args) {
        String itemSeparator = Config.ITEM_SEPARATOR;
        String mappingSeparator = Config.MAPPING_SEPARATOR;

        if (args.length > 0) {
            itemSeparator = args[0];
        }

        if (args.length > 1) {
            mappingSeparator = args[1];
        }

        this.itemSeparator = itemSeparator;
        this.mappingSeparator = mappingSeparator;
    }

    public MapConversion(String itemSeparator, String mappingSeparator)
    {
        this.itemSeparator = itemSeparator;
        this.mappingSeparator = mappingSeparator;
    }

    @Override
    public  Map<String, Integer> execute(String input) {
        if (input == null) {
            return Collections.emptyMap();
        }

        Map<String, Integer> out = new HashMap<>();

        for (String token : input.split(itemSeparator)) {
            String[] parts = token.trim().split(mappingSeparator);

            if (parts.length == 2)
            {
                out.put(parts[0], Integer.parseInt(parts[1]));
            }
        }

        return out;
    }

    @Override
    public String revert(Map<String, Integer> input)
    {
        if (input == null || input.isEmpty()) {
            return null;
        }

        StringBuilder out = new StringBuilder();

        for (String key : input.keySet()) {
            Integer value = input.get(key);
            if (value == null)
            {
                continue;
            }

            if (out.length() > 0) {
                out.append(itemSeparator.replaceAll("\\\\", ""));
            }

            out.append(key);
            out.append(mappingSeparator.replaceAll("\\\\", ""));
            out.append(value);
        }

        if (out.length() == 0) {
            return null;
        }

        return out.toString();
    }
}
