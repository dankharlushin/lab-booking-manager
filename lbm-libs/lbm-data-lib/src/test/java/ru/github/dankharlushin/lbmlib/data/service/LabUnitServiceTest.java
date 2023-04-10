package ru.github.dankharlushin.lbmlib.data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.github.dankharlushin.lbmlib.data.config.DataLibConfig;
import ru.github.dankharlushin.lbmlib.data.entity.LabUnit;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DataJpaTest
@Import(DataLibConfig.class)
class LabUnitServiceTest {

    @Autowired
    private LabUnitService labUnitService;
    @Autowired
    private TestEntityManager entityManager;

    private List<LabUnit> testLabs;

    @BeforeEach
    void setUp() {
        testLabs = createTestLabs();
        testLabs.forEach(entityManager::persist);
    }

    @Test
    void testFindAllLabApps() {
        final List<String> allLabApps = labUnitService.findAllLabApps();

        assertThat(allLabApps.size(), is(2));
        assertThat(allLabApps.get(0), is("appName0"));
        assertThat(allLabApps.get(1), is("appName1"));
    }

    private List<LabUnit> createTestLabs() {
        List<LabUnit> labs = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            labs.add(LabUnit.builder()
                    .name("labName" + i)
                    .appName("appName" + i)
                    .build());
        }

        return labs;
    }
}
