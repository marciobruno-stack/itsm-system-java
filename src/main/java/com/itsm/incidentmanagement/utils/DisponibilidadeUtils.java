package com.itsm.incidentmanagement.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class DisponibilidadeUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static boolean isDisponivel(String disponibilidadeJson, DayOfWeek dia, LocalTime hora) {
        if (disponibilidadeJson == null || disponibilidadeJson.isBlank()) return true; // assume disponível
        try {
            Map<String, List<String>> disponibilidade = mapper.readValue(disponibilidadeJson,
                    new TypeReference<Map<String, List<String>>>() {});
            String diaStr = dia.toString().toLowerCase(); // "monday", "tuesday", etc.
            List<String> intervalos = disponibilidade.get(diaStr);
            if (intervalos == null) return false;
            for (String intervalo : intervalos) {
                String[] parts = intervalo.split("-");
                LocalTime inicio = LocalTime.parse(parts[0]);
                LocalTime fim = LocalTime.parse(parts[1]);
                if (!hora.isBefore(inicio) && !hora.isAfter(fim)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return true; // se erro, assume disponível
        }
    }
}