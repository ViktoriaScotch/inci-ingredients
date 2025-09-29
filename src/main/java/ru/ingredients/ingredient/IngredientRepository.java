package ru.ingredients.ingredient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    //TODO вынести все REGEXP_REPLACE сразу в БД
    @Query("""
            SELECT DISTINCT i
            FROM Ingredient i
            LEFT JOIN FETCH i.otherNames o
            WHERE LOWER(FUNCTION('REGEXP_REPLACE', FUNCTION('REGEXP_REPLACE', i.inci, '\\(.+?\\)', ''), '\\W+', '', 'g')) IN :normalizedNames
               OR LOWER(FUNCTION('REGEXP_REPLACE', FUNCTION('REGEXP_REPLACE', i.tradeName, '\\(.+?\\)', ''), '\\W+', '', 'g')) IN :normalizedNames
               OR LOWER(FUNCTION('REGEXP_REPLACE', FUNCTION('REGEXP_REPLACE', o, '\\(.+?\\)', ''), '\\W+', '', 'g')) IN :normalizedNames
            """)
    List<Ingredient> findByAllNames(@Param("normalizedNames") List<String> normalizedNames);
}
