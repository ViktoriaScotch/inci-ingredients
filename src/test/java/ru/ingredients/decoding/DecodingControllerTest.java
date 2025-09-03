package ru.ingredients.decoding;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.ingredients.category.StringToCategoryDTOConverter;
import ru.ingredients.ingredient.IngredientDTO;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DecodingController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                value = StringToCategoryDTOConverter.class
        )
)
class DecodingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DecodingService decodingService;

    @Test
    void postDecodingText() throws Exception {
        //given
        String text = "Ingredient";

        //when //then
        mockMvc.perform(post("/decoding")
                        .param("text", text))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/decoding"));
    }

    @Test
    void getDecoding() throws Exception {
        //given
        String text = "ingredient, unknown";
        IngredientDTO ing1 = new IngredientDTO().setInci("ingredient").setTradeName("trade name");
        IngredientDTO ing2 = new IngredientDTO().setTradeName("unknown");
        List<IngredientDTO> allIngredients = List.of(ing1, ing2);
        List<IngredientDTO> foundIngredients = List.of(ing1);
        Map<String, List<IngredientDTO>> ingByCat = Map.of("Category", foundIngredients);

        when(decodingService.findIng(text)).thenReturn(allIngredients);
        when(decodingService.groupByCat(foundIngredients)).thenReturn(ingByCat);

        //when //then
        mockMvc.perform(get("/decoding")
                        .flashAttr("text", text))
                .andExpect(status().isOk())
                .andExpect(view().name("decoding/decoding"))
                .andExpect(model().attribute("text", text))
                .andExpect(model().attribute("ingByCat", ingByCat))
                .andExpect(model().attribute("allIngredients", allIngredients))
                .andExpect(model().attribute("foundIngredients", foundIngredients));

        verify(decodingService).findIng(text);
        verify(decodingService).groupByCat(foundIngredients);
    }
}