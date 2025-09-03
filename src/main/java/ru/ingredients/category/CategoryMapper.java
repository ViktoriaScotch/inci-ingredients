package ru.ingredients.category;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO toDto(Category entity);

    Category toEntity(CategoryDTO dto);
}
