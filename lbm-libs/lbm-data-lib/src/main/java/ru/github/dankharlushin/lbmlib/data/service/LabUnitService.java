package ru.github.dankharlushin.lbmlib.data.service;

import ru.github.dankharlushin.lbmlib.data.entity.LabUnit;

import java.util.List;

public interface LabUnitService {

    List<LabUnit> findAll();

    List<String> findAllLabApps();

    LabUnit getById(final int id);
}
