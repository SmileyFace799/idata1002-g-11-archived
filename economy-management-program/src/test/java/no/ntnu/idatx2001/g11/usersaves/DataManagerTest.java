package no.ntnu.idatx2001.g11.usersaves;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import no.ntnu.idatx2001.g11.DataManager;
import no.ntnu.idatx2001.g11.generics.Transaction;
import no.ntnu.idatx2001.g11.generics.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataManagerTest {

    private DataManager dataManager;

    @BeforeAll
    static void globalSetUp() {
        //Makes sure the "load user"-test won't falsely fail.
        SaveManager.saveUser(new User("TestUser"), 69);
    }

    /**
     * Sets up the test fixture by creating a new instance of {@link DataManager}.
     */
    @BeforeEach
    void setUp() {
        dataManager = DataManager.getInstance();
        dataManager.createFromUsername(69, "TestUser", 69.99);
    }

    /**
     * Positive test for the {@link DataManager#createFromUsername(int, String, double)} method.
     * It tests that the method successfully creates a new user with the given username.
     */
    @Test
    void testCreateFromUsername() {
        //User created in @BeforeEach
        assertEquals("TestUser", dataManager.getCurrentUser().getUsername());
    }

    /**
     * Negative test for the {@link DataManager#createFromUsername(int, String, double)} method.
     * It tests that the method throws a {@link NullPointerException} if the username is null.
     */
    @Test
    void testCreateFromUsernameWithNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> dataManager.createFromUsername(69, null, -1)
        );
    }

    /**
     * Negative test for the {@link DataManager#createFromUsername(int, String, double)} method.
     * It tests that the method throws an {@link IllegalArgumentException} if the username is an empty string.
     */
    @Test
    void testCreateFromUsernameWithEmptyString() {
        assertThrows(
                IllegalArgumentException.class,
                () -> dataManager.createFromUsername(69, "", -1)
        );
    }

    /**
     * Positive test for the {@link DataManager#loadUserFromSlot(int)} method.
     * It tests that the method successfully loads the user with the given slot number.
     */
    @Test
    void testLoadUserFromSlot() {
        dataManager.loadUserFromSlot(69);
        assertNotNull(dataManager.getCurrentUser());
        assertEquals("TestUser", dataManager.getCurrentUser().getUsername());
    }



    /**
     * Positive test for the {@link DataManager#submitNewTransaction(Transaction)} method.
     * It tests that the method successfully adds the new transaction to the user's transaction history.
     */
    @Test
    void testSubmitNewTransaction() {
        Transaction transaction = new Transaction(
                "Test",
                "TestCategory",
                -100,
                LocalDate.now()
        );
        dataManager.submitNewTransaction(transaction);
        List<Transaction> transactionList = dataManager
                .getCurrentUser()
                .getTransactionHistory()
                .getTransactions();
        assertTrue(transactionList.contains(transaction));
    }

    /**
     * Negative test for the {@link DataManager#submitNewTransaction(Transaction)} method.
     * It tests that the method throws a {@link NullPointerException} if the transaction is null.
     */
    @Test
    void testSubmitNewTransactionWithNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> dataManager.submitNewTransaction(null)
        );
    }

    @Test
    void testGetOutgoingPurchases() {
        Transaction transaction1 = new Transaction(
                "Test1",
                "TestCategory",
                -100,
                LocalDate.now()
        );
        Transaction transaction2 = new Transaction(
                "Test2",
                "TestCategory",
                200,
                LocalDate.now()
        );
        Transaction transaction3 = new Transaction(
                "Test3",
                "TestCategory",
                -300,
                LocalDate.now()
        );
        dataManager.submitNewTransaction(transaction1);
        dataManager.submitNewTransaction(transaction2);
        dataManager.submitNewTransaction(transaction3);
        List<Transaction> outgoingPurchases = dataManager.getOutgoingPurchases();
        assertTrue(outgoingPurchases.contains(transaction1));
        assertFalse(outgoingPurchases.contains(transaction2));
        assertTrue(outgoingPurchases.contains(transaction3));
    }

    @Test
    void testGetIncomingPurchases() {
        Transaction transaction1 = new Transaction(
                "Test1",
                "TestCategory",
                -100,
                LocalDate.now()
        );
        Transaction transaction2 = new Transaction(
                "Test2",
                "TestCategory",
                200,
                LocalDate.now()
        );
        Transaction transaction3 = new Transaction(
                "Test3",
                "TestCategory",
                -300,
                LocalDate.now()
        );
        dataManager.submitNewTransaction(transaction1);
        dataManager.submitNewTransaction(transaction2);
        dataManager.submitNewTransaction(transaction3);
        List<Transaction> incomingPurchases = dataManager.getIncomingPurchases();
        assertFalse(incomingPurchases.contains(transaction1));
        assertTrue(incomingPurchases.contains(transaction2));
        assertFalse(incomingPurchases.contains(transaction3));
    }

    @Test
    void testAddTransaction() {
        Transaction transaction = new Transaction(
                "Test",
                "TestCategory",
                -100,
                LocalDate.now()
        );
        dataManager.addTransaction(transaction);
        List<Transaction> transactionList = dataManager.getTransactionList();
        assertTrue(transactionList.contains(transaction));
    }

    @Test
    void testAddTransactionWithNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> dataManager.addTransaction(null)
        );
    }
}
