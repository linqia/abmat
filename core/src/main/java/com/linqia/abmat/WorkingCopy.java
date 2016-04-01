package com.linqia.abmat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.assertj.core.util.Strings;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

/**
 * A {@link Branch} checked out into a local folder
 */
public class WorkingCopy {

    private static final Logger LOG = LoggerFactory
            .getLogger(WorkingCopy.class);

    private final File m_dir;
    private final Branch m_branch;
    private List<Project> m_projects;

    public WorkingCopy(File dir, Branch branch) {
        m_dir = dir;
        m_branch = branch;
    }

    /**
     * Ensure the directory is setup correctly (a clone of the right repo,
     * checked out to the right branch, with no local changes, and up-to-date
     * with the remote). This will discard data from the folder if needed.
     * 
     * @throws IOException
     */
    public void ensureCorrectSetup() throws IOException {
        if (!m_dir.isDirectory()) {
            if (!m_dir.mkdirs()) {
                throw new IOException("Unable to create missing directory: "
                        + m_dir);
            }
        }

        Repository repo = null;
        try {
            repo = new FileRepositoryBuilder().setWorkTree(m_dir).build();
            if (Strings.isNullOrEmpty(repo.getBranch())
                    || repo.getRemoteNames().isEmpty()) {
                repo.close();
                repo = null;
            }
        } catch (IOException e) {
            LOG.debug("Error trying to read existing repo", e);
        }

        if (repo == null) {
            LOG.debug("Re-cloning repo {} to {}", m_branch.getRemote(), m_dir);
            try {
                Git git = Git.cloneRepository().setURI(m_branch.getRemote())
                        .setDirectory(m_dir).call();
                repo = git.getRepository();
            } catch (InvalidRemoteException e) {
                throw new IllegalArgumentException("Illegal remote: "
                        + m_branch.getRemote(), e);
            } catch (GitAPIException e) {
                throw new IOException("Failure cloning", e);
            }
        }
        if (!m_branch.getBranch().equals(repo.getBranch())) {
            LOG.debug("Wrong branch {} so switching to {}", repo.getBranch(),
                    m_branch.getBranch());
            try {
                // First checkout the origin branch to be sure it exists. This
                // fails fast if our desired branch doesn't exist, since we have
                // setCreateBranch set to false
                Git.wrap(repo).checkout()
                        .setName("origin/" + m_branch.getBranch()).call();

                // If we know the origin branch exists, checkout our local
                // branch (creating if needed)
                Git.wrap(repo).checkout().setName(m_branch.getBranch())
                        .setCreateBranch(true).call();
            } catch (GitAPIException e) {
                throw new IOException("Failure switching branches", e);
            }
            if (!m_branch.getBranch().equals(repo.getBranch())) {
                throw new IOException("Somehow the branch didn't switch");
            }
        }

        try {
            Git git = Git.wrap(repo);
            if (hasLocalChanges(git)) {
                LOG.debug("Resetting the working copy to HEAD");
                git.reset().setMode(ResetType.HARD).setRef("HEAD").call();
            }
        } catch (GitAPIException e) {
            throw new IOException("Failure reading/discarding local changes", e);
        }
        repo.close();
    }

    public boolean isCorrectSetup() throws IOException {
        if (!m_dir.isDirectory()) {
            LOG.debug("Directory missing: {}", m_dir);
            return false;
        }
        try {
            Repository repo = new FileRepositoryBuilder().setWorkTree(m_dir)
                    .build();
            Git git = Git.wrap(repo);
            boolean correctBranch = m_branch.getBranch().equals(
                    repo.getBranch());
            boolean localChanges = hasLocalChanges(git);

            LOG.debug("correctBranch={} localChanges={}", correctBranch,
                    localChanges);
            repo.close();
            return correctBranch && !localChanges;
        } catch (GitAPIException e) {
            throw new IOException("Failure working with local copy", e);
        }
    }

    private static boolean hasLocalChanges(Git git) throws GitAPIException {
        Status status = git.status().call();
        if (!status.getConflicting().isEmpty()) {
            LOG.debug("Has conflicting files");
            return true;
        }
        if (!status.getAdded().isEmpty()) {
            LOG.debug("Has added files");
            return true;
        }
        if (!status.getChanged().isEmpty()) {
            LOG.debug("Has changed files");
            return true;
        }
        if (!status.getMissing().isEmpty()) {
            LOG.debug("Has missing files");
            return true;
        }
        if (!status.getModified().isEmpty()) {
            LOG.debug("Has modified files");
            return true;
        }
        if (!status.getRemoved().isEmpty()) {
            LOG.debug("Has removed files");
            return true;
        }
        if (!status.getUncommittedChanges().isEmpty()) {
            LOG.debug("Has uncommitted changes");
            return true;
        }
        if (!status.getUntracked().isEmpty()) {
            LOG.debug("Has untracked files");
            return true;
        }
        if (!status.getUntrackedFolders().isEmpty()) {
            LOG.debug("Has untracked folders");
            return true;
        }
        if (!status.getConflictingStageState().isEmpty()) {
            LOG.debug("Has conflicting states");
            return true;
        }
        return false;
    }

    private static List<Project> findProjects(File dir) throws IOException {
        List<Project> projects = new ArrayList<>();
        for (File file : Files.fileTreeTraverser().preOrderTraversal(dir)) {
            if ("pom.xml".equals(file.getName())) {
                projects.add(new Project(file));
            }
        }
        return projects;
    }

    /**
     * Returns all the {@link Project} instances (as determined by pom.xml files
     * found) contained within
     * 
     * @throws IOException
     */
    public Collection<Project> getProjects() throws IOException {
        if (m_projects == null) {
            m_projects = Collections.unmodifiableList(findProjects(m_dir));
        }
        return m_projects;
    }

    /**
     * Get all the paths changed on this branch since it forked from the repo's
     * mainline. Changes to mainline since that fork should not be represented
     * here.
     * 
     * @return a list of paths relative to the git repo root
     */
    public Set<String> changedPaths() {
        throw new UnsupportedOperationException();
    }

    /**
     * Compare the current branch of the repo against its mainline. Find all
     * changed files, walking upwards to find a pom.xml. Return the Project
     * entries for each unique pom you find this way. If the workspace is
     * already on its mainline, this returns an empty list
     */
    public Collection<Project> getChangedProjects() {
        throw new UnsupportedOperationException();
    }

}
