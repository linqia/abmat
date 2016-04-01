package com.linqia.abmat;

/**
 * Contains an area on local disk with a bunch of {@link WorkingCopy} instances.
 * Also references a maven repo which may or may not be a private temporary one.
 */
public interface Workspace {

    /**
     * @return all the WorkingCopy instances contained within
     */
    WorkingCopy[] getWorkingCopies();
}
