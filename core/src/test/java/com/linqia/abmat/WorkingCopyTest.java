package com.linqia.abmat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Test;

public class WorkingCopyTest {

    private static final File DIR = TestingUtils
            .prepareTestDir(WorkingCopyTest.class);
    private static final Branch TEST_BRANCH = new Branch("unit-test-example",
            "../../../../../.git");

    @Test
    public void createFreshWorkingCopy() throws IOException {
        File wcDir = new File(DIR, "createFreshWorkingCopy");
        WorkingCopy wc = new WorkingCopy(wcDir, TEST_BRANCH);
        wc.ensureCorrectSetup();
        assertThat(wcDir).isDirectory();
        assertThat(new File(wcDir, ".git")).isDirectory();

        Repository repo = new FileRepositoryBuilder().setWorkTree(wcDir)
                .build();
        assertThat(repo.getBranch()).isEqualTo("unit-test-example");
        repo.close();
    }

    @Test
    public void failOnCreateWrongBranch() {
        File wcDir = new File(DIR, "failOnCreateWrongBranch");
        Branch branch = new Branch("unit-test-missing-example",
                TEST_BRANCH.getRemote());
        WorkingCopy wc = new WorkingCopy(wcDir, branch);
        try {
            wc.ensureCorrectSetup();
            fail("Should have thrown exception");
        } catch (IOException e) {
            // Expected
        }
    }

    @Test
    public void correctLocalChanges() throws IOException {
        File wcDir = new File(DIR, "correctLocalChanges");
        WorkingCopy wc = new WorkingCopy(wcDir, TEST_BRANCH);
        wc.ensureCorrectSetup();
        assertThat(wc.isCorrectSetup()).isTrue();

        File missingFile = new File(wcDir, "core/pom.xml");
        assertThat(missingFile.delete()).isTrue();
        wc = new WorkingCopy(wcDir, TEST_BRANCH);
        assertThat(wc.isCorrectSetup()).isFalse();
        wc.ensureCorrectSetup();
        assertThat(missingFile.isFile()).isTrue();
        assertThat(wc.isCorrectSetup()).isTrue();
    }

    @Test
    public void isCorrectSetup() throws IOException {
        File wcDir = new File(DIR, "isCorrectSetup");
        WorkingCopy wc = new WorkingCopy(wcDir, TEST_BRANCH);
        wc.ensureCorrectSetup();
        assertThat(wc.isCorrectSetup()).isTrue();

        wc = new WorkingCopy(wcDir, TEST_BRANCH);
        assertThat(wc.isCorrectSetup()).isTrue();

        assertThat(new File(wcDir, "core/pom.xml").delete()).isTrue();
        assertThat(wc.isCorrectSetup()).isFalse();
    }

    @Test
    public void getProjects() throws IOException {
        WorkingCopy wc = new WorkingCopy(new File(DIR, "getProjects"),
                TEST_BRANCH);
        wc.ensureCorrectSetup();
        Collection<Project> projects = wc.getProjects();
        assertThat(projects).hasSize(1);
        Project project = projects.iterator().next();
        assertThat(project.getId().getVersionlessString()).isEqualTo(
                "com.linqia.abmat:abmat-core");
    }
}
