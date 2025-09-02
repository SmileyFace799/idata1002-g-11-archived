package no.ntnu.idatx2001.g11;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import no.ntnu.idatx2001.g11.controllers.ui.ExceptionCommunicator;
import no.ntnu.idatx2001.g11.exceptions.NoUserException;


/**
 * JavaFX Application.
 */
public class App extends Application {

    private static Scene scene;

    private final DataManager dataManager = DataManager.getInstance();
    private final ExceptionCommunicator exceptionCommunicator = ExceptionCommunicator.getInstance();
    
    /**
     * JavaFX generated function to load an FXML layout.
     *
     * @param stage stage currently in use
     */
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFxml("User Select Screen"), 600, 400);
        stage.setScene(scene);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent arg0) {
                if (dataManager.getCurrentUser() != null) {
                    try {
                        dataManager.forceSave();
                        System.out.println("Saved the user!");
                    } catch (NoUserException e) {
                        exceptionCommunicator.throwErrorDialogue(
                            "Failed to save",
                            "Failed to save the user. Current user does not exist.");
                    }
                }
            }
        });

        stage.show();
    }

    /**
     * JavaFX generated function to set the current root.
     *
     * @param fxml the FXML to set as a root
     * @throws IOException if the FXML file is not found
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFxml(fxml));
    }

    /**
     * JavaFX generated function to load FXML. Wrapper function.
     *
     * @param fxml the FXML file name to load
     * @return the fxmlLoader
     * @throws IOException if the FXML file is not found
     */
    private static Parent loadFxml(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Called by the main function. Calls {@code launch()} internally.
     */
    public static void initApp() {
        launch();
    }
}