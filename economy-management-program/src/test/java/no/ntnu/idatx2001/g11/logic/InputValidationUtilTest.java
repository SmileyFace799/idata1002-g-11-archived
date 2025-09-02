package no.ntnu.idatx2001.g11.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//TODO: Make this work
public class InputValidationUtilTest {

    private Label errorLabel = new Label();
    private String errorObject = "Testing function";

    @BeforeAll
    public static void startupPlatform() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    public void createNecessaryObjects() {
        errorLabel = new Label();
    }

    @Test
    public void testTestBlankInput() {
        assertFalse(InputValidationUtil.testBlankInput("", errorLabel, errorObject));
        assertFalse(InputValidationUtil.testBlankInput("  ", errorLabel, errorObject));
        assertTrue(InputValidationUtil.testBlankInput("Stuff!", errorLabel, errorObject));
    }

    @Test
    public void testTestNumericInput() {
        assertFalse(InputValidationUtil.testPriceInput("abc", errorLabel, errorObject));
        assertFalse(InputValidationUtil.testPriceInput("1.234", errorLabel, errorObject));
        assertFalse(InputValidationUtil.testPriceInput("1..2", errorLabel, errorObject));
        assertTrue(InputValidationUtil.testPriceInput("123", errorLabel, errorObject));
        assertTrue(InputValidationUtil.testPriceInput("1.23", errorLabel, errorObject));
    }

    @Test
    public void testTestPriceInput() {
        assertFalse(InputValidationUtil.testPriceInput("abc", errorLabel, errorObject));
        assertFalse(InputValidationUtil.testPriceInput("1.234", errorLabel, errorObject));
        assertFalse(InputValidationUtil.testPriceInput("1..2", errorLabel, errorObject));
        assertTrue(InputValidationUtil.testPriceInput("123", errorLabel, errorObject));
        assertTrue(InputValidationUtil.testPriceInput("1.23", errorLabel, errorObject));

    }

    @Test
    public void testTestTextCompliance() {
        assertFalse(InputValidationUtil.testTextCompliance("With@Symbols", errorLabel, errorObject));
        assertFalse(InputValidationUtil.testTextCompliance("", errorLabel, errorObject));
        assertTrue(InputValidationUtil.testTextCompliance("With Spaces", errorLabel, errorObject));
        assertTrue(InputValidationUtil.testTextCompliance("Alphanumeric123", errorLabel, errorObject));

    }

    @Test
    public void testTestUsernameCompliance() {
        assertFalse(InputValidationUtil.testUsernameCompliance("User@Name", errorLabel, errorObject));
        assertFalse(InputValidationUtil.testUsernameCompliance("", errorLabel, errorObject));
        assertTrue(InputValidationUtil.testUsernameCompliance("Username123", errorLabel, errorObject));
        assertTrue(InputValidationUtil.testUsernameCompliance("username", errorLabel, errorObject));

    }
}
