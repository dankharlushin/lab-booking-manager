package ru.github.dankharlushin.lbmlib.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.github.dankharlushin.lbmlib.data.entity.LabUnit;

import java.util.List;

@Repository
public interface LabUnitRepository extends JpaRepository<LabUnit, Integer> {

    @Query("select l.appName from LabUnit l")
    List<String> findAllLabAppNames();
}
