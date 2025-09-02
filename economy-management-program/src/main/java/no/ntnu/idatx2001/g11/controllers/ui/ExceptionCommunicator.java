package no.ntnu.idatx2001.g11.controllers.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Class responsible for communicating errors to the user.
 * Handles opening error dialogues.
 */
public class ExceptionCommunicator {
    private static ExceptionCommunicator instance = null;

    private ExceptionCommunicator() {}

    /**
     * Singleton getter.
     *
     * @return this singleton's instance
     */
    public static ExceptionCommunicator getInstance() {
        if (instance == null) {
            instance = new ExceptionCommunicator();
        }

        return instance;
    }

    /**
     * Creates an error dialogue.
     *
     * @param title the title of the dialogue
     * @param content the content of the dialogue
     */
    public void throwErrorDialogue(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);

        alert.setTitle("An error occurred");
        alert.setHeaderText(title);
        alert.setContentText(content);

        alert.showAndWait();
    } 
}
