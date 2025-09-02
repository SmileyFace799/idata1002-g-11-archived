package no.ntnu.idatx2001.g11.controllers.ui;

import java.time.LocalDate;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import no.ntnu.idatx2001.g11.App;
import no.ntnu.idatx2001.g11.generics.Transaction;

/**
 * A JavaFX element representing a list entry for new transactions.
 */
public class PurchaseListing extends HBox {
    /**
     * Image asset used for trash icon.
     */
    public static final Image trashImage = new Image(App.class
        .getResource("Images/iconDelete.png").toExternalForm());

    private ImageView trashButton;

    /**
     * Constructor. Creates a new list entry based on a transaction.
     *
     * @param transaction transaction to base the entry off of
     */
    public PurchaseListing(Transaction transaction) {
        this.setPrefHeight(34);

        Label costLabel = new Label(String.format("%.2f kr", transaction.getAmount()));
        costLabel.getStyleClass().add("table-price");
        costLabel.setPrefWidth(128);
        costLabel.setMaxWidth(128);
        costLabel.setMaxHeight(Double.MAX_VALUE);
        
        
        Label productName = new Label(transaction.getName());
        productName.getStyleClass().add("table-product");
        productName.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(productName, Priority.ALWAYS);
        
        Label productCategory = new Label(transaction.getCategory());
        productCategory.getStyleClass().add("table-category");
        productCategory.setMaxWidth(Double.MAX_VALUE);
        
        VBox productInfoContainer = new VBox();
        productInfoContainer.getChildren().add(productName);
        productInfoContainer.getChildren().add(productCategory);
        HBox.setHgrow(productInfoContainer, Priority.ALWAYS);

        Label dateLabel = new Label(makeDateReadable(transaction.getDate()));
        dateLabel.getStyleClass().add("table-date");
        dateLabel.setMaxWidth(84d);
        dateLabel.setPrefWidth(84d);
        dateLabel.setMinWidth(84d);
        dateLabel.setMaxHeight(Double.MAX_VALUE);

        trashButton = new ImageView(trashImage);
        trashButton.setFitWidth(24d);
        trashButton.setFitHeight(24d);
        trashButton.setCursor(Cursor.HAND);
        HBox.setMargin(trashButton, new Insets(10d, 21d, 0d, 21d));

        this.getChildren().add(costLabel);
        this.getChildren().add(productInfoContainer);
        this.getChildren().add(dateLabel);
        this.getChildren().add(trashButton);
        this.getStyleClass().add("table-item");
    }

    private String makeDateReadable(LocalDate date) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(date.getDayOfMonth());
        stringBuilder.append("/");
        stringBuilder.append(date.getMonthValue());
        stringBuilder.append("/");
        stringBuilder.append(date.getYear());

        return stringBuilder.toString();
    }

    /**
     * Gets the trash button of this listing, for use with events.
     *
     * @return the trash button.
     */
    public ImageView getTrashButton() {
        return this.trashButton;
    }
}
