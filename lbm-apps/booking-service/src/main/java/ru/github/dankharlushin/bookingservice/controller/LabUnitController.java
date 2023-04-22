package ru.github.dankharlushin.bookingservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.github.dankharlushin.lbmlib.data.dto.LabUnitDto;
import ru.github.dankharlushin.lbmlib.data.entity.LabUnit;
import ru.github.dankharlushin.lbmlib.data.service.LabUnitService;

import java.util.List;

@RestController
@RequestMapping("/lab")
public class LabUnitController {

    private static final Logger logger = LoggerFactory.getLogger(LabUnitController.class);

    private final LabUnitService labUnitService;

    public LabUnitController(final LabUnitService labUnitService) {
        this.labUnitService = labUnitService;
    }

    @GetMapping("/names")
    public List<LabUnitDto> getLabUnitNames() {
        logger.debug("Called getLabUnitNames");
        return labUnitService
                .findAll()
                .stream()
                .map(lu -> new LabUnitDto(lu.getId(), lu.getName()))
                .toList();
    }

    @GetMapping("/{id}/name")
    public LabUnitDto getLabUnitNameById(@PathVariable final Integer id) {
        logger.debug("Called getLabUnitNameById with id [{}]", id);
        final LabUnit labUnit = labUnitService.getById(id);
        return new LabUnitDto(labUnit.getId(), labUnit.getName());
    }
}
