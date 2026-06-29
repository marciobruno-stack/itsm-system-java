package com.itsm.incidentmanagement.utils;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class DisponibilidadeUtilsTest {

    // ✅ TESTE 1: Disponibilidade válida
    @Test
    void testIsDisponivel_Valid() {
        String disponibilidade = "{\"segunda\":[\"09:00-18:00\"]}";
        DayOfWeek dia = DayOfWeek.MONDAY;
        LocalTime hora = LocalTime.of(10, 0);

        boolean result = DisponibilidadeUtils.isDisponivel(disponibilidade, dia, hora);

        assertTrue(result);
    }

    // ✅ TESTE 2: Fora do horário
    @Test
    void testIsDisponivel_OutOfHours() {
        String disponibilidade = "{\"segunda\":[\"09:00-18:00\"]}";
        DayOfWeek dia = DayOfWeek.MONDAY;
        LocalTime hora = LocalTime.of(20, 0);

        boolean result = DisponibilidadeUtils.isDisponivel(disponibilidade, dia, hora);

        assertFalse(result);
    }

    // ✅ TESTE 3: Dia não disponível
    @Test
    void testIsDisponivel_DayNotAvailable() {
        String disponibilidade = "{\"segunda\":[\"09:00-18:00\"]}";
        DayOfWeek dia = DayOfWeek.TUESDAY;
        LocalTime hora = LocalTime.of(10, 0);

        boolean result = DisponibilidadeUtils.isDisponivel(disponibilidade, dia, hora);

        assertFalse(result);
    }

    // ✅ TESTE 4: Disponibilidade nula - assume disponível
    @Test
    void testIsDisponivel_NullDisponibilidade() {
        DayOfWeek dia = DayOfWeek.MONDAY;
        LocalTime hora = LocalTime.of(10, 0);

        boolean result = DisponibilidadeUtils.isDisponivel(null, dia, hora);

        assertTrue(result);
    }

    // ✅ TESTE 5: JSON inválido - assume disponível
    @Test
    void testIsDisponivel_InvalidJson() {
        String disponibilidade = "INVALIDO";
        DayOfWeek dia = DayOfWeek.MONDAY;
        LocalTime hora = LocalTime.of(10, 0);

        boolean result = DisponibilidadeUtils.isDisponivel(disponibilidade, dia, hora);

        assertTrue(result);
    }
}