package no.ntnu.idatx2001.g11.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import no.ntnu.idatx2001.g11.App;
import no.ntnu.idatx2001.g11.DataManager;
import no.ntnu.idatx2001.g11.controllers.ui.ExceptionCommunicator;
import no.ntnu.idatx2001.g11.controllers.ui.PurchaseListing;
import no.ntnu.idatx2001.g11.enums.TimeType;
import no.ntnu.idatx2001.g11.exceptions.NoUserException;
import no.ntnu.idatx2001.g11.generics.Transaction;
import no.ntnu.idatx2001.g11.logic.InputValidationUtil;

/**
 * Controller for the main menu screen.
 * Handles all features related to the "Main - Purchases Screen" fxml file.
 */
public class MainMenuController implements Initializable {
    /**
     * Describing string for the product name entry field. Used for error handling.
     */ 
    public static final String PRODUCT_NAME_SHORTHAND = "Product name";
    
    /**
     * Describing string for the product price entry field. Used for error handling.
     */ 
    public static final String PRODUCT_PRICE_SHORTHAND = "Product price";

    /**
     * Array of the quick-select categories.
     */ 
    public static final String[] QUICK_CATEGORY_TYPES = new String[]{
        "Food", "Fuel", "Utility"
    };

    /**
     * Array of additional categories to be used in the "Other" dropdown.
     */ 
    public static final String[] ADDITIONAL_CATEGORY_TYPES = new String[]{
        "Rent", "Electricity", "Water",
        "Subscription", "Insurance",
        "Clothes", "Indulgement"
    };

    // Non-fxml images
    private Image plusIconImage;
    private Image backIconImage;

    // Singletons
    private DataManager dataManager = DataManager.getInstance();
    private ExceptionCommunicator exceptionCommunicator = ExceptionCommunicator.getInstance();

    // State variables
    private boolean createPurchaseWindowOpen = false;
    private int currentSelection = -1;

    // FXML definitions
    @FXML AnchorPane purchaseCreateScreen;
    @FXML ScrollPane purchaseListScreen;
    @FXML ImageView createPurchaseButtonImage;

    @FXML TextField createTransactionProductName;
    @FXML TextField createTransactionProductPrice;
    @FXML CheckBox createTransactionIncomeCheckbox;
    @FXML DatePicker createTransactionProductTime;

    @FXML CheckBox createTransactionIsRecurring;
    @FXML Spinner<Integer> createTransactionRecurringInterval;
    @FXML ComboBox<TimeType> createTransactionRecurringType;

    @FXML ComboBox<String> additionalCategorySelection;

    @FXML Label productRegisterErrorLabel;

    private Button[] quickSelectionButtons;

    @FXML Button quickSelect1;
    @FXML Button quickSelect2;
    @FXML Button quickSelect3;
    @FXML Button quickSelect4;

    @FXML VBox transactionList;

    @FXML LineChart<String, Float> graphItem;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Initialize variables
        plusIconImage = new Image(App.class
            .getResource("Images/iconAdd.png").toExternalForm());

        backIconImage = new Image(App.class
            .getResource("Images/iconBack.png").toExternalForm());

        initializeTransactionList();

        createTransactionRecurringType.getItems().setAll(
            FXCollections.observableArrayList(TimeType.values()));

        createTransactionRecurringType.setValue(TimeType.DAYS);

        quickSelectionButtons = new Button[]
            {quickSelect1, quickSelect2, quickSelect3, quickSelect4};

