package no.ntnu.idatx2001.g11.generics;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.ntnu.idatx2001.g11.exceptions.TransactionNotFoundException;
import no.ntnu.idatx2001.g11.usersaves.Savable;

/**
 * Holds the entire transaction history for a single user,
 * and has the ability to save it as binary.
 */
public class TransactionHistory implements Savable {
    private final List<Transaction> transactionList;

    /**
     * Constructor.
     */
    public TransactionHistory() {
        transactionList = new ArrayList<>();
    }

    /**
     * Gets transactions stored in this object.
     *
     * @return transactions stored in this object.
     */
    public List<Transaction> getTransactions() {
        return transactionList;
    }

    /**
     * Returns a sub-set of the transaction history,
     * containing only transactions within a specified time frame.
     *
     * @param startDate the start date of the range (Inclusive).
     * @param endDate   the end date of the range (Exclusive).
     * @return A {@link TransactionHistory} containing only
     *         transactions within the specified date range.
     */
    public TransactionHistory getTransactionsInRange(LocalDate startDate, LocalDate endDate) {
        TransactionHistory transactionsInRange = new TransactionHistory();
        for (Transaction transaction : transactionList) {
            if (!transaction.getDate().isBefore(startDate)
                    && transaction.getDate().isBefore(endDate)) {
                transactionsInRange.addTransaction(transaction);
            }
        }
        return transactionsInRange;
    }

    /**
     * Returns a map containing all transaction, grouped by income and expenses.
     * The values are all sub-sets of the transactions contained within the transaction history.
     * <br/>
     * The map will always have 2 keys: {@code Income} and {@code Expenses}.
     *
     * @return The map containing the grouped transactions.
     */
    public Map<String, TransactionHistory> getTransactionsByType() {
        HashMap<String, TransactionHistory> transactionsByType = new HashMap<>();
        transactionsByType.put("Income", new TransactionHistory());
        transactionsByType.put("Expenses", new TransactionHistory());
        transactionList.forEach(transaction -> transactionsByType.get(
                transaction.isIncome() ? "Income" : "Expenses").addTransaction(transaction));
        return transactionsByType;
    }

    /**
     * Returns a map containing all transactions, grouped by transaction category.
     * The values are all sub-sets of the transactions contained within the transaction history.
     * <br/>
     * The map's keys will all be categories for existing transactions.<br/>
     * <b>NB: If there is no transaction of a specific category in the transaction history,
     * the category will not be included in the returned map.</b>
     *
     * @return The map of expenses grouped by category.
     */
    public Map<String, TransactionHistory> getTransactionsByCategory() {
        HashMap<String, TransactionHistory> transactionsByCategory = new HashMap<>();
        for (Transaction transaction : transactionList) {
            String category = transaction.getCategory();
            transactionsByCategory.computeIfAbsent(category, c -> new TransactionHistory());
            transactionsByCategory.get(category).addTransaction(transaction);
        }
        return transactionsByCategory;
    }

    /**
     * Adds a transaction to the transaction history.
     * Will throw a {@code DuplicateTransactionException} if the transaction already exists.
     *
     * @param transaction The transaction to add.
     */
    public void addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("\"transaction\" cannot be null");
        }
        transactionList.add(transaction);
    }

    /**
     * Removes a transaction from the transaction history.
     * Will throw a {@code TransactionNotFoundException} if the transaction doesn't exist.
     *
     * @param transaction The transaction to remove.
     */
    public void removeTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("\"transaction\" cannot be null");
        }
        if (!transactionList.remove(transaction)) {
            throw new TransactionNotFoundException(transaction);
        }
    }

    /**
     * Gets the sum of every income/expense in the transaction history.
     * Numbers are converted to {@code double} before summing, to prevent loss of precision.
     *
     * @param getIncome If this method should return the total income.
     *                  Returns total expenses otherwise.
     * @return The sum of every income/expense.
     */
    private double getTransactionTotal(boolean getIncome) {
        return transactionList
                .stream()
                .filter(transaction -> transaction.isIncome() == getIncome)
                .mapToDouble(Transaction::getAbsAmount)
                .sum();
    }

    /**
     * Gets the sum of every income in the transaction history.
     * Numbers are converted to {@code double} before summing, to prevent loss of precision.
     *
     * @return The sum of every income.
     */
    public double getTotalIncome() {
        return getTransactionTotal(true);
    }

    /**
     * Gets the sum of every expense in the transaction history.
     * Numbers are converted to {@code double} before summing, to prevent loss of precision.
     *
     * @return The sum of every expense.
     */
    public double getTotalExpenses() {
        return getTransactionTotal(false);
    }

    /**
     * Gets the sum of every transaction in the transaction history.
     * Numbers are converted to {@code double} before summing, to prevent loss of precision.
     *
     * @return The sum of every transaction.
     */
    public double getSum() {
        return transactionList
                .stream()
                .mapToDouble(transaction -> (double) transaction.getAmount())
                .sum();
    }

    /**
     * Converts the transaction history into an array of bytes.<br/><br/>
     * The bytes are stored as follows:<br/>
     * <ul>
     *     <li><b>Byte 0 - 1:</b> The amount of transactions in the history, as a short.</li>
     *     <li><b>Byte 2 - n:</b> A transaction, encoded to bytes as documented in
     *     {@link Transaction#asBytes()}.</li>
     *     <li><b>Byte n+1 - m:</b> Another transaction, encoded to bytes.</li>
     * </ul>
     * This is repeated for every transaction in the transaction history.
     *
     * @return The transaction history, converted to an array of bytes.
     * @see Transaction#asBytes()
     */
    @Override
    public byte[] asBytes() {
        byte[] byteArray = new byte[byteLength()];

        byte[] transactionAmountBytes = ByteBuffer
                .allocate(2)
                .putShort((short) transactionList.size())
                .array();
        System.arraycopy(transactionAmountBytes, 0, byteArray, 0,
                transactionAmountBytes.length);

        int byteOffset = 2;
        for (Transaction transaction : transactionList) {
            byte[] transactionBytes = transaction.asBytes();
            System.arraycopy(transactionBytes, 0, byteArray, byteOffset,
                    transactionBytes.length);
            byteOffset += transactionBytes.length;
        }

        return byteArray;
    }

    @Override
    public int byteLength() {
        return 2 + transactionList.stream().mapToInt(Transaction::byteLength).sum();
    }

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
        TransactionHistory transactionHistory = (TransactionHistory) obj;
        return transactionList.equals(transactionHistory.getTransactions());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + transactionList.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "Transactions:\n  "
                + String.join("\n", getTransactions()
                        .stream()
                        .map(Transaction::toString)
                        .toList())
                .replace("\n", "\n  ");
    }
}
