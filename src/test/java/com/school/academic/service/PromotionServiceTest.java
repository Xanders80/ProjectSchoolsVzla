package com.school.academic.service;

import com.school.academic.entity.Promotion;
import com.school.academic.repository.PromotionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PromotionServiceTest {
    @Mock
    private PromotionRepository repository;

    @InjectMocks
    private PromotionService service;

    public PromotionServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Promotion p1 = new Promotion();
        p1.setStudentId("123");
        Promotion p2 = new Promotion();
        p2.setStudentId("456");
        when(repository.findAll()).thenReturn(Arrays.asList(p1, p2));
        List<Promotion> result = service.findAll();
        assertEquals(2, result.size());
        assertEquals("123", result.get(0).getStudentId());
    }

    @Test
    void testSave() {
        Promotion p = new Promotion();
        p.setStudentId("789");
        when(repository.save(p)).thenReturn(p);
        Promotion saved = service.save(p);
        assertEquals("789", saved.getStudentId());
    }
}