        additionalCategorySelection.getItems().setAll(
            FXCollections.observableArrayList(ADDITIONAL_CATEGORY_TYPES));
    }

    /**
     * Appends a transaction to the interface list.
     * <b>Does NOT register a transaction to the user.</b>
     *
     * @param transaction transaction to be appended.
     */
    private void appendTransactionList(Transaction transaction) {
        PurchaseListing listItem = new PurchaseListing(transaction);
        ImageView trashButton = listItem.getTrashButton();
        
        transactionList.getChildren().add(listItem);
        
        trashButton.setOnMouseClicked(e -> {
            List<Transaction> transactions = dataManager.getTransactionList();
            transactions.remove(transaction);
            transactionList.getChildren().remove(listItem);
        });
    }

    /**
     * Initializes the transaction list when first loading the FXML file.
     */
    private void initializeTransactionList() {
        List<Transaction> transactions = dataManager.getTransactionList();

        transactionList.getChildren().removeIf(t -> t.getId() == null);
    
        for (Transaction transaction : transactions) {
            appendTransactionList(transaction);
        }
    }
    
    /**
     * Event triggered when clicking the bottom right floating action button (FAB).
     * Should handle showing the user the transaction creation screen and
     * changing the button graphics to reflect their position in the program.
     *
     * @param e event parameter
     */
    @FXML
    public void onCreatePurchaseButton(Event e) {
        toggleCreateScreen();
    }

    /**
     * Event triggered when the user presses the top-bar back button.
     *
     * @param e event parameter
     */
    @FXML
    public void backButtonClicked(Event e) {
        if (createPurchaseWindowOpen) {
            toggleCreateScreen();
        } else {
            try {
                dataManager.forceSave();
                App.setRoot("User Select Screen");
            } catch (IOException e1) {
                exceptionCommunicator.throwErrorDialogue(
                    "Error navigating app",
                    "Failed to load the main application scene");
            } catch (NoUserException exception) {
                exceptionCommunicator.throwErrorDialogue("Could not save data",
                    "Could not save data as the associated user was not found.");
            }
        }
    }

    /**
     * Toggles the transaction create screen on or off.
     */
    private void toggleCreateScreen() {
        createPurchaseWindowOpen = !createPurchaseWindowOpen;

        purchaseCreateScreen.setVisible(createPurchaseWindowOpen);
        purchaseListScreen.setVisible(!createPurchaseWindowOpen);

        createPurchaseButtonImage.setImage(
            createPurchaseWindowOpen ? backIconImage : plusIconImage);

        if (createTransactionProductTime.getValue() == null) {
            createTransactionProductTime.setValue(LocalDate.now());
        }
    }

    /**
     * Clears all input in the transaction creation screen. To be used when the user
     * finalizes the creation of a transaction and it is added properly.
     */
    private void clearInput() {
        createTransactionProductName.clear();
        createTransactionProductPrice.clear();
        handleRecurringToggle(false);
        handleQuickSelection(-1);
    }

    /**
     * Validates fields in the submit screen and provides an error message to the user when
     * the fields are invalid.
     *
     * @return whether input passed validation
     */
    private boolean validateInput() {
        boolean passed = true;

        String inputName = createTransactionProductName.getText();
        String inputPrice = createTransactionProductPrice.getText();

        if (!InputValidationUtil.testTextCompliance(
                inputName, productRegisterErrorLabel, PRODUCT_NAME_SHORTHAND
            )
            ||
            !InputValidationUtil.testPriceInput(
                inputPrice, productRegisterErrorLabel, PRODUCT_PRICE_SHORTHAND
            )) {
            passed = false;
        } else if (createTransactionIsRecurring.isSelected()) {
            if (createTransactionRecurringInterval.getValue() < 1) {
                passed = false;
                setErrorLabel("Recurring frequency must be above 0");
            } else if (createTransactionRecurringType.getValue() == null) {
                passed = false;
                setErrorLabel("Please select a valid frequency for recurring purchases");
            }
        } else if (currentSelection == -1) {
            passed = false;
            setErrorLabel("Please select a category for your purchase");
        } else if (currentSelection == 3 && additionalCategorySelection.getValue() == null) {
            passed = false;
            setErrorLabel("Please select a category for your purchase in the drop-down");
        }
        
        return passed;
    }

    /**
     * Event triggered when clicking "Submit" in the transaction creation screen.
     * Communicates with the data manager to append the new
     * transaction to the user save if it passes validation.
     *
     * @param e event parameter
     */
    @FXML
    public void submitNewTransaction(Event e) {
        boolean passed = validateInput();
        String inputName = createTransactionProductName.getText();
        String inputPrice = createTransactionProductPrice.getText();

        if (passed) {
            String category = "";
            if (currentSelection >= 0 && currentSelection < 3) {
                category = QUICK_CATEGORY_TYPES[currentSelection];
            } else if (currentSelection == 3) {
                category = additionalCategorySelection.getValue();
            }
            try {
                int incomeMultiplier = 
                    createTransactionIncomeCheckbox.isSelected() ? 1 : -1;
                float price = Float.valueOf(inputPrice) * incomeMultiplier;
                Transaction transaction = new Transaction(
                    inputName, 
                    category, 
                    price, 
                    createTransactionProductTime.getValue());
                    
                try {
                    dataManager.submitNewTransaction(transaction);
                } catch (NoUserException exception) {
                    exceptionCommunicator.throwErrorDialogue("Could not save data", 
                        "Could not save the transaction as the associated user was not found");
                }

                appendTransactionList(transaction);

                setErrorLabel("");
                clearInput();
            } catch (Exception exception) {
                setErrorLabel("Could not interpret the input price");
            }
        }
    }

    /**
     * Sets the error label of the program.
     *
     * @param errorString string to set the error label to.
     */
    private void setErrorLabel(String errorString) {
        this.productRegisterErrorLabel.setText(errorString);
    }

    /**
     * Handles when the user clicks on a category selection. Primarily for
     * ensuring user feedback for selecting category.
     *
     * @param selection the category option that was selected
     */
    private void handleQuickSelection(int selection) {
        if (selection == currentSelection) {
            return;
        }

        for (int i = 0; i < quickSelectionButtons.length; i++) {
            if (i == selection) {
                currentSelection = selection;
                quickSelectionButtons[i].getStyleClass().add("selected");
            } else {
                quickSelectionButtons[i].getStyleClass().remove("selected");
            }
        }

        additionalCategorySelection.setDisable(3 != currentSelection);
    }

    /**
     * Event trigger for when quick select option 1 is clicked in the interface.
     *
     * @param e event parameter
     */
    @FXML
    public void onQuickSelect1(Event e) {
        handleQuickSelection(0);
    }

    /**
     * Event trigger for when quick select option 2 is clicked in the interface.
     *
     * @param e event parameter
     */
    @FXML
    public void onQuickSelect2(Event e) {
        handleQuickSelection(1);
    }

    /**
     * Event trigger for when quick select option 3 is clicked in the interface.
     *
     * @param e event parameter
     */
    @FXML
    public void onQuickSelect3(Event e) {
        handleQuickSelection(2);
    }

    /**
     * Event trigger for when quick select option 4 is clicked in the interface.
     *
     * @param e event parameter
     */
    @FXML
    public void onQuickSelect4(Event e) {
        handleQuickSelection(3);
    }

    /**
     * Handles toggling the recurring checkbox, to be used when manually toggling the checkbox
     * or when clearing input.
     *
     * @param selected the state of the checkbox.
     */
    private void handleRecurringToggle(boolean selected) {
        createTransactionIsRecurring.setSelected(selected);

        createTransactionRecurringInterval.setDisable(!selected);
        createTransactionRecurringType.setDisable(!selected);
    }

    /**
     * Event triggered when clicking the recurring checkbox in the interface.
     *
     * @param e event parameter
     */
    @FXML
    public void toggleRecurring(Event e) {
        boolean selected = createTransactionIsRecurring.isSelected();
        
        handleRecurringToggle(selected);
    }

    /**
     * Event triggered when the user selects the graph view from the bottom menu.
     *
     * @param e event parameter
     */
    @FXML
    public void graphTabSelected(Event e) {
        ObservableList<Series<String, Float>> graph = graphItem.getData();
        Series<String, Float> data = dataManager.getGraphFromData();
        graph.clear();
        graph.add(data);
    }
}
