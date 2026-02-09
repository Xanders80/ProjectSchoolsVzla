package com.school.academic.service;

import com.school.academic.entity.TeacherDevelopment;
import com.school.academic.repository.TeacherDevelopmentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherDevelopmentServiceTest {
    @Mock
    private TeacherDevelopmentRepository repository;

    @InjectMocks
    private TeacherDevelopmentService service;

    public TeacherDevelopmentServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        TeacherDevelopment t1 = new TeacherDevelopment();
        t1.setTitle("Curso A");
        TeacherDevelopment t2 = new TeacherDevelopment();
        t2.setTitle("Curso B");
        when(repository.findAll()).thenReturn(Arrays.asList(t1, t2));
        List<TeacherDevelopment> result = service.findAll();
        assertEquals(2, result.size());
        assertEquals("Curso A", result.get(0).getTitle());
    }

    @Test
    void testSave() {
        TeacherDevelopment t = new TeacherDevelopment();
        t.setTitle("Curso C");
        when(repository.save(t)).thenReturn(t);
        TeacherDevelopment saved = service.save(t);
        assertEquals("Curso C", saved.getTitle());
    }
}
