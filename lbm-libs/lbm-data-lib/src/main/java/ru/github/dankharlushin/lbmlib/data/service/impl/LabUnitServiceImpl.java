package ru.github.dankharlushin.lbmlib.data.service.impl;

import org.springframework.stereotype.Service;
import ru.github.dankharlushin.lbmlib.data.entity.LabUnit;
import ru.github.dankharlushin.lbmlib.data.repository.LabUnitRepository;
import ru.github.dankharlushin.lbmlib.data.service.LabUnitService;

import java.util.List;

@Service
public class LabUnitServiceImpl implements LabUnitService {

    private final LabUnitRepository labUnitRepository;

    public LabUnitServiceImpl(final LabUnitRepository labUnitRepository) {
        this.labUnitRepository = labUnitRepository;
    }

    @Override
    public List<String> findAllLabApps() {
        return labUnitRepository.findAllLabAppNames();
    }

    @Override
    public List<LabUnit> findAll() {
        return labUnitRepository.findAll();
    }

    @Override
    public LabUnit getById(final int id) {
        return labUnitRepository.getReferenceById(id);
    }
}
