package no.ntnu.idatx2001.g11.logic;

import java.time.LocalDate;
import no.ntnu.idatx2001.g11.generics.Transaction;
import no.ntnu.idatx2001.g11.generics.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegressedGraphTest {
    private LocalDate now;
    private User testUser;
    private RegressedGraph validRegressedGraph;

    @BeforeEach
    void before() {
        now = LocalDate.now();
        testUser = new User("a", 10000);
        testUser.getTransactionHistory().addTransaction(new Transaction(
                "a", "b", -100, now.minusDays(2)
        ));
        testUser.getTransactionHistory().addTransaction(new Transaction(
                "a", "b", -600, now.minusDays(1)
        ));
        testUser.getTransactionHistory().addTransaction(new Transaction(
                "a", "b", -300, now.minusDays(1)
        ));
        testUser.getTransactionHistory().addTransaction(new Transaction(
                "a", "b", 500, now
        ));
        validRegressedGraph = new RegressedGraph(testUser, 2);
    }

    @Test
    void testCreationOfRegressedGraphWithNoUser() {
        assertThrows(IllegalArgumentException.class, () -> new RegressedGraph(null));
    }

    @Test
    void testCreationOfRegressedGraphWithInvalidRegressionRange() {
        assertThrows(IllegalArgumentException.class, () ->
                new RegressedGraph(testUser, 0));
        //The following test will falsely fail in about 25269511429072704 years
        assertThrows(IllegalArgumentException.class, () ->
                new RegressedGraph(testUser, Long.MAX_VALUE));
    }

    @Test
    void testCreationOfRegressedGraphWithNoRegressionRange() {
        assertDoesNotThrow(() -> new RegressedGraph(testUser));
    }

    @Test
    void testRegressionGraphExtrapolation() {
        assertEquals(10000, validRegressedGraph.extrapolate(now.minusDays(2)));
        assertEquals(9750, validRegressedGraph.extrapolate(now.minusDays(1)));
        assertEquals(9500, validRegressedGraph.extrapolate(now));
        assertEquals(9000, validRegressedGraph.extrapolate(now.plusDays(2)));
        assertEquals(-500, validRegressedGraph.extrapolate(now.plusDays(40)));
    }
}
