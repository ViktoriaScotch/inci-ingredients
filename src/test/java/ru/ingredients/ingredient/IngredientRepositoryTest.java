package ru.ingredients.ingredient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.ingredients.AbstractTestcontainers;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IngredientRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        ingredientRepository.deleteAll();
    }

    @ParameterizedTest
    @CsvSource({
            "inci, trade, other, inci", //SearchByInci
            "inci, trade, other, trade", //SearchByTradeName
            "inci, trade, other, other", //SearchByOtherNames
            "inci (with parentheses), trade, other, inci", //IgnoreParenthesesForInci
            "inci, trade (with parentheses), other, trade", //IgnoreParenthesesForTradeName
            "inci, trade, other (with parentheses), other", //IgnoreParenthesesForOtherName
            "INCI Name-123, trade, other, inciname123", //IgnoreNonAlphanumericForInci
            "inci, Trade Name-123, other, tradename123", //IgnoreNonAlphanumericForTradeName
            "inci, trade, Other Name-123, othername123" //IgnoreNonAlphanumericForOtherName
    })
    void findByAllNames(String inciName, String tradeName, String otherName, String search) {
        //given
        Ingredient ing = new Ingredient().setInci(inciName).setTradeName(tradeName).setOtherNames(Set.of(otherName));
        em.persistAndFlush(ing);

        //when
        List<Ingredient> result = ingredientRepository.findByAllNames(List.of(search));

        //then
        assertThat(result).containsExactly(ing);
    }

    @Test
    void findByAllNames_ReturnsMultipleIngredients() {
        //given
        Ingredient ing1 = new Ingredient().setInci("ing1").setTradeName("инг1");
        em.persist(ing1);
        Ingredient ing2 = new Ingredient().setInci("ing2").setTradeName("инг2");
        em.persist(ing2);
        em.flush();

        //when
        List<Ingredient> result = ingredientRepository.findByAllNames(List.of("ing1", "ing2"));

        //then
        assertThat(result).containsExactlyInAnyOrder(ing1, ing2);
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = "Unknown")
    void findByAllNames_ReturnsEmpty(String source) {
        //given
        Ingredient ing = new Ingredient().setInci("INCI name").setTradeName("Trade name");
        em.persistAndFlush(ing);

        //when
        List<Ingredient> result = ingredientRepository.findByAllNames(List.of(source));

        //then
        assertThat(result).isEmpty();
    }
}