package com.gsg.task.gsgtask.helper;

import com.gsg.task.helper.CountryHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CountryHelperTest {
    @Test
    void countryHelperTest() {
        CountryHelper.getAllCountryCodes();
        assertEquals(CountryHelper.getAllCountryCodes().size(), CountryHelper.getAllCountryNames().size());
        assertNotNull(CountryHelper.getCountryName("GE"));
        assertNotNull(CountryHelper.getCountryCode(CountryHelper.getCountryName("GE")));
    }
}
