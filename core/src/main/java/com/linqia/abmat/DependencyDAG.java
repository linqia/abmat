package com.linqia.abmat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DependencyDAG {

    private static final Logger LOG = LoggerFactory
            .getLogger(DependencyDAG.class);

    static Collection<Project> requiredProjects(
            Collection<Project> allProjects, Collection<Project> anchorProjects) {
        Map<ProjectId, Project> requiredProjects = new HashMap<>();
        for (Project project : anchorProjects) {
            requiredProjects.put(project.getId(), project);
        }
        boolean more = true;
        while (more) {
            more = false;
            for (Iterator<Project> it = allProjects.iterator(); it.hasNext();) {
                Project project = it.next();
                ProjectId id = project.getId();
                if (requiredProjects.containsKey(id)) {
                    it.remove();
                    continue;
                }
                for (ProjectId dep : project.getDependencies()) {
                    if (requiredProjects.containsKey(dep)) {
                        it.remove();
                        requiredProjects.put(id, project);
                        more = true;
                        break;
                    }
                }
            }
        }
        return requiredProjects.values();
    }

    private final Map<String, Project> m_projects;
    private final Map<String, Set<String>> m_dependants;

    public DependencyDAG(Collection<Project> projects) {
        m_projects = new HashMap<>();
        m_dependants = new HashMap<>();
        for (Project project : projects) {
            ProjectId id = project.getId();
            String versionless = id.getVersionlessString();
            if (m_projects.containsKey(versionless)) {
                throw new IllegalArgumentException(
                        "Given more than one version for " + versionless);
            }
            m_projects.put(versionless, project);
            m_dependants.put(versionless, new HashSet<String>());
        }

        for (Project project : projects) {
            ProjectId id = project.getId();
            String versionlessId = id.getVersionlessString();
            for (ProjectId depId : project.getDependencies()) {
                String versionlessDepId = depId.getVersionlessString();
                if (m_dependants.containsKey(versionlessDepId)) {
                    m_dependants.get(versionlessDepId).add(versionlessId);
                }
            }
        }
        LOG.debug("Built dependency graph: {}", m_dependants);
    }

    public Collection<Project> getRequiredProjects(
            Collection<Project> anchorProjects) {
        Set<String> versionlessRequired = new HashSet<>();
        for (Project project : anchorProjects) {
            String versionless = project.getId().getVersionlessString();
            if (!m_projects.containsKey(versionless)) {
                throw new IllegalArgumentException("Project not part of DAG: "
                        + project.getId());
            }
            addRequiredProjects(versionlessRequired, versionless);
        }
        Collection<Project> requiredProjects = new ArrayList<>();
        for (String versionlessId : versionlessRequired) {
            requiredProjects.add(m_projects.get(versionlessId));
        }
        return requiredProjects;
    }

    private void addRequiredProjects(Set<String> required, String versionlessId) {
        Set<String> deps = m_dependants.get(versionlessId);
        if (deps != null) {
            required.add(versionlessId);
            for (String versionlessDepId : deps) {
                addRequiredProjects(required, versionlessDepId);
            }
        }
    }
}
