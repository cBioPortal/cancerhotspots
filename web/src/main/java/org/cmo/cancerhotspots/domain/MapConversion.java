package org.cmo.cancerhotspots.domain;

import com.univocity.parsers.conversions.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapConversion implements Conversion<String, Map<String, Integer>>
{
    private final String itemSeparator;
    private final String mappingSeparator;

    public MapConversion(String... args) {
        String itemSeparator = "\\|";
        String mappingSeparator = ":";

        if (args.length == 1) {
            itemSeparator = args[0];
        }

        if (args.length == 2) {
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
                out.append(itemSeparator);
            }

            out.append(key);
            out.append(mappingSeparator);
            out.append(value);
        }

        if (out.length() == 0) {
            return null;
        }

        return out.toString();
    }
}
