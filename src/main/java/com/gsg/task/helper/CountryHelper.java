package com.gsg.task.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CountryHelper {
    private static final HashMap<String, String> countriesName = new HashMap<>();
    private static final HashMap<String, String> countriesCode = new HashMap<>();

    static {
        Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2).forEach(ccode -> {
            String displayName = new Locale("", ccode.toUpperCase()).getDisplayCountry();
            countriesName.put(displayName, ccode);
            countriesCode.put(ccode, displayName);
        });
    }

    public static List<String> getAllCountryCodes() {
        return new ArrayList<>(countriesName.values());
    }

    public static List<String> getAllCountryNames() {
        return new ArrayList<>(countriesName.keySet());
    }

    public static String getCountryCode(String country) {
        return countriesName.get(country);
    }

    public static String getCountryName(String code) {
        return countriesCode.get(code);
    }

    public static boolean countryIsIncorrect(String countryCode) {
        return countriesCode.get(countryCode) == null;
    }

}
