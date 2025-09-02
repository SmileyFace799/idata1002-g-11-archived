package no.ntnu.idatx2001.g11.exceptions;

import no.ntnu.idatx2001.g11.generics.Transaction;

/**
 * Thrown when a purchase is not found in a set of data.
 */
public class TransactionNotFoundException extends RuntimeException {
    /**
     * Constructor.
     *
     * @param transaction the transaction that was not found within the data.
     */
    public TransactionNotFoundException(Transaction transaction) {
        super("Transaction \"" + transaction.getName() + "\" not found in transaction history");
    }
}
