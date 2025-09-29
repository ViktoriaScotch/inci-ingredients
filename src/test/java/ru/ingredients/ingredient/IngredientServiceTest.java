package ru.ingredients.ingredient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

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
    void getIngredientById_throwsNoSuchElementExceptionWhenNotFound() {
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
    void saveIngredient_returnsDTO() {
        //given
        Ingredient ing = new Ingredient().setId(1L).setInci("ing");
        IngredientDTO dto = ingredientMapper.toDto(ing);

        when(ingredientRepository.save(ing)).thenReturn(ing);

        //when
        IngredientDTO result = ingredientService.saveIngredient(dto);

        //then
        verify(ingredientRepository).save(ing);
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void saveIngredient_whenEditingExisting() {
        //given
        Ingredient existingIng = new Ingredient()
                .setId(1L)
                .setInci("inci")
                .setTradeName("trade");
        IngredientDTO dtoWithChanges = new IngredientDTO()
                .setId(1L)
                .setInci("inci")
                .setTradeName("changed trade");
        Ingredient savedIng = ingredientMapper.toEntity(dtoWithChanges);

        when(ingredientRepository.findByAllNames(anyList())).thenReturn(List.of(existingIng));

        //when
        ingredientService.saveIngredient(dtoWithChanges);

        //then
        verify(ingredientRepository).save(savedIng);
    }

    @ParameterizedTest
    @CsvSource({
            "existing inci, trade, other",
            "inci, existing trade, other",
            "inci, trade, existing other"
    })
    void saveIngredient_throwsIllegalArgumentExceptionWhenAnyNameAlreadyExists(String inci, String trade, String other) {
        //given
        Ingredient existingIng = new Ingredient().setId(1L)
                .setInci("existing inci")
                .setTradeName("existing trade")
                .setOtherNames(Set.of("existing other"));
        IngredientDTO dtoToSave = new IngredientDTO()
                .setInci(inci)
                .setTradeName(trade)
                .setOtherNames(Set.of(other));

        when(ingredientRepository.findByAllNames(anyList())).thenReturn(List.of(existingIng));

        //when //then
        assertThatThrownBy(() -> ingredientService.saveIngredient(dtoToSave))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(existingIng.getId().toString());
        verify(ingredientRepository).findByAllNames(anyList());
        verify(ingredientRepository, never()).save(any());
    }

    @Test
    void saveIngredient_throwsIllegalArgumentExceptionWhenNameAlreadyExistsInMultiple() {
        //given
        Ingredient existingIng1 = new Ingredient()
                .setId(1L)
                .setInci("existing1")
                .setTradeName("trade");
        Ingredient existingIng2 = new Ingredient()
                .setId(2L)
                .setInci("inci")
                .setTradeName("existing2");
        IngredientDTO dtoToSave = new IngredientDTO()
                .setInci(existingIng1.getInci())
                .setTradeName(existingIng2.getTradeName());

        when(ingredientRepository.findByAllNames(anyList())).thenReturn(List.of(existingIng1, existingIng2));

        //when //then
        assertThatThrownBy(() -> ingredientService.saveIngredient(dtoToSave))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContainingAll(existingIng1.getId().toString(), existingIng2.getId().toString());
        verify(ingredientRepository).findByAllNames(anyList());
        verify(ingredientRepository, never()).save(any());
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
    void deleteIngredient_throwsNoSuchElementExceptionWhenNotFound() {
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