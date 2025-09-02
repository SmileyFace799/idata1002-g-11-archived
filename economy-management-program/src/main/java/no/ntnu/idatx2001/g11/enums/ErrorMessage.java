package no.ntnu.idatx2001.g11.enums;

/**
 * Enum containing common front-end error messages.
 */
public enum ErrorMessage {
    /** Error message for when a field is empty. */
    ERROR_EMPTY_FIELD("%s can not be empty"),
    /** Error message for when a field is not a valid username. */
    ERROR_INVALID_USERNAME("%s can only contain alphanumerical characters (A-Z, 0-9, no spaces)"),
    /** Error message for when a field is not valid text. */
    ERROR_INVALID_TEXT("%s can only contain alphanumerical characters and spaces (A-Z, 0-9)"),
    /** Error message for when a field is not a valid number. */
    ERROR_INVALID_NUMBER("%s must be a number (59.98)"),
    /** Error message for when a field is a number with two more than two decimal places. */
    ERROR_OVERFLOW_DECIMAL("%s may only have up to two decimal places"),
    /** Error message for when a field is a number with two more than one decimal point. */
    ERROR_MULTIPLE_DOTS("%s may only contain one decimal point (\".\")");
    
    private final String message;

    /**
     * Constructor.
     *
     * @param message the error message associated with the enum
     */
    private ErrorMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the message associated with the enum.
     *
     * @param variable the variable this message applies to
     *     (for example "Text field" results in "Text field is empty").
     * @return the formatted error message
     */
    public String getMessage(String variable) {
        return String.format(this.message, variable);
    }
}