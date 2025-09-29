package ru.ingredients.ingredient;

import org.springframework.stereotype.Service;
import ru.ingredients.utils.NormalizationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    public IngredientService(IngredientRepository ingredientRepository, IngredientMapper ingredientMapper) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
    }

    public List<IngredientDTO> getAllIngredients() {
        return this.ingredientRepository.findAll().stream().map(ingredientMapper::toMinDto).toList();
    }

    public IngredientDTO getIngredientById(long id) {
        return ingredientRepository.findById(id).map(ingredientMapper::toDto).orElseThrow();
    }

    public IngredientDTO saveIngredient(IngredientDTO ingToSave) {
        // перед сохранением проверяем, что нет ингредиентов с такими же наименованиями
        List<IngredientDTO> existingIng = getIngredientsWithSameNames(ingToSave);
        if (!existingIng.isEmpty()) {
            String ingIds = existingIng.stream().map(IngredientDTO::getId).map(String::valueOf).collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Перепроверьте все указанные наименования, есть совпадения с id " + ingIds);
        }
        // сохраняем ингредиент
        Ingredient savedIng = ingredientRepository.save(ingredientMapper.toEntity(ingToSave));
        return ingredientMapper.toDto(savedIng);
    }

    public void deleteIngredient(long id) {
        if (ingredientRepository.existsById(id)) {
            ingredientRepository.deleteById(id);
        } else throw new NoSuchElementException();
    }

    public List<IngredientDTO> getIngredientsByAllNames(List<String> names) {
        if (names == null || names.isEmpty()) return List.of();
        List<String> normalizedNames = names.stream().map(NormalizationUtils::normalize).toList();
        List<Ingredient> foundIngredients = ingredientRepository.findByAllNames(normalizedNames);
        return foundIngredients.stream().map(ingredientMapper::toDto).toList();
    }

    private List<IngredientDTO> getIngredientsWithSameNames(IngredientDTO ingToCheck) {
        // создаем список всех имен для поиска совпадений
        List<String> allNamesToCheck = new ArrayList<>(ingToCheck.getOtherNames());
        allNamesToCheck.add(ingToCheck.getInci());
        allNamesToCheck.add(ingToCheck.getTradeName());

        // получаем ингредиенты с этими именами из БД
        List<IngredientDTO> existingIng = new ArrayList<>(getIngredientsByAllNames(allNamesToCheck));

        //если ингредиент уже существует, удаляем его наименования
        if (ingToCheck.getId() != null) {
            existingIng.removeIf(i -> i.getId().equals(ingToCheck.getId()));
        }
        return existingIng;
    }
}
