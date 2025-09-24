package ru.ingredients.ingredient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    private final IngredientMapper ingredientMapper = new IngredientMapperImpl();

    private IngredientService ingredientService;

    @BeforeEach
    void setUp() {
        ingredientService = new IngredientService(ingredientRepository, ingredientMapper);
    }

    @Test
    void getAllIngredients() {
        //given
        Ingredient ing1 = new Ingredient().setId(1L).setInci("ing1");
        Ingredient ing2 = new Ingredient().setId(2L).setInci("ing2");
        IngredientDTO dto1 = ingredientMapper.toMinDto(ing1);
        IngredientDTO dto2 = ingredientMapper.toMinDto(ing2);

        when(ingredientRepository.findAll()).thenReturn(List.of(ing1, ing2));

        //when
        List<IngredientDTO> result = ingredientService.getAllIngredients();

        //then
        assertThat(result).containsExactlyInAnyOrderElementsOf(List.of(dto1, dto2));
        verify(ingredientRepository).findAll();
    }

    @Test
    void getIngredientById() {
        //given
        long id = 1L;
        Ingredient ing = new Ingredient().setId(id).setInci("ing");
        IngredientDTO dto = ingredientMapper.toDto(ing);
        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ing));

        //when
        IngredientDTO result = ingredientService.getIngredientById(id);

        //then
        assertThat(result).isEqualTo(dto);
        verify(ingredientRepository).findById(id);
    }

    @Test
    void getIngredientById_ThrowsExceptionWhenNotFound() {
        //given
        long id = 0L;
        when(ingredientRepository.findById(id)).thenReturn(Optional.empty());

        //when //then
        assertThatThrownBy(() -> ingredientService.getIngredientById(id))
                .isInstanceOf(NoSuchElementException.class);
        verify(ingredientRepository).findById(id);
    }

    @Test
    void saveIngredient() {
        //given
        Ingredient ing = new Ingredient().setId(1L).setInci("ing");
        IngredientDTO dto = ingredientMapper.toDto(ing);

        //when
        ingredientService.saveIngredient(dto);

        //then
        verify(ingredientRepository).save(ing);
    }

    @Test
    void deleteIngredient() {
        //given
        long id = 1L;
        when(ingredientRepository.existsById(id)).thenReturn(true);

        //when
        ingredientService.deleteIngredient(id);

        //then
        verify(ingredientRepository).existsById(id);
        verify(ingredientRepository).deleteById(id);
    }

    @Test
    void deleteIngredient_ThrowsExceptionWhenNotFound() {
        //given
        long id = 0L;
        when(ingredientRepository.existsById(id)).thenReturn(false);

        //when //then
        assertThatThrownBy(() -> ingredientService.deleteIngredient(id))
                .isInstanceOf(NoSuchElementException.class);
        verify(ingredientRepository).existsById(id);
        verify(ingredientRepository, never()).deleteById(id);
    }

    @Test
    void getIngredientsByAllNames() {
        //given
        List<String> input = List.of("ing1", "ing2");
        Ingredient ing1 = new Ingredient().setId(1L).setInci("ing1");
        Ingredient ing2 = new Ingredient().setId(2L).setInci("ing2");
        IngredientDTO dto1 = ingredientMapper.toMinDto(ing1);
        IngredientDTO dto2 = ingredientMapper.toMinDto(ing2);

        when(ingredientRepository.findByAllNames(input)).thenReturn(List.of(ing1, ing2));

        //when
        List<IngredientDTO> result = ingredientService.getIngredientsByAllNames(input);

        //then
        assertThat(result).containsExactlyInAnyOrder(dto1, dto2);
    }

    @Test
    void getIngredientsByAllNames_usesNormalization() {
        //given
        List<String> input = List.of("Name (with parentheses)", "Name-123");
        List<String> normalized = List.of("name", "name123");

        //when
        ingredientService.getIngredientsByAllNames(input);

        //then
        verify(ingredientRepository).findByAllNames(normalized);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getIngredientsByAllNames_returnsEmptyWhenNullAndEmptySource(List<String> source) {
        //when
        List<IngredientDTO> result = ingredientService.getIngredientsByAllNames(source);

        //then
        assertThat(result).isEmpty();
        verifyNoInteractions(ingredientRepository);
    }

    @Test
    void getIngredientsByAllNames_returnsEmptyWhenNotFound() {
        //given
        List<String> input = List.of("unknown");
        when(ingredientRepository.findByAllNames(input)).thenReturn(List.of());

        //when
        List<IngredientDTO> result = ingredientService.getIngredientsByAllNames(input);

        //then
        assertThat(result).isEmpty();
        verify(ingredientRepository).findByAllNames(input);
    }
}