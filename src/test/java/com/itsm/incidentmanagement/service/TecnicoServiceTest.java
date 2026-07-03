package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.model.entity.Tecnico;
import com.itsm.incidentmanagement.model.entity.Utilizador;
import com.itsm.incidentmanagement.repository.TecnicoRepository;
import com.itsm.incidentmanagement.repository.UtilizadorRepository;
import com.itsm.incidentmanagement.service.CacheService;
import com.itsm.incidentmanagement.service.TecnicoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TecnicoServiceTest {

    @Mock
    private TecnicoRepository tecnicoRepository;

    @Mock
    private UtilizadorRepository utilizadorRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private TecnicoService tecnicoService;

    private Tecnico tecnico;
    private Utilizador utilizador;

    @BeforeEach
    void setUp() {
        utilizador = new Utilizador();
        utilizador.setId(12L);
        utilizador.setNome("Joao Tecnico");
        utilizador.setUsername("tecnico1");

        tecnico = new Tecnico();
        tecnico.setId(1L);
        tecnico.setUtilizador(utilizador);
        tecnico.setCargaTrabalhoAtual(0);
        tecnico.setDisponibilidade("{\"segunda\":[\"09:00-18:00\"]}");
    }

    // ✅ TESTE 1: Listar técnicos
    @Test
    void testFindAll() {
        when(tecnicoRepository.findAll()).thenReturn(Arrays.asList(tecnico));

        List<Tecnico> result = tecnicoService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tecnicoRepository).findAll();
    }

    // ✅ TESTE 2: Buscar técnico por ID
    @Test
    void testFindById() {
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnico));

        Tecnico found = tecnicoService.findById(1L);

        assertNotNull(found);
        assertEquals("Joao Tecnico", found.getUtilizador().getNome());
    }

    // ✅ TESTE 3: Buscar técnico por ID - não encontrado
    @Test
    void testFindById_NotFound() {
        when(tecnicoRepository.findById(99L)).thenReturn(Optional.empty());

        Tecnico found = tecnicoService.findById(99L);

        assertNull(found);
    }

    // ✅ TESTE 4: Criar técnico
    @Test
    void testCreate() {
        when(utilizadorRepository.findById(12L)).thenReturn(Optional.of(utilizador));
        when(tecnicoRepository.save(any(Tecnico.class))).thenReturn(tecnico);

        Tecnico created = tecnicoService.create(tecnico);

        assertNotNull(created);
        assertEquals(0, created.getCargaTrabalhoAtual());
        verify(cacheService).loadTecnicos();
    }

    // ✅ TESTE 5: Criar técnico sem utilizador - erro
    @Test
    void testCreate_WithoutUser_ThrowsException() {
        Tecnico invalid = new Tecnico();

        assertThrows(RuntimeException.class, () -> {
            tecnicoService.create(invalid);
        });
    }

    // ✅ TESTE 6: Ordenar técnicos por carga
    @Test
    void testFindByOrderByCargaTrabalho() {
        when(tecnicoRepository.findByOrderByCargaTrabalhoAtualAsc())
                .thenReturn(Arrays.asList(tecnico));

        List<Tecnico> result = tecnicoService.findByOrderByCargaTrabalho();

        assertNotNull(result);
        verify(tecnicoRepository).findByOrderByCargaTrabalhoAtualAsc();
    }

    // ✅ TESTE 7: Deletar técnico
    @Test
    void testDelete() {
        doNothing().when(tecnicoRepository).deleteById(1L);

        tecnicoService.delete(1L);

        verify(tecnicoRepository).deleteById(1L);
        verify(cacheService).loadTecnicos();
    }
}