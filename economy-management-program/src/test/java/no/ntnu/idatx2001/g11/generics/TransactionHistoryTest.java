package no.ntnu.idatx2001.g11.generics;

import java.time.LocalDate;
import no.ntnu.idatx2001.g11.exceptions.TransactionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionHistoryTest {
    private TransactionHistory validTransactionHistory;
    private Transaction validTransaction;

    @BeforeEach
    void before() {
        validTransactionHistory = new TransactionHistory();
        validTransaction = new Transaction("Test name", "Test category",
                500, LocalDate.now());
    }

    @Test
    void testValidCreationOfTransactionHistory() {
        assertTrue(validTransactionHistory.getTransactions().isEmpty());
    }

    @Test
    void testAdditionOfNullTransaction() {
        assertThrows(IllegalArgumentException.class, () -> validTransactionHistory
                .addTransaction(null));
    }

    @Test
    void testValidAdditionOfTransaction() {
        assertDoesNotThrow(() -> validTransactionHistory.addTransaction(validTransaction));
    }

    @Test
    void testRemovalOfNullTransaction() {
        assertThrows(IllegalArgumentException.class, () -> validTransactionHistory
                .removeTransaction(null));
    }

    @Test
    void testRemovalOfNonExistentTransaction() {
        assertThrows(TransactionNotFoundException.class, () -> validTransactionHistory
                .removeTransaction(validTransaction));
    }

    @Test
    void testValidRemovalOfTransaction() {
        validTransactionHistory.addTransaction(validTransaction);
        assertDoesNotThrow(() -> validTransactionHistory.removeTransaction(validTransaction));
    }

    @Test
    void testTransactionHistoryEquals() {
        assertEquals(validTransactionHistory, validTransactionHistory);
        assertNotEquals(null, validTransactionHistory);
        assertNotEquals(new Object(), validTransactionHistory);

        assertEquals(new TransactionHistory(), validTransactionHistory);

        validTransactionHistory.addTransaction(validTransaction);
        assertNotEquals(new TransactionHistory(), validTransactionHistory);
    }
}
