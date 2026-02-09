package com.school.academic.service;

import com.school.academic.entity.MaterialDidactico;
import com.school.academic.repository.MaterialDidacticoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MaterialDidacticoServiceTest {
    @Mock
    private MaterialDidacticoRepository repository;

    @InjectMocks
    private MaterialDidacticoService service;

    public MaterialDidacticoServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        MaterialDidactico m1 = new MaterialDidactico();
        m1.setTitulo("Libro A");
        MaterialDidactico m2 = new MaterialDidactico();
        m2.setTitulo("Libro B");
        when(repository.findAll()).thenReturn(Arrays.asList(m1, m2));
        List<MaterialDidactico> result = service.findAll();
        assertEquals(2, result.size());
        assertEquals("Libro A", result.get(0).getTitulo());
    }

    @Test
    void testSave() {
        MaterialDidactico m = new MaterialDidactico();
        m.setTitulo("Libro C");
        when(repository.save(m)).thenReturn(m);
        MaterialDidactico saved = service.save(m);
        assertEquals("Libro C", saved.getTitulo());
    }
}
