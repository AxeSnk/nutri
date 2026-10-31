package com.nutri.app.core.data.local

import com.nutri.app.core.domain.model.Macros
import org.junit.Assert.assertEquals
import org.junit.Test

/** Юнит-тест чистых расчётов домена — без эмулятора. */
class MacroCalculatorTest {

    @Test
    fun `kcal formula 4-9-4`() {
        val macros = Macros(protein = 10f, fat = 10f, carbs = 10f)
        assertEquals(10 * 4 + 10 * 9 + 10 * 4, macros.kcalPer100.toInt())
    }

    @Test
    fun `portion recalc scales macros linearly`() {
        val per100 = Macros(20f, 5f, 50f)
        val for250 = com.nutri.app.core.domain.util.MacroCalculator.macrosFor(250f, per100)
        assertEquals(50f, for250.protein, 0.001f)
        assertEquals(12.5f, for250.fat, 0.001f)
        assertEquals(125f, for250.carbs, 0.001f)
    }

    @Test
    fun `portion calories use listed calories per 100 grams`() {
        assertEquals(247.5f, com.nutri.app.core.domain.util.MacroCalculator.kcalFor(150f, 165f), 0.001f)
    }

    @Test
    fun `sum of ingredients`() {
        val rice = 150f to Macros(7f, 1f, 78f)
        val chicken = 200f to Macros(25f, 3f, 0f)
        val total = com.nutri.app.core.domain.util.MacroCalculator.sum(listOf(rice, chicken))
        assertEquals(7 * 1.5f + 25 * 2f, total.protein, 0.001f)
        assertEquals(1 * 1.5f + 3 * 2f, total.fat, 0.001f)
    }

    @Test
    fun `goal validation rejects mismatch`() {
        val errors = com.nutri.app.core.domain.util.MacroCalculator.validateGoal(2000, 200, 10, 0)
        assert(errors.isNotEmpty())
    }
}
