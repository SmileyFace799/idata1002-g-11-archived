package no.ntnu.idatx2001.g11.logic;

import javafx.scene.control.Label;
import no.ntnu.idatx2001.g11.enums.ErrorMessage;

/**
 * Utility class for testing validity of input.
 */
public class InputValidationUtil {
    // Text test formats
    /**
     * Tests if the input matches the username requirements. 
     */
    public static final String USERNAME_FORMAT = "([\\dA-Za-z])*";

    /**
     * Tests if the input matches the regular text requirements. 
     */
    public static final String TEXT_FORMAT = "([\\dA-Za-z ])*";

    // Number test formats
    /**
     * Test the input to see if it matches the full number format at all. 
     */
    public static final String COMPLETE_NUMBER_FORMAT = "(\\d*.\\d{1,2})|(\\d*)";

    /**
     * Tests if the input is a number at all. 
     */
    public static final String NUMBER_FORMAT = "[\\d\\.]*";

    /**
     * Tests if the input has more than two decimal places.
     */
    public static final String OVERFLOW_DECIMAL_FORMAT = "\\d*.\\d{3,}";

    /**
     * Tests if the input has more than one decimal point.
     */
    public static final String OVERFLOW_PERIOD_FORMAT = "(\\d*\\.){2,}+.*+";

    private InputValidationUtil() {
        // private construction prevents creating an object of this utility
    }

    /**
     * Tests whether an input is compliant with being a price-tag.
     *
     * @param input input string to test
     * @param errorLabel error label to output errors to
     * @param errorObject error object to be used in error message (e.g. "product price")
     * @return whether the test passed without errors
     */
    public static final boolean testPriceInput(String input, Label errorLabel, String errorObject) {
        boolean success = true;
        
        if (input == null || errorLabel == null || errorObject == null) {
            return false;
        }

        if (!testBlankInput(input, errorLabel, errorObject)) {
            success = false;
        } else if (!testNumericInput(input, errorLabel, errorObject)) {
            success = false;
        }

        return success;
    }

    /**
     * Tests whether an input is blank/empty.
     *
     * @param input input string to test
     * @param errorLabel error label to output errors to
     * @param errorObject error object to be used in error message (e.g. "product price")
     * @return whether the test passed without errors
     */
    public static final boolean testBlankInput(String input, Label errorLabel, String errorObject) {
        boolean success = true;
        if (input == null || errorLabel == null || errorObject == null) {
            return false;
        }
        
        if (input.isBlank()) {
            errorLabel.setText(
                ErrorMessage.ERROR_EMPTY_FIELD.getMessage(errorObject)
            );
            success = false;
        }
        
        return success;
    }

    /**
     * Tests whether the input complies with the username format (alphanumeric).
     *
     * @param input input string to test
     * @param errorLabel error label to output errors to
     * @param errorObject error object to be used in error message (e.g. "product price")
     * @return whether the test passed without errors
     */
    public static final boolean testUsernameCompliance(
        String input, Label errorLabel, String errorObject) {
        boolean success = true;
        if (input == null || errorLabel == null || errorObject == null) {
            return false;
        }

        if (!testBlankInput(input, errorLabel, errorObject)) {
            success = false;
        } else if (!input.matches(USERNAME_FORMAT)) {
            errorLabel.setText(
                ErrorMessage.ERROR_INVALID_USERNAME.getMessage(errorObject)
            );
            success = false;
        }

        return success;
    }

    /**
     * Tests whether the input complies with the text format (alphanumeric and space).
     *
     * @param input input string to test
     * @param errorLabel error label to output errors to
     * @param errorObject error object to be used in error message (e.g. "product price")
     * @return whether the test passed without errors
     */
    public static final boolean testTextCompliance(
        String input, Label errorLabel, String errorObject) {
        boolean success = true;

        if (input == null || errorLabel == null || errorObject == null) {
            return false;
        }
        
        if (!testBlankInput(input, errorLabel, errorObject)) {
            success = false;
        } else if (!input.matches(TEXT_FORMAT)) {
            errorLabel.setText(
                ErrorMessage.ERROR_INVALID_TEXT.getMessage(errorObject)
            );
            success = false;
        }

        return success;
    }

    /**
     * Tests whether an input is compliant with numeric standards.
     * <ol>
     * <li>Tests if it meets the correct format (readable as float)</li>
     * <li>Tests if it meets the maximum decimal symbols (max 2 decimals)</li>
     * <li>Tests if it meets the maximum decimal points (no more than one full stop)</li>
     * </ol>
     *
     * @param input input string to test
     * @param errorLabel error label to output errors to
     * @param errorObject error object to be used in error message (e.g. "product price")
     * @return whether the test passed without errors
     */
    public static final boolean testNumericInput(
        String input, Label errorLabel, String errorObject) {
        boolean success = true;
        
        if (input == null || errorLabel == null || errorObject == null) {
            return false;
        }

        if (!input.matches(COMPLETE_NUMBER_FORMAT)) {
            if (!input.matches(NUMBER_FORMAT)) {
                errorLabel.setText(
                    ErrorMessage.ERROR_INVALID_NUMBER.getMessage(errorObject)
                );
            } else if (input.matches(OVERFLOW_DECIMAL_FORMAT)) {
                errorLabel.setText(
                    ErrorMessage.ERROR_OVERFLOW_DECIMAL.getMessage(errorObject)
                );
            } else if (input.matches(OVERFLOW_PERIOD_FORMAT)) {
                errorLabel.setText(
                    ErrorMessage.ERROR_MULTIPLE_DOTS.getMessage(errorObject)
                );
            }
            success = false;
        }

        return success;
    }
}
