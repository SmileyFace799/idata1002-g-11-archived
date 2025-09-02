package no.ntnu.idatx2001.g11.generics;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import no.ntnu.idatx2001.g11.usersaves.Savable;

/**
 * (replacing the RecurringTransaction and Data classes)
 * The Transaction class represents a financial transaction that can be
 * either a single event or a recurring transaction.
 * It stores information about the category, name, amount, date, income status,
 * and other details of a transaction.
 * The class provides methods for retrieving and setting these attributes,
 * as well as for determining the balance of the transaction.
 */

public class Transaction implements Savable {

    private String category;
    private String name;
    private float amount;
    private LocalDate date;
    private Frequency frequency;
    private String currency;
    private String[] tags;

    /**
     * Creates a Transaction object with the specified name, category, amount, and date.
     *
     * @param name     the name of the transaction; must not be null or empty
     * @param category the category of the transaction; must not be null or empty
     * @param amount   the amount of the transaction; if negative,
     *                 the transaction will be regarded as income
     * @param date     the date of the transaction; must not be null
     * @throws IllegalArgumentException if the name or category is null or empty,
     *                                  or if the date is null
     */

    public Transaction(
            String name,
            String category,
            float amount,
            LocalDate date
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name should not be null or empty");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException(
                    "Category should not be null or empty"
            );
        }
        if (date == null) {
            throw new IllegalArgumentException("LocalDate \"date\" cannot be null");
        }

        this.name = name;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    /**
     * Creates a recurring Transaction object with the given parameters.
     *
     * @param name      the name of the transaction
     * @param category  the category of the transaction
     * @param amount    the amount of the transaction.
     *                  If negative, the transaction will be regarded as income.
     * @param date      the date of the transaction
     * @param frequency the frequency of the transaction (in days)
     * @throws IllegalArgumentException if the category is null or empty,
     *                                  the monthly earnings are negative,
     *                                  or the frequency is less than or equal to zero.
     */
    public Transaction(
            String name,
            String category,
            float amount,
            LocalDate date,
            Frequency frequency
    ) {
        this(name, category, amount, date);
        this.frequency = frequency;
    }

    /**
     * Gets the date of this transaction.
     *
     * @return the date of this transaction.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date of this transaction.
     *
     * @param date the date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Gets the category of this transaction.
     *
     * @return the category of this transaction.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of this transaction.
     *
     * @param category the category to set.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the name of this transaction.
     *
     * @return the name of this transaction.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this transaction.
     *
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the cost/income of this transaction.
     *
     * @return the cost/income of this transaction.
     */
    public float getAmount() {
        return amount;
    }

    /**
     * Sets the cost/income of this transaction.
     *
     * @param amount the cost/income to set.
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    /**
     * Gets the absolute value of this transaction.
     *
     * @return the absolute value of this transaction.
     */
    public float getAbsAmount() {
        return Math.abs(amount);
    }

    /**
     * Checks if the transaction is income.
     *
     * @return true if the transaction is income.
     */
    public boolean isIncome() {
        return amount >= 0;
    }

    /**
     * Checks if the transaction is recurring.
     *
     * @return true if the transaction is recurring
     */
    public boolean isRecurring() {
        return frequency != null;
    }

    /**
     * Gets the frequency of this transaction.
     *
     * @return the frequency of this transaction.
     */
    public Frequency getFrequency() {
        return frequency;
    }

    /**
     * Sets the frequency of this transaction.
     *
     * @param frequency the frequency to set.
     */
    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    /**
     * Gets the currency of this transaction.
     *
     * @return the currency of this transaction.
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency of this transaction.
     *
     * @param currency the currency to set.
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the tags of this transaction.
     *
     * @return the tags of this transaction.
     */
    public String[] getTags() {
        return tags;
    }

    /**
     * Sets the tags of this transaction.
     *
     * @param tags the tags to set.
     */
    public void setTags(String[] tags) {
        this.tags = tags;
    }

    /**
     * Converts the transaction into an array of bytes.<br/><br/>
     * The bytes are stored as follows:<br/>
     * <ul>
     *     <li><b>Byte 0 - 1:</b> The year of the purchase date, as a short.</li>
     *     <li><b>Byte 2:</b> The month of the purchase date.</li>
     *     <li><b>Byte 3:</b> The day of the purchase date.</li>
     *     <li><b>Byte 4 - 7:</b> The purchase amount, as a float.</li>
     *     <li><b>Byte 8:</b> The amount of bytes used to store the category.</li>
     *     <li><b>Byte 9 - {@code n}:</b> The category, encoded to bytes using {@code UTF-8}.</li>
     *     <li><b>Byte {@code n}+1:</b> The amount of bytes used to store the transaction name.</li>
     *     <li><b>Byte {@code n}+2 - {@code m}:</b>
     *     The transaction name, encoded to bytes using {@code UTF-8}.</li>
     *     <li><b>Byte {@code m}+1:</b> If the transaction has a frequency</li>
     *     <li><b>Byte {@code m}+2 - {@code l}:</b> The transaction frequency,
     *     encoded to bytes as documented in {@link Frequency#asBytes()}.</li>
     * </ul>
     *
     * @return The data, converted to an array of bytes.
     * @see Frequency#asBytes()
     */

    @Override
    public byte[] asBytes() {
        byte[] byteArray = new byte[byteLength()];

        byte[] yearBytes = ByteBuffer
                .allocate(2)
                .putShort((short) date.getYear())
                .array();
        System.arraycopy(yearBytes, 0, byteArray, 0, yearBytes.length);

        byteArray[2] = (byte) date.getMonthValue();

        byteArray[3] = (byte) date.getDayOfMonth();

        byte[] amountBytes = ByteBuffer.allocate(4).putFloat(amount).array();
        System.arraycopy(amountBytes, 0, byteArray, 4, amountBytes.length);

        byte[] categoryBytes = category.getBytes(StandardCharsets.UTF_8);
        byteArray[8] = (byte) categoryBytes.length;
        System.arraycopy(categoryBytes, 0, byteArray, 9, categoryBytes.length);
        int byteIndex = 9 + categoryBytes.length;

        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        byteArray[byteIndex] = (byte) nameBytes.length;
        byteIndex++;
        System.arraycopy(nameBytes, 0, byteArray, byteIndex, nameBytes.length);
        byteIndex += nameBytes.length;

        if (frequency != null) {
            byteArray[byteIndex] = 1;
            byteIndex++;
            System.arraycopy(
                    frequency.asBytes(),
                    0,
                    byteArray,
                    byteIndex,
                    frequency.byteLength()
            );
        } else {
            byteArray[byteIndex] = 0;
        }
        return byteArray;
    }

    @Override
    public int byteLength() {
        return (11
                + category.getBytes(StandardCharsets.UTF_8).length
                + name.getBytes(StandardCharsets.UTF_8).length
                + (frequency != null ? frequency.byteLength() : 0));
    }

    /**
     * Determines if this transaction is equal to another object.
     * Two transactions are considered equal if they have the same name, category, amount,
     * date, and frequency (if applicable).
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Transaction transaction = (Transaction) obj;
        return (name.equals(transaction.getName())
                && category.equals(transaction.getCategory())
                && amount == transaction.getAmount()
                && date.equals(transaction.getDate())
                && (
                frequency != null
                        ? frequency.equals(transaction.getFrequency())
                        : transaction.getFrequency() == null));
    }

    /**
     * Computes the hash code of this transaction.
     * The hash code is calculated based on the name, category, amount, date,
     * and frequency (if applicable) of the transaction.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + name.hashCode();
        hash = 31 * hash + category.hashCode();
        hash = 31 * hash + Float.hashCode(amount);
        hash = 31 * hash + date.hashCode();
        hash = 31 * hash + (frequency != null ? frequency.hashCode() : 0);
        return hash;
    }

    /**
     * Returns a string representation of the Transaction object, for debugging purposes.
     * The string includes the name, category, amount, date, isIncome,
     * isRecurring and frequency.
     *
     * @return a string representation of the Transaction object.
     */
    @Override
    public String toString() {
        return "Name:\n  " + getName()
                + "\nCategory:\n  " + getCategory()
                + "\nAmount:\n  " + getAmount()
                + "\nDate:\n  " + getDate()
                + "\nIncome?\n  " + isIncome()
                + "\nRecurring?\n  " + isRecurring()
                + "\nFrequency:\n  " + (getFrequency() == null ? null
                : getFrequency().toString().replace("\n", "\n  "));

    }
}
