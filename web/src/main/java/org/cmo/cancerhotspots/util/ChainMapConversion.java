package org.cmo.cancerhotspots.util;

import com.univocity.parsers.conversions.Conversion;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
public class ChainMapConversion implements Conversion<String, Map<String, Double>>
{
    private final String itemSeparator;
    private final String bracketOpen;
    private final String bracketClose;

    public ChainMapConversion(String... args) {
        String itemSeparator = Config.CHAIN_ITEM_SEPARATOR;
        String bracketOpen = Config.CHAIN_OPEN_BRACKET;
        String bracketClose = Config.CHAIN_CLOSE_BRACKET;

        if (args.length > 0) {
            itemSeparator = args[0];
        }

        if (args.length > 1) {
            bracketOpen = args[1];
        }

        if (args.length > 2) {
            bracketClose = args[2];
        }

        this.itemSeparator = itemSeparator;
        this.bracketOpen = bracketOpen;
        this.bracketClose = bracketClose;
    }

    public ChainMapConversion(String itemSeparator, String bracketOpen, String bracketClose)
    {
        this.itemSeparator = itemSeparator;
        this.bracketOpen = bracketOpen;
        this.bracketClose = bracketClose;
    }

    @Override
    public  Map<String, Double> execute(String input) {
        if (input == null) {
            return Collections.emptyMap();
        }

        Map<String, Double> out = new HashMap<>();

        for (String token : input.split(itemSeparator)) {
            String[] parts = token.trim().split(bracketOpen + "|" + bracketClose);

            if (parts.length >= 2)
            {
                out.put(parts[0], Double.parseDouble(parts[1]));
            }
        }

        return out;
    }

    @Override
    public String revert(Map<String, Double> input)
    {
        if (input == null || input.isEmpty()) {
            return null;
        }

        StringBuilder out = new StringBuilder();

        for (String key : input.keySet()) {
            Double value = input.get(key);
            if (value == null)
            {
                continue;
            }

            if (out.length() > 0) {
                out.append(itemSeparator.replaceAll("\\\\", ""));
            }

            out.append(key);
            out.append(bracketOpen.replaceAll("\\\\", ""));
            out.append(value);
            out.append(bracketClose.replaceAll("\\\\", ""));
        }

        if (out.length() == 0) {
            return null;
        }

        return out.toString();
    }
}
