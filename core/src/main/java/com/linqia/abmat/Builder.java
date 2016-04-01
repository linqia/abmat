package com.linqia.abmat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Given a workspace and a whitelist of required projects, run a full build
 */
public class Builder {

    private final Workspace m_workspace;
    private final List<Project> m_requiredProjects;

    public Builder(Workspace workspace, Project... requiredProjects) {
        m_workspace = workspace;
        m_requiredProjects = Arrays.asList(requiredProjects);
    }

    public void run() throws IOException {
        List<Project> allProjects = new ArrayList<>();
        for (WorkingCopy wc : m_workspace.getWorkingCopies()) {
            allProjects.addAll(wc.getProjects());
        }
        Collection<Project> allAffectedProjects = DependencyDAG
                .requiredProjects(allProjects, m_requiredProjects);
    }
}
