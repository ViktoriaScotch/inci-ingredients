package ru.ingredients.ingredient;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.ingredients.category.CategoryMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {CategoryMapper.class})
public interface IngredientMapper {

    IngredientDTO toDto(Ingredient entity);

    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "otherNames", ignore = true)
    IngredientDTO toMinDto(Ingredient entity);

    Ingredient toEntity(IngredientDTO dto);
}
