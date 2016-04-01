package com.linqia.abmat;

/**
 * Given an existing directory with existing {@link WorkingCopy} folders, fix it
 * up to be used.
 */
class ReusableGitWorkspace implements Workspace {

    @Override
    public WorkingCopy[] getWorkingCopies() {
        throw new UnsupportedOperationException();
    }
}
