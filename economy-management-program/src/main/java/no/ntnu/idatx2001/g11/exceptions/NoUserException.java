package no.ntnu.idatx2001.g11.exceptions;

/**
 * An exception for the scenario in which the program tries
 * to access a user that does not exist.
 */
public class NoUserException extends IllegalStateException {
    /**
     * Constructor.
     */
    public NoUserException() {
        super("There is no current user set.");
    }
}
