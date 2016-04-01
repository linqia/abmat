package com.linqia.abmat;

import java.util.Collection;

/**
 * Represents an open pull request
 */
public interface PullRequest {

    /**
     * @return the branch pointed to by this pull request
     */
    Branch getBranch();

    /**
     * Get the upstream pull requests pointed to by the branch
     * 
     * @return a list of pull requests this pull request depends on, or an empty
     *         collection if none
     */
    Collection<PullRequest> getDependentPullRequests();

    /**
     * Check the pull request to see if a report has been posted, and if the
     * code has changed since.
     * 
     * @return true iff a report has been posted and no code changes have been
     *         posted to the pull request since the report
     */
    boolean hasUpToDateReport();

    /**
     * Post the given report back into the pull request
     * 
     * @param report
     *            the report to attach
     */
    void postReport(Report report);
}
