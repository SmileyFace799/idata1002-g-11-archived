package no.ntnu.idatx2001.g11.generics;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

  private User validUser;

  @BeforeEach
  void before() {
    validUser = new User("Test name");
  }

  @Test
  void testCreationOfUserWithNoUsername() {
    assertThrows(IllegalArgumentException.class, () -> new User(null));
    assertThrows(IllegalArgumentException.class, () -> new User("  "));
  }

  @Test
  void testValidCreationOfUser() {
    assertEquals("Test name", validUser.getUsername());
    assertNotNull(validUser.getTransactionHistory());
    assertTrue(validUser.getTransactionHistory().getTransactions().isEmpty());
  }

  @Test
  void testSetUsernameWithNoUsername() {
    assertThrows(
      IllegalArgumentException.class,
      () -> validUser.setUsername(null)
    );
    assertThrows(
      IllegalArgumentException.class,
      () -> validUser.setUsername("  ")
    );
  }

  @Test
  void testUserEquals() {
    assertEquals(validUser, validUser);
    assertNotEquals(null, validUser);
    assertNotEquals(new Object(), validUser);

    assertEquals(new User("Test name"), validUser);

    assertNotEquals(new User("Another name"), validUser);
    validUser
      .getTransactionHistory()
      .addTransaction(
        new Transaction("Test name", "Test category", 5, LocalDate.now())
      );
    assertNotEquals(new User("Test name"), validUser);
  }

  @Test
  void testAsBytesAndByteLength() {
    double startingFunds = 1500.0;
    String username = "TestUser";
    User user = new User(username, startingFunds);

    // Add a transaction to the user's transaction history
    Transaction transaction = new Transaction(
      "Groceries",
      "Food",
      -50.0f,
      LocalDate.now()
    );
    user.getTransactionHistory().addTransaction(transaction);

    byte[] userBytes = user.asBytes();
    assertEquals(user.byteLength(), userBytes.length);

    ByteBuffer buffer = ByteBuffer.wrap(userBytes);
    assertEquals(startingFunds, buffer.getDouble(), 0.001);

    int usernameLength = buffer.get();
    byte[] usernameBytes = new byte[usernameLength];
    buffer.get(usernameBytes);
    assertEquals(username, new String(usernameBytes, StandardCharsets.UTF_8));

    byte[] transactionHistoryBytes = Arrays.copyOfRange(
      userBytes,
      buffer.position(),
      userBytes.length
    );
    byte[] originalTransactionHistoryBytes = user
      .getTransactionHistory()
      .asBytes();
    assertArrayEquals(originalTransactionHistoryBytes, transactionHistoryBytes);
  }

  @Test
  void testStartingAndCurrentFunds() {
    double startingFunds = 2000.0;
    User user = new User("TestUser", startingFunds);
    assertEquals(startingFunds, user.getStartingFunds());

    double currentFunds = startingFunds + 100 - 50;
    user
      .getTransactionHistory()
      .addTransaction(
        new Transaction("Test Income", "Income", 100, LocalDate.now())
      );
    user
      .getTransactionHistory()
      .addTransaction(
        new Transaction("Test Expense", "Expense", -50, LocalDate.now())
      );
    assertEquals(currentFunds, user.getCurrentFunds());
  }

  @Test
  void testTransactionHistory() {
    validUser
      .getTransactionHistory()
      .addTransaction(
        new Transaction("Test Income", "Income", 100, LocalDate.now())
      );
    validUser
      .getTransactionHistory()
      .addTransaction(
        new Transaction("Test Expense", "Expense", -50, LocalDate.now())
      );

    assertEquals(2, validUser.getTransactionHistory().getTransactions().size());
    assertEquals(100, validUser.getTransactionHistory().getTotalIncome());
    assertEquals(50, validUser.getTransactionHistory().getTotalExpenses());
  }

  @Test
  void testGetBalance() {
    validUser
      .getTransactionHistory()
      .addTransaction(
        new Transaction("Test Income", "Income", 100, LocalDate.now())
      );
    validUser
      .getTransactionHistory()
      .addTransaction(
        new Transaction("Test Expense", "Expense", -50, LocalDate.now())
      );

    assertEquals(50, validUser.getBalance());
  }

  @Test
  void testHashCode() {
    User user1 = new User("TestUser", 2000.0);
    User user2 = new User("TestUser", 2000.0);
    User user3 = new User("AnotherUser", 1500.0);

    assertEquals(user1.hashCode(), user2.hashCode());
    assertNotEquals(user1.hashCode(), user3.hashCode());
  }
}
