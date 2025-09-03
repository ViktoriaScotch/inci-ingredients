package ru.ingredients.decoding;

import org.springframework.stereotype.Service;
import ru.ingredients.category.CategoryDTO;
import ru.ingredients.ingredient.Ingredient;
import ru.ingredients.ingredient.IngredientDTO;
import ru.ingredients.ingredient.IngredientMapper;
import ru.ingredients.ingredient.IngredientRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DecodingService {

    private final IngredientRepository ingredientRepository;

    private final IngredientMapper ingredientMapper;

    public DecodingService(IngredientRepository ingredientRepository, IngredientMapper ingredientMapper) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
    }

    private static final String REGEX_INSIDE_PARENTHESES = "\\(.+?\\)";
    private static final String REGEX_NON_ALPHANUMERIC = "[^a-zA-Zа-яА-Я0-9]+";

    public List<IngredientDTO> findIng(String text) {
        // выходим из метода, если передан пустой параметр
        if (text == null || text.isBlank()) return List.of();

        // отделяем имена через запятую
        List<String> separateNames = List.of(text.split(", "));

        //нормализуем имена, делаем по ним запрос в БД
        List<String> normalizedNames = separateNames.stream().map(this::normalize).toList();
        List<Ingredient> foundIngredients = ingredientRepository.findByAllNames(normalizedNames);

        // сохраняем все имена найденных ингредиентов, чтобы отфильтровать нераспознанные
        Map<String, Ingredient> foundMap = new HashMap<>();
        for (Ingredient ing : foundIngredients) {
            foundMap.put(normalize(ing.getInci()), ing);
            foundMap.put(normalize(ing.getTradeName()), ing);
            ing.getOtherNames().forEach(n -> foundMap.put(normalize(n), ing));
        }

        // проходимся по списку наименований, чтобы вернуть распознанные
        // и нераспознанные ингредиенты в изначальном порядке состава
        List<Ingredient> result = new ArrayList<>();
        for (String name : separateNames) {
            Ingredient ing = foundMap.get(normalize(name)); //проверяем, если ли имя в списке распознанных
            if (ing != null) {
                result.add(ing); //если распознан, сохраняем ингредиент
            } else {
                result.add(new Ingredient().setTradeName(name)); //если нет, создаем пустой объект с нераспознанным именем
            }
        }

        // возвращаем результат в формате DTO (при маппинге отдельно подтянутся категории найденных ингредиентов)
        return result.stream().map(ingredientMapper::toDto).toList();
    }


    public Map<String, List<IngredientDTO>> groupByCat(List<IngredientDTO> ingredients) {
        Map<String, List<IngredientDTO>> ingByCat = new HashMap<>();
        for (IngredientDTO ingredient : ingredients) {
            for (CategoryDTO cat : ingredient.getCategories()) {
                ingByCat.computeIfAbsent(cat.getName(), k -> new ArrayList<>()).add(ingredient);
            }
        }
        return ingByCat;
    }

    private String normalize(String s) {
        return s == null ? "" : s
                .replaceAll(REGEX_INSIDE_PARENTHESES, "") //убирается информация в скобках
                .replaceAll(REGEX_NON_ALPHANUMERIC, "") //убираются все не цифро-буквенные символы
                .toLowerCase(); //приводится к нижнему регистру
    }
}
