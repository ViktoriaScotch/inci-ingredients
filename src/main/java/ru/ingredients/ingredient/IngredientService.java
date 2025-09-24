package ru.ingredients.ingredient;

import org.springframework.stereotype.Service;
import ru.ingredients.utils.NormalizationUtils;

import java.util.List;
import java.util.NoSuchElementException;

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

    public void saveIngredient(IngredientDTO ingredient) {
        ingredientRepository.save(ingredientMapper.toEntity(ingredient));
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
}
