package ru.github.dankharlushin.lbmlib.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.github.dankharlushin.lbmlib.data.entity.LabUnit;

@Repository
public interface LabUnitRepository extends JpaRepository<LabUnit, Integer> {
}
