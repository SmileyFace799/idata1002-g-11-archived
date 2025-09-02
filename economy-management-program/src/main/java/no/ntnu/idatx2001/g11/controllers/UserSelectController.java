package no.ntnu.idatx2001.g11.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import no.ntnu.idatx2001.g11.App;
import no.ntnu.idatx2001.g11.DataManager;
import no.ntnu.idatx2001.g11.controllers.ui.ExceptionCommunicator;
import no.ntnu.idatx2001.g11.exceptions.NoUserException;
import no.ntnu.idatx2001.g11.logic.InputValidationUtil;

/**
 * The controller for selecting a user.
 */
public class UserSelectController implements Initializable {

    /**
     * Default string for user slots without a registered user.
     */
    public static final String NO_USER_STRING = "New user";

    /**
     * Describing string for the name entry field. Used for error handling.
     */ 
    public static final String NAME_FIELD_SHORTHAND = "Name field";

    /**
     * Describing string for the starting funds field. Used for error handling.
     */ 
    public static final String STARTING_FUNDS_SHORTHAND = "Starting funds";

    // Singletons
    private DataManager dataManager = DataManager.getInstance();
    private ExceptionCommunicator exceptionCommunicator = ExceptionCommunicator.getInstance();

    // Storage for whether a slot exists.
    private boolean[] slotExists;

    // The currently selected slot, used with user creation
    int selectedUserCreationSlot = -1;

    // User slot labels
    private VBox[] userSlot;
    @FXML VBox userPanel1;
    @FXML VBox userPanel2;
    @FXML VBox userPanel3;

    @FXML HBox userSelectScreen;
    @FXML VBox userCreateScreen;

    @FXML TextField userCreateNameEntry;
    @FXML TextField userCreateFundsEntry;
    @FXML Label userCreateErrorLabel;

