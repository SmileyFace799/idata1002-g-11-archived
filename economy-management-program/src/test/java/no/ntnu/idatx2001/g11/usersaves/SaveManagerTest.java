package no.ntnu.idatx2001.g11.usersaves;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import no.ntnu.idatx2001.g11.enums.TimeType;
import no.ntnu.idatx2001.g11.generics.Frequency;
import no.ntnu.idatx2001.g11.generics.Transaction;
import no.ntnu.idatx2001.g11.generics.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for SaveManager
 */
class SaveManagerTest {

    private User validUser;
    private User emptyValidUser;

    @BeforeEach
    void before() {
        emptyValidUser = new User("empty user", 1000);
        validUser = new User("Test user", 15000);
        validUser
                .getTransactionHistory()
                .addTransaction(
                        new Transaction(
                                "Test name",
                                "Test category",
                                999.9f,
                                LocalDate.of(2021, 6, 17)
                        )
                );
        validUser
                .getTransactionHistory()
                .addTransaction(
                        new Transaction(
                                "Another test name",
                                "Another test category",
                                -13.37f,
                                LocalDate.of(2020, 2, 29)
                        )
                );
        validUser
                .getTransactionHistory()
                .addTransaction(
                        new Transaction(
                                "Recurring test transaction",
                                "Test category",
                                -420.69f,
                                LocalDate.of(1963, 12, 31),
                                new Frequency((short) 1, TimeType.YEARS)
                        )
                );
    }

    @Test
    void testSaveAndLoad() {
        assertDoesNotThrow(() -> SaveManager.saveUser(emptyValidUser, 69));
        User loadedUser = assertDoesNotThrow(() -> SaveManager.loadUserBytes(69).makeUser());
        assertEquals(emptyValidUser, loadedUser);

        assertDoesNotThrow(() -> SaveManager.saveUser(validUser, 69));
        loadedUser = assertDoesNotThrow(() -> SaveManager.loadUserBytes(69).makeUser());
        assertEquals(validUser, loadedUser);
    }
}
