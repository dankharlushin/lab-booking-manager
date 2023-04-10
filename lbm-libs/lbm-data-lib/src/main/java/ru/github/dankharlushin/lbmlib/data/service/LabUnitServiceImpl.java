package ru.github.dankharlushin.lbmlib.data.service;

import org.springframework.stereotype.Component;
import ru.github.dankharlushin.lbmlib.data.repository.LabUnitRepository;

import java.util.List;

@Component
public class LabUnitServiceImpl implements LabUnitService {

    private final LabUnitRepository labUnitRepository;

    public LabUnitServiceImpl(final LabUnitRepository labUnitRepository) {
        this.labUnitRepository = labUnitRepository;
    }

    @Override
    public List<String> findAllLabApps() {
        return labUnitRepository.findAllLabAppNames();
    }
}