    @FXML ImageView backButton; 

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        initialization();
    }

    /**
     * Handles initialization of the scene.
     */
    public void initialization() {
        userSlot = new VBox[] {
            userPanel1,
            userPanel2,
            userPanel3
        };

        slotExists = new boolean[3];

        String[] userNames = dataManager.fetchUserNames();
        
        
        for (int i = 0; i <= 2; i++) {
            boolean exists = !userNames[i].isEmpty();

            if (exists) {
                VBox slot = userSlot[i];
                Label slotLabel = getLabelFromUserSlot(slot);
                if (slotLabel != null) {
                    slotLabel.setText(userNames[i]);
                    setDisabledClassOfSlot(slot, false);
                } else {
                    slot.setDisable(true);
                }
            } else {
                VBox slot = userSlot[i];
                Label slotLabel = getLabelFromUserSlot(slot);
                if (slotLabel != null) {
                    slotLabel.setText(NO_USER_STRING);
                    setDisabledClassOfSlot(slot, true);
                } else {
                    slot.setDisable(true);
                }
            }

            slotExists[i] = exists;
        }
    }

    /**
     * Gets a label from a user slot UI element.
     *
     * @param inputVBox the user slot UI element
     * @return the user slot's label UI element
     */
    private Label getLabelFromUserSlot(VBox inputVBox) {
        Label foundLabel = null;

        Iterator<Node> childIterator = inputVBox.getChildren().iterator();
        AnchorPane spottedPane = null;

        while (childIterator.hasNext() && spottedPane == null) {
            Node current = childIterator.next();

            if (current instanceof AnchorPane) {
                spottedPane = (AnchorPane) current;
            }
        }

        if (spottedPane != null && spottedPane.getChildren().get(0) instanceof Label) {
            foundLabel = (Label) spottedPane.getChildren().get(0);
        }

        return foundLabel;
    }

    /**
     * Sets the disabled class of a slot, making it appear "empty" in the UI.
     *
     * @param slot slot to change
     * @param disabled state to set
     */
    private void setDisabledClassOfSlot(VBox slot, boolean disabled) {
        if (disabled) {
            slot.getStyleClass().add("disabled-container");
        } else {
            boolean hasDisabled = false;
            Iterator<String> iterableStyleClasses = slot.getStyleClass().iterator();

            while (iterableStyleClasses.hasNext() && !hasDisabled) {
                String current = iterableStyleClasses.next();

                if (current.equals("disabled-container")) {
                    hasDisabled = true;
                    slot.getStyleClass().remove(current);
                }
            }
        }
    }

    /**
     * Handles when the user clicks a user portrait.
     *
     * @param userSlot the clicked slot.
     */
    private void onUserSelect(int userSlot) {
        if (userSlot < 0 || userSlot > 2) {
            exceptionCommunicator.throwErrorDialogue(
                "Invalid selection", "Could not find the selected user slot");
            return;
        }

        boolean exists = slotExists[userSlot];

        if (exists) {
            try {
                dataManager.loadUserFromSlot(userSlot);
                App.setRoot("Main - Purchases Screen");
            } catch (NoUserException noUserException) {
                exceptionCommunicator.throwErrorDialogue(
                    "Error loading user",
                    "Could not load the user. Either it does not exist, or data is corrupted");
            } catch (IOException ioException) {
                exceptionCommunicator.throwErrorDialogue(
                    "Error navigating app",
                    "Failed to load the main application scene");
            } 
            
        } else {
            createNewUser(userSlot);
        }
    }

    /**
     * Event triggered when the user presses the first slot.
     *
     * @param e event parameter
     */
    @FXML
    public void userSelect1(MouseEvent e) {
        onUserSelect(0);
    }

    /**
     * Event triggered when the user presses the second slot.
     *
     * @param e event parameter
     */
    @FXML
    public void userSelect2(MouseEvent e) {
        onUserSelect(1);
    }

    /**
     * Event triggered when the user presses the third slot.
     *
     * @param e event parameter
     */
    @FXML
    public void userSelect3(MouseEvent e) {
        onUserSelect(2);
    }
    
    /**
     * Changes the screen to show user creation inputs.
     *
     * @param slot the slot in which the user clicks to trigger this
     */
    private void createNewUser(int slot) {
        if (slot < 0 || slot > 2) {
            return;
        }

        userSelectScreen.setVisible(false);
        userCreateScreen.setVisible(true);

        backButton.setVisible(true);

        userCreateNameEntry.requestFocus();

        selectedUserCreationSlot = slot;
    }

    /**
     * When the user clicks submit or presses enter when creating
     * a new user.
     *
     * @param e event
     */
    @FXML
    public void submitNewUser(Event e) {
        String inputName = userCreateNameEntry.getText();
        String inputFunds = userCreateFundsEntry.getText();

        // Name can only contain alphanumeric characters (A-Z, 0-9)
        if (
            InputValidationUtil.testUsernameCompliance(
                inputName, userCreateErrorLabel, NAME_FIELD_SHORTHAND
            )
            &&
            InputValidationUtil.testPriceInput(
                inputFunds, userCreateErrorLabel, STARTING_FUNDS_SHORTHAND
            ) 
            ) {
            setErrorLabel("");
        } else {
            return;
        }

        if (selectedUserCreationSlot != -1) {
            dataManager.createFromUsername(selectedUserCreationSlot, inputName, Double.parseDouble(inputFunds));
            slotExists[selectedUserCreationSlot] = true;

            try {
                dataManager.loadUserFromSlot(selectedUserCreationSlot);
                App.setRoot("Main - Purchases Screen");
            } catch (NoUserException exception) {
                exceptionCommunicator.throwErrorDialogue(
                    "Error loading user",
                    "Could not load the user. Either it does not exist, or data is corrupted");
            } catch (IOException exception) {
                exceptionCommunicator.throwErrorDialogue(
                    "Error navigating app",
                    "Failed to load the main application scene");
            } 
        }

        userCreateNameEntry.setText("");
        userCreateFundsEntry.setText("");

        userSelectScreen.setVisible(true);
        userCreateScreen.setVisible(false);

        backButton.setVisible(false);

        selectedUserCreationSlot = -1;
    }

    /**
     * Triggers when the user clicks the back button.
     *
     * @param e event
     */
    @FXML
    public void backButtonClicked(MouseEvent e) {
        userSelectScreen.setVisible(true);
        userCreateScreen.setVisible(false);

        backButton.setVisible(false);
    }

    /**
     * Sets the error label of this FXML document.
     *
     * @param errorString string to set on the error label.
     */
    private void setErrorLabel(String errorString) {
        this.userCreateErrorLabel.setText(errorString);
    }
}
