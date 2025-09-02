package no.ntnu.idatx2001.g11;

import java.nio.file.NoSuchFileException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import javafx.scene.chart.XYChart;
import no.ntnu.idatx2001.g11.exceptions.NoUserException;
import no.ntnu.idatx2001.g11.generics.Transaction;
import no.ntnu.idatx2001.g11.generics.User;
import no.ntnu.idatx2001.g11.logic.RegressedGraph;
import no.ntnu.idatx2001.g11.usersaves.SaveManager;

/**
 * Class responsible for communicating data between the front-end and the back-end.
 * Acts as an interface between the two to communicate events such as creating new transactions
 * and new users, or deleting transactions and such.
 */
public class DataManager {

    private static DataManager instance = null;
    private List<Transaction> transactionList;

    private int currentSlot = 0;
    private User currentUser = null;
    private int saveIntervalCounter = 0;
    private static final int SAVE_INTERVAL_MAX = 5;

    private DataManager() {}

    /**
     * Singleton getter.
     *
     * @return singleton
     */
    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }

        return instance;
    }

    private void setUser(int slot, User user) {
        currentSlot = slot;
        currentUser = user;
        transactionList = user.getTransactionHistory().getTransactions();
    }

    /**
     * Called by the front-end when a save slot has been
     * selected by the user. Should trigger loading a
     * save from the given slot.
     *
     * @param slot the slot to load from (0 to 2)
     * @throws NoUserException if the user does not exist
     */
    public void loadUserFromSlot(int slot) throws NoUserException {
        try {
            setUser(slot, SaveManager.loadUserBytes(slot).makeUser());
        } catch (NoSuchFileException nsfe) {
            throw new NoUserException();
        }
    }

    /**
     * Called by the front-end when the user has
     * submitted a valid username when creating
     * a new user. Should create a new save file
     * for that user.
     *
     * @param slot the slot to load from (0 to 2)
     * @param username submitted username
     * @param startingFunds submitted starting funds
     */
    public void createFromUsername(int slot, String username, double startingFunds) {
        setUser(slot, new User(username, startingFunds));
        SaveManager.saveUser(currentUser, slot);
    }

    /**
     * Forces the program to save the data of the current user.
     *
     * @throws NoUserException if the user does not exist
     */
    public void forceSave() throws NoUserException {
        if (currentUser != null) {
            SaveManager.saveUser(currentUser, currentSlot);
        } else {
            throw new NoUserException();
        }
    }

    /**
     * Counts a saveable action. When called 5 times, saves the user's data.
     */
    private void saveAfterInterval() {
        saveIntervalCounter = saveIntervalCounter++ % SAVE_INTERVAL_MAX;
        if (saveIntervalCounter == 0) {
            forceSave();
        }
    }

    /**
     * Attempts to submit a new transaction to the current user's data.
     *
     * @param transaction the transaction to be saved
     * @throws NoUserException if the user does not exist
     */
    public void submitNewTransaction(Transaction transaction) throws NoUserException {
        if (currentUser != null) {
            currentUser.getTransactionHistory().addTransaction(transaction);

            transactionList.sort((Transaction a, Transaction b) -> {
                return a.getDate().compareTo(b.getDate());
            });
        } else {
            throw new NoUserException();
        }
        saveAfterInterval();
    }

    /**
     * Used by the user-select front-end to display the names
     * associated with the individual save files.
     *
     * @return an array of 3 strings, representing the usernames
     *     of the users, in order of the slot (entry [0] should
     *     be for save slot 1, etc.). If a slot is empty, the name
     *     should be "Empty".
     */
    public String[] fetchUserNames() {
        String[] userNames = new String[3];
        for (int i = 0; i < 3; i++) {
            try {
                userNames[i] = SaveManager.loadUserBytes(i)
                        .getString("username", "(No username)");
            } catch (NoSuchFileException nsfe) {
                userNames[i] = "";
            }
        }
        return userNames;
    }

    /**
     * Returns a list of all outgoing purchases (transactions with a negative amount).
     *
     * @return a list of all outgoing purchases.
     */
    public List<Transaction> getOutgoingPurchases() {
        return transactionList
                .stream()
                .filter(transaction -> !transaction.isIncome())
                .toList();
    }

    /**
     * Returns a list of all incoming purchases (transactions with a positive amount).
     *
     * @return a list of all incoming purchases.
     */
    public List<Transaction> getIncomingPurchases() {
        return transactionList
                .stream()
                .filter(Transaction::isIncome)
                .toList();
    }

    /**
     * Adds a new transaction to the list of transactions.
     *
     * @param transaction the transaction to add.
     * @throws IllegalArgumentException if the transaction is null.
     */
    public void addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        transactionList.add(transaction);
    }

    /**
     * Get's the stored transaction list.
     *
     * @return the stored transaction list
     */
    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    /**
     * Gets the current user.
     *
     * @return the current user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Creates a JavaFX chart based on regressed data.
     *
     * @return a chart based on the transactionList with extrapolated data.
     */
    public XYChart.Series<String, Float> getGraphFromData() {
        XYChart.Series<String, Float> series = new XYChart.Series<>();

        double currentMoney = currentUser.getStartingFunds();

        Iterator<Transaction> transactions = transactionList.iterator();

        while (transactions.hasNext()) {
            Transaction transaction = transactions.next();

            currentMoney += transaction.getAmount();

            XYChart.Data<String, Float> data =
                new XYChart.Data<String, Float>(
                    transaction.getDate().toString(),
                    (float) currentMoney);

            series.getData().add(data);
        }

        RegressedGraph graphModel = new RegressedGraph(currentUser);

        Transaction finalTransaction = transactionList.get(transactionList.size() - 1);
        for (int i = 0; i < RegressedGraph.DEFAULT_RANGE; i++) {
            LocalDate extrapolatedDate = finalTransaction.getDate().plusDays(i + 1);
            float extrapolatedAmount = (float) graphModel.extrapolate(extrapolatedDate);
            XYChart.Data<String, Float> data =
                new XYChart.Data<String, Float>(extrapolatedDate.toString(), extrapolatedAmount);

            series.getData().add(data);
            
        }

        return series;
    }
}
