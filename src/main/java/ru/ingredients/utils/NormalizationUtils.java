package ru.ingredients.utils;

public final class NormalizationUtils {

    private static final String REGEX_INSIDE_PARENTHESES = "\\(.+?\\)";
    private static final String REGEX_NON_ALPHANUMERIC = "[^a-zA-Zа-яА-Я0-9]+";

    private NormalizationUtils() {
    }

    public static String normalize(String s) {
        return s == null ? "" : s
                .replaceAll(REGEX_INSIDE_PARENTHESES, "") //убирается информация в скобках
                .replaceAll(REGEX_NON_ALPHANUMERIC, "") //убираются все не цифро-буквенные символы
                .toLowerCase(); //приводится к нижнему регистру
    }
}
