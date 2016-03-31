package com.linqia.abmat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import com.google.common.io.Files;

public class ProjectTest {

    private static final File DIR = TestingUtils
            .prepareTestDir(ProjectTest.class);

    @Test
    public void readPom() throws IOException {
        Project p = new Project(new File(
                "src/test/resources/poms/abmat.core.xml"));
        assertThat(p.getId()).isEqualTo(
                ProjectId.parse("com.linqia.abmat:abmat-core:0.0.1-SNAPSHOT"));
        Collection<ProjectId> deps = p.getDependencies();
        assertThat(deps).hasSize(17);
        assertThat(deps).contains(
                ProjectId.parse("org.apache.maven:maven-embedder:3.1.1"));
    }

    @Test
    public void setDependency() throws IOException {
        File pom = new File(DIR, "setDependency.xml");
        Files.copy(new File("src/test/resources/poms/abmat.core.xml"), pom);
        Project p = new Project(pom);
        ProjectId oldDep = ProjectId
                .parse("org.apache.maven:maven-embedder:3.1.1");
        ProjectId newDep = ProjectId
                .parse("org.apache.maven:maven-embedder:3.1.5");

        assertThat(p.getDependencies()).contains(oldDep);
        assertThat(p.getDependencies()).doesNotContain(newDep);
        p.setDependency(newDep);
        assertThat(p.getDependencies()).doesNotContain(oldDep);
        assertThat(p.getDependencies()).contains(newDep);

        p = new Project(pom);
        assertThat(p.getDependencies()).doesNotContain(oldDep);
        assertThat(p.getDependencies()).contains(newDep);
    }

    @Test
    public void setNonDependency() throws IOException {
        File pom = new File(DIR, "setNonDependency.xml");
        Files.copy(new File("src/test/resources/poms/abmat.core.xml"), pom);
        Project p = new Project(pom);

        try {
            p.setDependency(ProjectId
                    .parse("org.apache.maven:other-artifact:1.0"));
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }
}
