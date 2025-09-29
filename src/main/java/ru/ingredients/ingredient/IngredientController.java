package ru.ingredients.ingredient;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    public String createIngredient(@Valid @ModelAttribute("ingredient") IngredientDTO ingredient, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "ingredient/ingredient-new";
        }
        try {
            ingredient = ingredientService.saveIngredient(ingredient);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("duplicate", e.getMessage());
            return "ingredient/ingredient-new";
        }
        return "redirect:/ingredients/" + ingredient.getId();
    }

    @GetMapping("/{id}")
    public String getIngredient(@PathVariable(value = "id") long id, Model model, RedirectAttributes redirectAttributes) {
        IngredientDTO ingredient;
        try {
            ingredient = ingredientService.getIngredientById(id);
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ингредиент не найден");
            return "redirect:/ingredients";
        }
        model.addAttribute("ingredient", ingredient);
        return "ingredient/ingredient";
    }

    @GetMapping("/{id}/edit")
    public String getEditIngredientForm(@PathVariable(value = "id") long id, Model model, RedirectAttributes redirectAttributes) {
        IngredientDTO ingredient;
        try {
            ingredient = ingredientService.getIngredientById(id);
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ингредиент не найден");
            return "redirect:/ingredients";
        }
        model.addAttribute("allCategories", categoryService.getAllCategories());
        model.addAttribute("ingredient", ingredient);
        return "ingredient/ingredient-edit";
    }

    @PatchMapping(value = "/{id}/edit")
    public String updateIngredient(@Valid @ModelAttribute("ingredient") IngredientDTO ingredient, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "ingredient/ingredient-edit";
        }
        try {
            ingredientService.saveIngredient(ingredient);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("duplicate", e.getMessage());
            return "ingredient/ingredient-edit";
        }
        return "redirect:/ingredients/{id}";
    }

    @DeleteMapping("/{id}")
    public String deleteIngredient(@PathVariable(value = "id") long id) {
        ingredientService.deleteIngredient(id);
        return "redirect:/ingredients";
    }
}
