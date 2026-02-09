package com.school.academic.service;

import com.school.academic.entity.CurriculumGrid;
import com.school.academic.repository.CurriculumGridRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurriculumGridServiceTest {
    @Mock
    private CurriculumGridRepository repository;

    @InjectMocks
    private CurriculumGridService service;

    public CurriculumGridServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveValid() {
        CurriculumGrid grid = new CurriculumGrid();
        grid.setGradeLevel(1);
        grid.setStudyPlan(mock(com.school.academic.entity.StudyPlan.class));
        when(repository.findAll()).thenReturn(List.of());
        when(repository.save(grid)).thenReturn(grid);
        CurriculumGrid saved = service.save(grid);
        assertEquals(1, saved.getGradeLevel());
    }

    @Test
    void testSaveInvalid() {
        CurriculumGrid grid = new CurriculumGrid();
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.save(grid));
        assertTrue(ex.getMessage().contains("obligatorio"));
    }
}
