package ru.ingredients.decoding;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ingredients.category.CategoryDTO;
import ru.ingredients.ingredient.IngredientDTO;
import ru.ingredients.ingredient.IngredientService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DecodingServiceTest {

    @Mock
    private IngredientService ingredientService;

    private DecodingService decodingService;

    private IngredientDTO ing1;
    private IngredientDTO ing2;

    @BeforeEach
    void setUp() {
        decodingService = new DecodingService(ingredientService);

        ing1 = new IngredientDTO().setInci("Name (with parentheses)").setOtherNames(Set.of("Other name"));
        ing2 = new IngredientDTO().setInci("Name-123").setTradeName("Trade name");
    }

    @Test
    void decode_searchForAllNames() {
        //given
        IngredientDTO ing3 = new IngredientDTO().setInci("INCI name");

        when(ingredientService.getIngredientsByAllNames(anyList())).thenReturn(List.of(ing1, ing2, ing3));

        //when
        List<IngredientDTO> result = decodingService.decode("Other name, Trade name, INCI name");

        //then
        assertThat(result).containsExactlyInAnyOrderElementsOf(List.of(ing1, ing2, ing3));
        verify(ingredientService).getIngredientsByAllNames(anyList());
    }

    @Test
    void decode_returnsIngInCorrectOrder() {
        //given
        IngredientDTO ing3 = new IngredientDTO().setInci("INCI name");
        when(ingredientService.getIngredientsByAllNames(anyList())).thenReturn(List.of(ing1, ing2, ing3));

        //when
        List<IngredientDTO> result = decodingService.decode("INCI name, Trade name, Other name");

        //then
        assertThat(result).containsExactlyElementsOf(List.of(ing3, ing2, ing1));
        verify(ingredientService).getIngredientsByAllNames(anyList());
    }

    @Test
    void decode_returnsNewIngWhenNotFound() {
        //given
        String unknown = "Unknown";
        IngredientDTO dto = new IngredientDTO().setTradeName(unknown);
        when(ingredientService.getIngredientsByAllNames(anyList())).thenReturn(List.of());

        //when
        List<IngredientDTO> result = decodingService.decode(unknown);

        //then
        assertThat(result).containsExactlyInAnyOrderElementsOf(List.of(dto));
        verify(ingredientService).getIngredientsByAllNames(anyList());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void decode_returnsEmptyForNullOrBlank(String source) {
        //given //when
        List<IngredientDTO> result = decodingService.decode(source);

        //then
        assertThat(result).isEmpty();
        verifyNoInteractions(ingredientService);
    }

    @Test
    void groupByCat() {
        //given
        CategoryDTO cat1 = new CategoryDTO(1L, "Category 1");
        CategoryDTO cat2 = new CategoryDTO(2L, "Category 2");
        ing1.setCategories(Set.of(cat1, cat2));
        ing2.setCategories(Set.of(cat1));

        //when
        Map<String, List<IngredientDTO>> result = decodingService.groupByCat(List.of(ing1, ing2));

        //then
        assertThat(result).hasSize(2);
        assertThat(result.get(cat1.getName())).containsExactlyInAnyOrderElementsOf(List.of(ing2, ing1));
        assertThat(result.get(cat2.getName())).containsExactlyInAnyOrderElementsOf(List.of(ing1));
    }

    @ParameterizedTest
    @MethodSource("provideIngredientsForEmptyResult")
    void groupByCat_ReturnsEmpty(List<IngredientDTO> source) {
        //given //when
        Map<String, List<IngredientDTO>> result = decodingService.groupByCat(source);

        //then
        assertThat(result).isEmpty();
    }

    private static Stream<Arguments> provideIngredientsForEmptyResult() {
        IngredientDTO ingWithoutCategory = new IngredientDTO().setInci("Ingredient");

        return Stream.of(
                Arguments.of(List.of(ingWithoutCategory)),
                Arguments.of(List.of())
        );
    }
}