package ru.ingredients.ingredient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.ingredients.category.CategoryDTO;
import ru.ingredients.category.CategoryService;
import ru.ingredients.category.StringToCategoryDTOConverter;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = IngredientController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                value = StringToCategoryDTOConverter.class
        )
)
class IngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IngredientService ingredientService;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void getAllIngredients() throws Exception {
        //given
        IngredientDTO ing1 = new IngredientDTO().setId(1L).setInci("ing1");
        IngredientDTO ing2 = new IngredientDTO().setId(2L).setInci("ing2");
        List<IngredientDTO> ingredients = List.of(ing1, ing2);
        when(ingredientService.getAllIngredients()).thenReturn(ingredients);

        //when //then
        mockMvc.perform(get("/ingredients"))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredient/ingredients"))
                .andExpect(model().attribute("ingredients", ingredients));

        verify(ingredientService).getAllIngredients();
    }

    @Test
    void getNewIngredientForm() throws Exception {
        //given
        CategoryDTO cat1 = new CategoryDTO(1L, "cat1");
        CategoryDTO cat2 = new CategoryDTO(2L, "cat2");
        List<CategoryDTO> categories = List.of(cat1, cat2);
        when(categoryService.getAllCategories()).thenReturn(categories);

        //when //then
        mockMvc.perform(get("/ingredients/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredient/ingredient-new"))
                .andExpect(model().attribute("allCategories", categories));

        verify(categoryService).getAllCategories();
    }

    @Test
    void createIngredient() throws Exception {
        //given
        IngredientDTO ing = new IngredientDTO().setInci("ing");

        //when //then
        mockMvc.perform(post("/ingredients/new")
                        .param("inci", ing.getInci()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredients"));

        verify(ingredientService).saveIngredient(ing);
    }

    @Test
    void getIngredient() throws Exception {
        //given
        long id = 1L;
        IngredientDTO ing = new IngredientDTO().setId(id).setInci("ing");
        when(ingredientService.getIngredientById(id)).thenReturn(ing);

        //when //then
        mockMvc.perform(get("/ingredients/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredient/ingredient"))
                .andExpect(model().attribute("ingredient", ing));

        verify(ingredientService).getIngredientById(id);
    }

    @Test
    void getIngredient_redirectsWhenNotFound() throws Exception {
        //given
        long id = 0L;
        when(ingredientService.getIngredientById(id)).thenThrow(new NoSuchElementException());

        //when //then
        mockMvc.perform(get("/ingredients/" + id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredients"));

        verify(ingredientService).getIngredientById(id);
    }

    @Test
    void getEditIngredientForm() throws Exception {
        //given
        long id = 1L;
        IngredientDTO ing = new IngredientDTO().setId(id).setInci("ing");
        when(ingredientService.getIngredientById(id)).thenReturn(ing);

        CategoryDTO cat1 = new CategoryDTO(1L, "cat1");
        CategoryDTO cat2 = new CategoryDTO(2L, "cat2");
        List<CategoryDTO> categories = List.of(cat1, cat2);
        when(categoryService.getAllCategories()).thenReturn(categories);

        //when //then
        mockMvc.perform(get("/ingredients/" + id + "/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredient/ingredient-edit"))
                .andExpect(model().attribute("allCategories", categories))
                .andExpect(model().attribute("ingredient", ing));

        verify(categoryService).getAllCategories();
        verify(ingredientService).getIngredientById(id);
    }

    @Test
    void getEditIngredientForm_redirectsWhenNotFound() throws Exception {
        //given
        long id = 0L;
        when(ingredientService.getIngredientById(id)).thenThrow(new NoSuchElementException());

        //when //then
        mockMvc.perform(get("/ingredients/" + id + "/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredients"));

        verify(ingredientService).getIngredientById(id);
    }

    @Test
    void updateIngredient() throws Exception {
        //given
        long id = 1L;
        IngredientDTO ing = new IngredientDTO().setId(id).setInci("ing");

        //when //then
        mockMvc.perform(patch("/ingredients/" + id + "/edit")
                        .param("inci", ing.getInci()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredients/" + id));

        verify(ingredientService).saveIngredient(ing);
    }

    @Test
    void deleteIngredient() throws Exception {
        //given
        long id = 1L;

        //when //then
        mockMvc.perform(delete("/ingredients/" + id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredients"));

        verify(ingredientService).deleteIngredient(id);
    }
}