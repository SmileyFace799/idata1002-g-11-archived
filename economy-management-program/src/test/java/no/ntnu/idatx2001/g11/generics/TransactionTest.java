package no.ntnu.idatx2001.g11.generics;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import no.ntnu.idatx2001.g11.enums.TimeType;
import org.junit.jupiter.api.Test;

/**
 * A test suite for the {@link Transaction} class.
 */
class TransactionTest {

  /**
   * Tests that a valid one-time transaction can be created.
   */
  @Test
  void testOneTimeTransaction() {
    // Arrange
    String name = "Bought groceries";
    String category = "Food";
    float amount = -100.00f;
    LocalDate date = LocalDate.now();

    // Act
    Transaction transaction = new Transaction(name, category, amount, date);

    // Assert
    assertEquals(name, transaction.getName());
    assertEquals(category, transaction.getCategory());
    assertEquals(amount, transaction.getAmount(), 0.001);
    assertEquals(date, transaction.getDate());
    assertFalse(transaction.isIncome());
    assertFalse(transaction.isRecurring());
  }

  /**
   * Tests that a valid recurring transaction can be created.
   */
  @Test
  void testRecurringTransaction() {
    // Arrange
    String name = "Monthly pay";
    String category = "Salary";
    float amount = 5000.00f;
    LocalDate date = LocalDate.now();
    Frequency frequency = new Frequency((short) 1, TimeType.MONTHS);

    // Act
    Transaction transaction = new Transaction(
      name,
      category,
      amount,
      date,
      frequency
    );

    // Assert
    assertEquals(category, transaction.getCategory());
    assertEquals(amount, transaction.getAmount(), 0.001);
    assertEquals(date, transaction.getDate());
    assertTrue(transaction.isIncome());
    assertTrue(transaction.isRecurring());
    assertEquals(frequency, transaction.getFrequency());
  }

  /**
   * Tests that an exception is thrown when the category is null or empty.
   */
  @Test
  void testNameAndCategoryNull() {
    // Arrange
    String name = "Test name";
    String category = "Test category";
    float amount = -100.00f;
    LocalDate date = LocalDate.now();

    // Assert
    assertThrows(
      IllegalArgumentException.class,
      () -> new Transaction(null, category, amount, date)
    );
    assertThrows(
      IllegalArgumentException.class,
      () -> new Transaction(name, null, amount, date)
    );
  }

  @Test
  void testAsBytes() {
    String name = "Bought groceries";
    String category = "Food";
    float amount = -100.00f;
    LocalDate date = LocalDate.now();
    Frequency frequency = new Frequency((short) 1, TimeType.DAYS);

    Transaction transaction = new Transaction(
      name,
      category,
      amount,
      date,
      frequency
    );
    byte[] transactionBytes = transaction.asBytes();

    ByteBuffer buffer = ByteBuffer.allocate(transaction.byteLength());
    buffer.putShort((short) date.getYear());
    buffer.put((byte) date.getMonthValue());
    buffer.put((byte) date.getDayOfMonth());
    buffer.putFloat(amount);

    byte[] categoryBytes = category.getBytes(StandardCharsets.UTF_8);
    buffer.put((byte) categoryBytes.length);
    buffer.put(categoryBytes);

    byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
    buffer.put((byte) nameBytes.length);
    buffer.put(nameBytes);

    buffer.put((byte) 1); // Frequency is not null
    buffer.put(frequency.asBytes());

    byte[] expectedBytes = buffer.array();

    assertArrayEquals(expectedBytes, transactionBytes);
  }

  @Test
  void testEqualsAndHashCode() {
    String name = "Bought groceries";
    String category = "Food";
    float amount = -100.00f;
    LocalDate date = LocalDate.now();
    Frequency frequency = new Frequency((short) 1, TimeType.DAYS);

    Transaction transaction1 = new Transaction(
      name,
      category,
      amount,
      date,
      frequency
    );
    Transaction transaction2 = new Transaction(
      name,
      category,
      amount,
      date,
      frequency
    );
    Transaction transaction3 = new Transaction(
      name,
      "Different Category",
      amount,
      date,
      frequency
    );

    assertEquals(transaction1, transaction2);
    assertEquals(transaction2, transaction1);
    assertNotEquals(transaction1, transaction3);
    assertNotEquals(transaction2, transaction3);

    assertEquals(transaction1.hashCode(), transaction2.hashCode());
    assertNotEquals(transaction1.hashCode(), transaction3.hashCode());
    assertNotEquals(transaction2.hashCode(), transaction3.hashCode());
  }

  @Test
  void testAmountEdgeCases() {
    String name = "Test";
    String category = "Test Category";
    LocalDate date = LocalDate.now();

    Transaction zeroAmount = new Transaction(name, category, 0.0f, date);
    assertTrue(zeroAmount.isIncome());

    Transaction nearZeroPositive = new Transaction(
      name,
      category,
      0.001f,
      date
    );
    assertTrue(nearZeroPositive.isIncome());

    Transaction nearZeroNegative = new Transaction(
      name,
      category,
      -0.001f,
      date
    );
    assertFalse(nearZeroNegative.isIncome());

    Transaction largePositive = new Transaction(name, category, 1e9f, date);
    assertTrue(largePositive.isIncome());

    Transaction largeNegative = new Transaction(name, category, -1e9f, date);
    assertFalse(largeNegative.isIncome());
  }

  @Test
  void testSettersAndGetAbsAmount() {
    String name = "Bought groceries";
    String category = "Food";
    float amount = -100.00f;
    LocalDate date = LocalDate.now();

    Transaction transaction = new Transaction(name, category, amount, date);

    String newName = "New groceries";
    String newCategory = "New Food";
    float newAmount = -200.00f;
    LocalDate newDate = LocalDate.now().plusDays(1);

    transaction.setName(newName);
    transaction.setCategory(newCategory);
    transaction.setAmount(newAmount);
    transaction.setDate(newDate);

    assertEquals(newName, transaction.getName());
    assertEquals(newCategory, transaction.getCategory());
    assertEquals(newAmount, transaction.getAmount(), 0.001);
    assertEquals(newDate, transaction.getDate());

    assertEquals(200.00f, transaction.getAbsAmount(), 0.001);
  }

  @Test
  void testChangeTransactionToRecurring() {
    String name = "Bought groceries";
    String category = "Food";
    float amount = -100.00f;
    LocalDate date = LocalDate.now();

    Transaction transaction = new Transaction(name, category, amount, date);
    assertFalse(transaction.isRecurring());

    Frequency frequency = new Frequency((short) 1, TimeType.MONTHS);
    transaction.setFrequency(frequency);
    assertTrue(transaction.isRecurring());
    assertEquals(frequency, transaction.getFrequency());
  }

  @Test
  void testChangeTransactionToNonRecurring() {
    String name = "Monthly pay";
    String category = "Salary";
    float amount = 5000.00f;
    LocalDate date = LocalDate.now();
    Frequency frequency = new Frequency((short) 1, TimeType.MONTHS);

    Transaction transaction = new Transaction(
      name,
      category,
      amount,
      date,
      frequency
    );
    assertTrue(transaction.isRecurring());

    transaction.setFrequency(null);
    assertFalse(transaction.isRecurring());
    assertNull(transaction.getFrequency());
  }
}
