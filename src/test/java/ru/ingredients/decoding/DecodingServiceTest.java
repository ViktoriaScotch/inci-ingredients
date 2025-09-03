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
import ru.ingredients.ingredient.*;

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
    private IngredientRepository ingredientRepository;

    private final IngredientMapper ingredientMapper = new IngredientMapperImpl();

    private DecodingService decodingService;

    private Ingredient ing1;
    private Ingredient ing2;

    private IngredientDTO dto1;
    private IngredientDTO dto2;

    @BeforeEach
    void setUp() {
        decodingService = new DecodingService(ingredientRepository, ingredientMapper);

        ing1 = new Ingredient().setInci("Name (with parentheses)").setOtherNames(Set.of("Other name"));
        ing2 = new Ingredient().setInci("Name-123").setTradeName("Trade name");

        dto1 = ingredientMapper.toDto(ing1);
        dto2 = ingredientMapper.toDto(ing2);
    }

    @Test
    void findIng_SearchForAllNames() {
        //given
        Ingredient ing3 = new Ingredient().setInci("INCI name");
        IngredientDTO dto3 = ingredientMapper.toDto(ing3);

        when(ingredientRepository.findByAllNames(anyList())).thenReturn(List.of(ing1, ing2, ing3));

        //when
        List<IngredientDTO> result = decodingService.findIng("Other name, Trade name, INCI name");

        //then
        assertThat(result).containsExactlyInAnyOrderElementsOf(List.of(dto1, dto2, dto3));
        verify(ingredientRepository).findByAllNames(anyList());
    }

    @Test
    void findIng_ReturnsIngInCorrectOrder() {
        //given
        Ingredient ing3 = new Ingredient().setInci("INCI name");
        IngredientDTO dto3 = ingredientMapper.toDto(ing3);
        when(ingredientRepository.findByAllNames(anyList())).thenReturn(List.of(ing1, ing2, ing3));

        //when
        List<IngredientDTO> result = decodingService.findIng("INCI name, Trade name, Other name");

        //then
        assertThat(result).containsExactlyElementsOf(List.of(dto3, dto2, dto1));
        verify(ingredientRepository).findByAllNames(anyList());
    }

    @Test
    void findIng_ForNormalizeNames() {
        //given
        when(ingredientRepository.findByAllNames(List.of("name", "name123"))).thenReturn(List.of(ing1, ing2));

        //when
        List<IngredientDTO> result = decodingService.findIng("Name (with parentheses), Name-123");

        //then
        assertThat(result).containsExactlyInAnyOrderElementsOf(List.of(dto1, dto2));
        verify(ingredientRepository).findByAllNames(List.of("name", "name123"));
    }

    @Test
    void findIng_ReturnsNewIngWhenNotFound() {
        //given
        String unknown = "Unknown";
        IngredientDTO dto = new IngredientDTO().setTradeName(unknown);
        when(ingredientRepository.findByAllNames(anyList())).thenReturn(List.of());

        //when
        List<IngredientDTO> result = decodingService.findIng(unknown);

        //then
        assertThat(result).containsExactlyInAnyOrderElementsOf(List.of(dto));
        verify(ingredientRepository).findByAllNames(anyList());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void findIng_ReturnsEmptyForNullOrBlank(String source) {
        //given //when
        List<IngredientDTO> result = decodingService.findIng(source);

        //then
        assertThat(result).isEmpty();
        verifyNoInteractions(ingredientRepository);
    }

    @Test
    void groupByCat() {
        //given
        CategoryDTO cat1 = new CategoryDTO(1L, "Category 1");
        CategoryDTO cat2 = new CategoryDTO(2L, "Category 2");
        dto1.setCategories(Set.of(cat1, cat2));
        dto2.setCategories(Set.of(cat1));

        //when
        Map<String, List<IngredientDTO>> result = decodingService.groupByCat(List.of(dto1, dto2));

        //then
        assertThat(result).hasSize(2);
        assertThat(result.get(cat1.getName())).containsExactlyInAnyOrderElementsOf(List.of(dto2, dto1));
        assertThat(result.get(cat2.getName())).containsExactlyInAnyOrderElementsOf(List.of(dto1));
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