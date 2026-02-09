package com.school.academic.service;

import com.school.academic.entity.MaterialDidactico;
import org.springframework.stereotype.Service;
// ...existing code...
import com.school.academic.repository.MaterialDidacticoRepository;
import java.util.List;

@Service
public class MaterialDidacticoService {
    private final MaterialDidacticoRepository materialDidacticoRepository;

    public MaterialDidacticoService(MaterialDidacticoRepository materialDidacticoRepository) {
        this.materialDidacticoRepository = materialDidacticoRepository;
    }

    public MaterialDidactico save(@org.springframework.lang.NonNull MaterialDidactico material) {
        return materialDidacticoRepository.save(material);
    }

    public List<MaterialDidactico> findAll() {
        return materialDidacticoRepository.findAll();
    }

    public MaterialDidactico findById(@org.springframework.lang.NonNull Long id) {
        return materialDidacticoRepository.findById(id).orElse(null);
    }

    public void delete(@org.springframework.lang.NonNull Long id) {
        materialDidacticoRepository.deleteById(id);
    }
}
