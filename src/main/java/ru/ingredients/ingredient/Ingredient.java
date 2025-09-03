package ru.ingredients.ingredient;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.ingredients.category.Category;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String inci;

    @Column(nullable = false)
    private String tradeName;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String description;

    @ElementCollection
    private Set<String> otherNames = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "ingredient_category",
            joinColumns = @JoinColumn(name = "ingredient_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    public Ingredient() {
    }

    public Ingredient(String inci, String tradeName, String description, Set<String> otherNames, Set<Category> categories) {
        this.inci = inci;
        this.tradeName = tradeName;
        this.description = description;
        this.otherNames = otherNames;
        this.categories = categories;
    }

    public Long getId() {
        return id;
    }

    public Ingredient setId(Long id) {
        this.id = id;
        return this;
    }

    public String getInci() {
        return inci;
    }

    public Ingredient setInci(String inci) {
        this.inci = inci;
        return this;
    }

    public String getTradeName() {
        return tradeName;
    }

    public Ingredient setTradeName(String tradeName) {
        this.tradeName = tradeName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Ingredient setDescription(String description) {
        this.description = description;
        return this;
    }

    public Set<String> getOtherNames() {
        return otherNames;
    }

    public Ingredient setOtherNames(Set<String> otherNames) {
        this.otherNames = otherNames;
        return this;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public Ingredient setCategories(Set<Category> categories) {
        this.categories = categories;
        return this;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
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
        Ingredient that = (Ingredient) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(inci, that.inci) &&
                Objects.equals(tradeName, that.tradeName) &&
                Objects.equals(description, that.description) &&
                Objects.equals(otherNames, that.otherNames) &&
                Objects.equals(categories, that.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, inci, tradeName, description, otherNames, categories);
    }
}
