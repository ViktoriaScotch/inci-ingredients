package ru.ingredients.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

class NormalizationUtilsTest {

    @ParameterizedTest
    @CsvSource({
            "name (with parentheses), name",
            "name123 with non-alphanumeric, name123withnonalphanumeric",
            "name with/slash, namewithslash",
            "имя на кириллице, имянакириллице",
            "TOLOWERCASE, tolowercase"
    })
    void normalize(String input, String expected) {
        //when
        String result = NormalizationUtils.normalize(input);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void normalize_whenNullAndEmptySource(String input) {
        //when
        String result = NormalizationUtils.normalize(input);

        //then
        assertThat(result).isEqualTo("");
    }

}