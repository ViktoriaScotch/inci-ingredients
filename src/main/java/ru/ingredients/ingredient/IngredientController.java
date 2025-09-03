package ru.ingredients.ingredient;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.ingredients.category.CategoryService;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;
    private final CategoryService categoryService;

    public IngredientController(IngredientService ingredientService, CategoryService categoryService) {
        this.ingredientService = ingredientService;
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public String getAllIngredients(Model model) {
        model.addAttribute("ingredients", ingredientService.getAllIngredients());
        return "ingredient/ingredients";
    }

    @GetMapping("/new")
    public String getNewIngredientForm(@ModelAttribute("ingredient") IngredientDTO ignoredIngredient, Model model) {
        model.addAttribute("allCategories", categoryService.getAllCategories());
        return "ingredient/ingredient-new";
    }

    @PostMapping("/new")
    public String createIngredient(@ModelAttribute("ingredient") IngredientDTO ingredient) {
        ingredientService.saveIngredient(ingredient);
        return "redirect:/ingredients";
    }

    @GetMapping("/{id}")
    public String getIngredient(@PathVariable(value = "id") long id, Model model) {
        IngredientDTO ingredient;
        try {
            ingredient = ingredientService.getIngredientById(id);
        } catch (NoSuchElementException e) {
            return "redirect:/ingredients";
        }
        model.addAttribute("ingredient", ingredient);
        return "ingredient/ingredient";
    }

    @GetMapping("/{id}/edit")
    public String getEditIngredientForm(@PathVariable(value = "id") long id, Model model) {
        IngredientDTO ingredient;
        try {
            ingredient = ingredientService.getIngredientById(id);
        } catch (NoSuchElementException e) {
            return "redirect:/ingredients";
        }
        model.addAttribute("allCategories", categoryService.getAllCategories());
        model.addAttribute("ingredient", ingredient);
        return "ingredient/ingredient-edit";
    }

    @PatchMapping(value = "/{id}/edit")
    public String updateIngredient(@ModelAttribute("ingredient") IngredientDTO ingredient) {
        ingredientService.saveIngredient(ingredient);
        return "redirect:/ingredients/{id}";
    }

    @DeleteMapping("/{id}")
    public String deleteIngredient(@PathVariable(value = "id") long id) {
        ingredientService.deleteIngredient(id);
        return "redirect:/ingredients";
    }
}
