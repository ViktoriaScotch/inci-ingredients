package ru.ingredients.category;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class StringToCategoryDTOConverter implements Converter<String, CategoryDTO> {

    private final CategoryService categoryService;

    public StringToCategoryDTOConverter(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public CategoryDTO convert(@NonNull String source) {
        try {
            long id = Long.parseLong(source);
            return categoryService.getCategoryById(id);
        } catch (Exception e) {
            throw new ConversionFailedException(
                    TypeDescriptor.valueOf(String.class),
                    TypeDescriptor.valueOf(CategoryDTO.class),
                    source,
                    e
            );
        }
    }
}