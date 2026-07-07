package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.model.entity.Utilizador;
import com.itsm.incidentmanagement.repository.TecnicoRepository;
import com.itsm.incidentmanagement.repository.TicketRepository;
import com.itsm.incidentmanagement.repository.UtilizadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UtilizadorServiceTest {

    @Mock
    private UtilizadorRepository utilizadorRepository;

    @Mock
    private TecnicoRepository tecnicoRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private UtilizadorService utilizadorService;

    private Utilizador utilizador;

    @BeforeEach
    void setUp() {
        utilizador = new Utilizador();
        utilizador.setId(9L);
        utilizador.setUsername("admin");
        utilizador.setNome("Administrador");
        utilizador.setEmail("admin@itsm.com");
        utilizador.setRole("ADMIN");
    }

    @Test
    void testFindAll() {
        when(utilizadorRepository.findAll()).thenReturn(Arrays.asList(utilizador));

        List<Utilizador> result = utilizadorService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(utilizadorRepository).findAll();
    }

    @Test
    void testFindById() {
        when(utilizadorRepository.findById(9L)).thenReturn(Optional.of(utilizador));

        Utilizador found = utilizadorService.findById(9L);

        assertNotNull(found);
        assertEquals("admin", found.getUsername());
    }

    @Test
    void testFindByUsername() {
        when(utilizadorRepository.findByUsername("admin")).thenReturn(Optional.of(utilizador));

        Utilizador found = utilizadorService.findByUsername("admin");

        assertNotNull(found);
        assertEquals("Administrador", found.getNome());
    }

    @Test
    void testFindByEmail() {
        when(utilizadorRepository.findByEmail("admin@itsm.com")).thenReturn(Optional.of(utilizador));

        Utilizador found = utilizadorService.findByEmail("admin@itsm.com");

        assertNotNull(found);
        assertEquals("admin", found.getUsername());
    }

    @Test
    void testSave() {
        when(utilizadorRepository.save(any(Utilizador.class))).thenReturn(utilizador);

        Utilizador saved = utilizadorService.save(utilizador);

        assertNotNull(saved);
        verify(utilizadorRepository).save(any(Utilizador.class));
    }

    @Test
    void testDelete() {
        when(utilizadorRepository.findById(9L)).thenReturn(Optional.of(utilizador));
        when(tecnicoRepository.findByUtilizadorId(9L)).thenReturn(Optional.empty());
        when(ticketRepository.findByAbertoPorId(9L)).thenReturn(Collections.emptyList());
        doNothing().when(utilizadorRepository).deleteById(9L);

        utilizadorService.delete(9L);

        verify(utilizadorRepository).deleteById(9L);
    }
}
