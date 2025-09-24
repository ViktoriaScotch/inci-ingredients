package ru.ingredients.decoding;

import org.springframework.stereotype.Service;
import ru.ingredients.category.CategoryDTO;
import ru.ingredients.ingredient.*;

import java.util.*;

import static ru.ingredients.utils.NormalizationUtils.normalize;

@Service
public class DecodingService {

    private final IngredientService ingredientService;

    public DecodingService(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    public List<IngredientDTO> decode(String text) {
        // выходим из метода, если передан пустой параметр
        if (text == null || text.isBlank()) return List.of();

        // отделяем имена через запятую, убираем пустые значения, пробелы в начале и в конце
        List<String> separateNames = Arrays.stream(text.split(", "))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        // делаем запрос в БД
        List<IngredientDTO> foundIngredients = ingredientService.getIngredientsByAllNames(separateNames);

        // сохраняем все имена найденных ингредиентов, чтобы отфильтровать нераспознанные
        Map<String, IngredientDTO> foundMap = new HashMap<>();
        for (IngredientDTO ing : foundIngredients) {
            foundMap.put(normalize(ing.getInci()), ing);
            foundMap.put(normalize(ing.getTradeName()), ing);
            ing.getOtherNames().forEach(n -> foundMap.put(normalize(n), ing));
        }

        // проходимся по списку наименований, чтобы вернуть распознанные
        // и нераспознанные ингредиенты в изначальном порядке состава
        List<IngredientDTO> result = new ArrayList<>();
        for (String name : separateNames) {
            IngredientDTO ing = foundMap.get(normalize(name)); //проверяем, если ли имя в списке распознанных
            if (ing != null) {
                result.add(ing); //если распознан, сохраняем ингредиент
            } else {
                result.add(new IngredientDTO().setTradeName(name)); //если нет, создаем пустой объект с нераспознанным именем
            }
        }

        return result;
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
}
