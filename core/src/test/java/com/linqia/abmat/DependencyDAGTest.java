package com.linqia.abmat;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

/**
 * Setup a dependency graph for testing
 * 
 * <pre>
 * A
 *   /> C -> E
 * B      /
 *   \> D -> F
 *        \
 *         > G
 * </pre>
 */
public class DependencyDAGTest {

    private static final ProjectId A_ID = ProjectId.parse("com.linqia:A:1.0");
    private static final Project A_PROJECT = new ProjectStub(A_ID);

    private static final ProjectId B_ID = ProjectId.parse("com.linqia:B:1.0");
    private static final Project B_PROJECT = new ProjectStub(B_ID);

    private static final ProjectId C_ID = ProjectId.parse("com.linqia:C:1.0");
    private static final Project C_PROJECT = new ProjectStub(C_ID, B_ID);

    private static final ProjectId D_ID = ProjectId.parse("com.linqia:D:1.0");
    private static final Project D_PROJECT = new ProjectStub(D_ID, B_ID);

    private static final ProjectId E_ID = ProjectId.parse("com.linqia:E:1.0");
    private static final Project E_PROJECT = new ProjectStub(E_ID, C_ID, D_ID);

    private static final ProjectId F_ID = ProjectId.parse("com.linqia:F:1.0");
    private static final Project F_PROJECT = new ProjectStub(F_ID, D_ID);

    private static final ProjectId G_ID = ProjectId.parse("com.linqia:G:1.0");
    private static final Project G_PROJECT = new ProjectStub(G_ID, D_ID);

    private static final Collection<Project> allProjects() {
        return new ArrayList<>(Arrays.asList(A_PROJECT, B_PROJECT, C_PROJECT,
                D_PROJECT, E_PROJECT, F_PROJECT, G_PROJECT));
    }

    @Test
    public void noAnchor() {
        Collection<Project> required = DependencyDAG.requiredProjects(
                allProjects(), Collections.<Project> emptyList());
        assertThat(required).isEmpty();

        DependencyDAG dag = new DependencyDAG(allProjects());
        assertThat(dag.getRequiredProjects(Collections.<Project> emptyList()))
                .isEmpty();
    }

    @Test
    public void standAloneAnchor() {
        Collection<Project> required = DependencyDAG.requiredProjects(
                allProjects(), Arrays.<Project> asList(A_PROJECT));
        assertThat(required).containsOnly(A_PROJECT);

        DependencyDAG dag = new DependencyDAG(allProjects());
        assertThat(dag.getRequiredProjects(Arrays.<Project> asList(A_PROJECT)))
                .containsOnly(A_PROJECT);
    }

    @Test
    public void topLevelAnchor() {
        Collection<Project> required = DependencyDAG.requiredProjects(
                allProjects(), Arrays.<Project> asList(B_PROJECT));
        assertThat(required).containsOnly(B_PROJECT, C_PROJECT, D_PROJECT,
                E_PROJECT, F_PROJECT, G_PROJECT);

        DependencyDAG dag = new DependencyDAG(allProjects());
        assertThat(dag.getRequiredProjects(Arrays.<Project> asList(B_PROJECT)))
                .containsOnly(B_PROJECT, C_PROJECT, D_PROJECT, E_PROJECT,
                        F_PROJECT, G_PROJECT);
    }

    @Test
    public void midLevelAnchor() {
        Collection<Project> required = DependencyDAG.requiredProjects(
                allProjects(), Arrays.<Project> asList(D_PROJECT));
        assertThat(required).containsOnly(D_PROJECT, E_PROJECT, F_PROJECT,
                G_PROJECT);

        DependencyDAG dag = new DependencyDAG(allProjects());
        assertThat(dag.getRequiredProjects(Arrays.<Project> asList(D_PROJECT)))
                .containsOnly(D_PROJECT, E_PROJECT, F_PROJECT, G_PROJECT);
    }

    private static class ProjectStub extends Project {

        public ProjectStub(ProjectId id, ProjectId... deps) {
            super(null, id, Arrays.asList(deps));
        }

        @Override
        public void setDependency(ProjectId dep) throws IOException {
            throw new IllegalArgumentException();
        }

        @Override
        public void install() {
            throw new IllegalArgumentException();
        }

        @Override
        public String toString() {
            return "ProjectStub(" + getId() + ")";
        }
    }
}
