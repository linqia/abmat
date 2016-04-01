package com.linqia.abmat;

import java.util.Collection;

/**
 * Use this to get started with everything. Contains all preferences.
 */
public interface AbMat {

    /**
     * @return all open pull requests on the monitored repos mentined in the
     *         config, or empty collection if none
     */
    Collection<PullRequest> getPullRequests();

    /**
     * Return a workspace with the given branches checked out. Any repo
     * mentioned in the config that does not have a branch passed in will be
     * checked out on the mainline branch.
     * 
     * @param branches
     *            the branches to check out
     * @return a workspace pointed to existing local checked out repos
     */
    Workspace setupWorkspace(Branch... branches);
}
