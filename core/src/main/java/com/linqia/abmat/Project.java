package com.linqia.abmat;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.maven.cli.MavenCli;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.google.common.base.Throwables;

/**
 * This represents a maven project existing in a local {@link WorkingCopy}.
 */
public class Project {

    private static Model parse(File pom) throws IOException {
        try (FileReader fileReader = new FileReader(pom);) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            return reader.read(fileReader);
        } catch (XmlPullParserException e) {
            Throwables.propagateIfPossible(e, IOException.class);
            throw new IOException(e);
        }
    }

    private static void extractDependencies(Model model,
            Collection<ProjectId> deps) {
        deps.clear();
        for (Dependency dep : model.getDependencies()) {
            deps.add(new ProjectId(dep.getGroupId(), dep.getArtifactId(), dep
                    .getVersion()));
        }
    }

    private final File m_pomFile;
    private final ProjectId m_id;
    private final Collection<ProjectId> m_deps;

    Project(File pomFile, ProjectId id, Collection<ProjectId> deps) {
        m_pomFile = pomFile;
        m_id = id;
        m_deps = deps;
    }

    public Project(File pomFile) throws IOException {
        m_pomFile = pomFile;

        Model model = parse(m_pomFile);
        m_id = new ProjectId(model.getGroupId(), model.getArtifactId(),
                model.getVersion());

        m_deps = new ArrayList<>();
        extractDependencies(model, m_deps);
    }

    /**
     * @return the id of this project
     */
    public ProjectId getId() {
        return m_id;
    }

    /**
     * @return all the dependencies that this project has
     */
    public Collection<ProjectId> getDependencies() {
        return new ArrayList<>(m_deps);
    }

    /**
     * Edit the pom for this project, changing the version to depend on this
     * artifact
     * 
     * @param dep
     *            the groupId and artifactId to match with, and the version to
     *            set to
     * @throws IOException
     */
    public void setDependency(ProjectId dep) throws IOException {
        Model model = parse(m_pomFile);
        for (Dependency realDep : model.getDependencies()) {
            if (dep.getArtifactId().equals(realDep.getArtifactId())
                    && dep.getGroupId().equals(realDep.getGroupId())) {
                realDep.setVersion(dep.getVersion());

                try (FileWriter fileWriter = new FileWriter(m_pomFile);) {
                    MavenXpp3Writer writer = new MavenXpp3Writer();
                    writer.write(fileWriter, model);
                }

                model = parse(m_pomFile);
                extractDependencies(model, m_deps);
                return;
            }
        }
        throw new IllegalArgumentException(
                "Could not find an existing dependency for " + dep);
    }

    /**
     * Install this maven project
     */
    public void install() {
        String[] args = new String[] { "test" };
        String wd = ".";
        PrintStream stdout = System.out;
        PrintStream stderr = System.err;
        new MavenCli().doMain(args, wd, stdout, stderr);
    }

    @Override
    public String toString() {
        return m_id + "(" + m_pomFile + ")";
    }
}
