package ru.ingredients.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper = new CategoryMapperImpl();

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository, categoryMapper);
    }

    @Test
    void getAllCategories() {
        //given
        Category cat1 = new Category(1L, "cat1");
        Category cat2 = new Category(2L, "cat2");
        CategoryDTO dto1 = categoryMapper.toDto(cat1);
        CategoryDTO dto2 = categoryMapper.toDto(cat2);

        when(categoryRepository.findAll()).thenReturn(List.of(cat1, cat2));

        //when
        List<CategoryDTO> result = categoryService.getAllCategories();

        //then
        assertThat(result).containsExactlyInAnyOrderElementsOf(List.of(dto1, dto2));
        verify(categoryRepository).findAll();
    }

    @Test
    void getCategoryById() {
        //given
        long id = 1L;
        Category cat = new Category(id, "cat");
        CategoryDTO dto = categoryMapper.toDto(cat);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(cat));

        //when
        CategoryDTO result = categoryService.getCategoryById(id);

        //then
        assertThat(result).isEqualTo(dto);
        verify(categoryRepository).findById(id);
    }

    @Test
    void getCategoryById_ThrowsExceptionWhenNotFound() {
        //given
        long id = 0L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        //when //then
        assertThatThrownBy(() -> categoryService.getCategoryById(id))
                .isInstanceOf(NoSuchElementException.class);
        verify(categoryRepository).findById(id);
    }
}