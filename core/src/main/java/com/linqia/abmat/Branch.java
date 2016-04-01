package com.linqia.abmat;

import org.assertj.core.util.Strings;

/**
 * Represents a branch on a remote git repository, which has a baseline branch
 * to compare to.
 */
public final class Branch {

    private final String m_branch;
    private final String m_remote;
    private final String m_mainline;

    public Branch(String branch, String remote, String mainline) {
        if (Strings.isNullOrEmpty(remote) || Strings.isNullOrEmpty(mainline)
                || Strings.isNullOrEmpty(branch)) {
            throw new NullPointerException();
        }
        m_remote = remote;
        m_mainline = mainline;
        m_branch = branch;
    }

    public Branch(String branch, String remote) {
        this(branch, remote, "master");
    }

    public String getBranch() {
        return m_branch;
    }

    public String getRemote() {
        return m_remote;
    }

    public String getMainline() {
        return m_mainline;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + m_branch.hashCode();
        result = prime * result + m_remote.hashCode();
        result = prime * result + m_mainline.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Branch other = (Branch) obj;
        return m_branch.equals(other.m_branch)
                && m_remote.equals(other.m_remote)
                && m_mainline.equals(other.m_mainline);
    }

    @Override
    public String toString() {
        return m_branch + "(" + m_remote + ":" + m_mainline + ")";
    }
}
