package com.linqia.abmat;

/**
 * Thrown when something is wrong with the AbMat config, or with the repos it
 * describes.
 */
public class AbMatException extends Exception {

    private static final long serialVersionUID = 1L;

    public AbMatException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbMatException(String message) {
        super(message);
    }

    public AbMatException(Throwable cause) {
        super(cause);
    }
}
