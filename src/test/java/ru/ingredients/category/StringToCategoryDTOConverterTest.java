package ru.ingredients.category;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionFailedException;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StringToCategoryDTOConverterTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private StringToCategoryDTOConverter converter;

    @Test
    void convert() {
        //given
        CategoryDTO cat = new CategoryDTO(1L, "Category");
        when(categoryService.getCategoryById(cat.getId())).thenReturn(cat);

        //when
        CategoryDTO result = converter.convert("1");

        //then
        assertThat(result).isEqualTo(cat);
        verify(categoryService).getCategoryById(cat.getId());
    }

    @Test
    void convert_ThrowsExceptionWhenCategoryNotFound() {
        //given
        long id = 0L;
        when(categoryService.getCategoryById(id)).thenThrow(new NoSuchElementException());

        //when //then
        assertThatThrownBy(() -> converter.convert("0"))
                .isInstanceOf(ConversionFailedException.class)
                .hasCauseInstanceOf(NoSuchElementException.class);
        verify(categoryService).getCategoryById(id);

    }

    @Test
    void convert_ThrowsExceptionWhenSourceIsNotNumber() {
        //given //when //then
        assertThatThrownBy(() -> converter.convert("not number"))
                .isInstanceOf(ConversionFailedException.class)
                .hasCauseInstanceOf(NumberFormatException.class);
        verifyNoInteractions(categoryService);
    }
}