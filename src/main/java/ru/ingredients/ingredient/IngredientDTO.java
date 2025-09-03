package ru.ingredients.ingredient;

import ru.ingredients.category.CategoryDTO;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class IngredientDTO {

    private Long id;

    private String inci;

    private String tradeName;

    private String description;

    private Set<String> otherNames = new HashSet<>();

    private Set<CategoryDTO> categories = new HashSet<>();

    public IngredientDTO() {
    }

    public IngredientDTO(
            Long id,
            String inci,
            String tradeName,
            String description,
            Set<String> otherNames,
            Set<CategoryDTO> categories) {
        this.id = id;
        this.inci = inci;
        this.tradeName = tradeName;
        this.description = description;
        this.otherNames = otherNames;
        this.categories = categories;
    }

    public Long getId() {
        return id;
    }

    public IngredientDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getInci() {
        return inci;
    }

    public IngredientDTO setInci(String inci) {
        this.inci = inci;
        return this;
    }

    public String getTradeName() {
        return tradeName;
    }

    public IngredientDTO setTradeName(String tradeName) {
        this.tradeName = tradeName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public IngredientDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public Set<String> getOtherNames() {
        return otherNames;
    }

    public IngredientDTO setOtherNames(Set<String> otherNames) {
        this.otherNames = otherNames;
        return this;
    }

    public Set<CategoryDTO> getCategories() {
        return categories;
    }

    public IngredientDTO setCategories(Set<CategoryDTO> categories) {
        this.categories = categories;
        return this;
    }

    @Override
    public String toString() {
        return "IngredientDTO{" +
                "id=" + id +
                ", inci='" + inci + '\'' +
                ", tradeName='" + tradeName + '\'' +
                ", description='" + description + '\'' +
                ", otherNames=" + otherNames +
                ", categories=" + categories +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IngredientDTO dto = (IngredientDTO) o;
        return Objects.equals(id, dto.id) &&
                Objects.equals(inci, dto.inci) &&
                Objects.equals(tradeName, dto.tradeName) &&
                Objects.equals(description, dto.description) &&
                Objects.equals(otherNames, dto.otherNames) &&
                Objects.equals(categories, dto.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, inci, tradeName, description, otherNames, categories);
    }
}
