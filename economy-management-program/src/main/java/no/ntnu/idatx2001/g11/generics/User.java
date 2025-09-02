package no.ntnu.idatx2001.g11.generics;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import no.ntnu.idatx2001.g11.usersaves.Savable;

/**
 * Represents a user of the application.
 * Each user has a unique username and a list of expenses.
 */
public class User implements Savable {

    private final double startingFunds;
    /**
     * The list of expenses of the user.
     */
    private final TransactionHistory transactionHistory;
    /**
     * The username of the user.
     */
    private String username;

    // Note that this is only ever used in testing classes.
    /**
     * User constructor without starting funds.
     *
     * @param username username of this user
     * @deprecated all new users should be initialized with starting funds.
     * @see User#User(String, double)
     */
    public User(String username) {
        this(username, 0);
    }

    /**
     * Creates a new user with the given username.
     *
     * @param username The username of the user.
     * @param startingFunds the starting funds of the user.
     */
    public User(String username, double startingFunds) {
        setUsername(username);
        this.startingFunds = startingFunds;
        this.transactionHistory = new TransactionHistory();
    }

    /**
     * Returns the username of the user.
     *
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username The new username of the user.
     */
    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "String \"username\" cannot be null or blank"
            );
        }
        this.username = username;
    }

    /**
     * Gets the user's starting funds.
     *
     * @return the user's starting funds
     */
    public double getStartingFunds() {
        return startingFunds;
    }
    
    /**
     * Gets the user's current funds.
     *
     * @return the user's current funds
     */
    public double getCurrentFunds() {
        return startingFunds + transactionHistory.getSum();
    }

    /**
     * Returns the list of expenses of the user.
     *
     * @return The list of expenses of the user.
     */
    public TransactionHistory getTransactionHistory() {
        return transactionHistory;
    }

    /**
     * Returns the user's balance (income - expenses).
     *
     * @return The balance of the user.
     */
    public double getBalance() {
        return (transactionHistory.getTotalIncome()
                - transactionHistory.getTotalExpenses());
    }

    /**
     * Converts the user into an array of bytes.<br/><br/>
     * The bytes are stored as follows:<br/>
     * <ul>
     *     <li><b>Byte 0 - 7:</b> The user's starting funds.</li>
     *     <li><b>Byte 8:</b> The amount of bytes used to store the username.</li>
     *     <li><b>Byte 9 - n:</b> The username, encoded to bytes using UTF-8</li>
     *     <li><b>n+1 - m:</b> The user's transaction history,
     *     encoded to bytes as documented in {@link TransactionHistory#asBytes()}.</li>
     * </ul>
     *
     * @return The user, converted to an array of bytes.
     * @see TransactionHistory#asBytes()
     */
    @Override
    public byte[] asBytes() {
        byte[] usernameBytes = username.getBytes(StandardCharsets.UTF_8);

        byte[] byteArray = new byte[byteLength()];

        byte[] fundsBytes = ByteBuffer.allocate(8).putDouble(startingFunds).array();
        System.arraycopy(fundsBytes, 0, byteArray, 0, fundsBytes.length);

        byteArray[8] = (byte) usernameBytes.length;

        System.arraycopy(usernameBytes, 0, byteArray, 9, usernameBytes.length);

        byte[] transactionBytes = transactionHistory.asBytes();
        System.arraycopy(
                transactionBytes,
                0,
                byteArray,
                9 + usernameBytes.length,
                transactionBytes.length
        );

        return byteArray;
    }

    @Override
    public int byteLength() {
        return (9
                + username.getBytes(StandardCharsets.UTF_8).length
                + transactionHistory.byteLength());
    }

    /**
     * Determines if this user is equal to another object.
     * Two users are considered equal if they have the same username, transaction history,
     * and starting funds.
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
        User user = (User) obj;
        return (username.equals(user.getUsername())
                && transactionHistory.equals(user.getTransactionHistory())
                && startingFunds == user.getStartingFunds());
    }

    /**
     * Computes the hash code of this user.
     * The hash code is calculated based on the username, transaction history, and starting funds
     * of the user.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + username.hashCode();
        hash = 31 * hash + transactionHistory.hashCode();
        hash = 31 * hash + Double.hashCode(startingFunds);
        return hash;
    }

    @Override
    public String toString() {
        return "Username:\n  " + getUsername()
                + "\nStarting funds:\n  " + getStartingFunds()
                + "\nTransaction history:\n  " + getTransactionHistory()
                .toString().replace("\n", "\n  ");
    }
}
