package no.ntnu.idatx2001.g11.logic;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.ToDoubleFunction;
import no.ntnu.idatx2001.g11.generics.Transaction;
import no.ntnu.idatx2001.g11.generics.User;

/**
 * A linearly regressed graph from a user's transaction data.
 */
public class RegressedGraph {
    /**
     * The default range of days used when regressing data.
     */
    public static final int DEFAULT_RANGE = 5;

    private final ToDoubleFunction<LocalDate> regressedFunction;

    /**
     * Uses linear regression to create a graph based on a user's recent transactions,
     * which allows for extrapolation of future data.<br/>
     * This uses the default range for the amount of days.
     *
     * @param user The user to create the regressed graph for.
     * @see #RegressedGraph(User, long)
     * @see #DEFAULT_RANGE
     */
    public RegressedGraph(User user) {
        this(user, DEFAULT_RANGE);
    }

    /**
     * Uses linear regression to create a graph based on a user's recent transactions,
     * which allows for extrapolation of future data.
     *
     * @param user The user to create the regressed graph for.
     * @param regressionRangeDays How many days back to regress data for.
     */
    public RegressedGraph(User user, long regressionRangeDays) {
        if (user == null) {
            throw new IllegalArgumentException("\"user\" cannot be null");
        }
        if (regressionRangeDays <= 0) {
            throw new IllegalArgumentException(
                    "long \"regressionRangeDays\" must be greater than 0");
        }
        LocalDate endDate = user
                .getTransactionHistory()
                .getTransactions()
                .stream()
                .map(Transaction::getDate)
                .reduce(LocalDate.now(), (a, b) -> a.isAfter(b) ? a : b);
        LocalDate startDate;
        try {
            startDate = endDate.minusDays(regressionRangeDays);
        } catch (DateTimeException dte) {
            throw new IllegalArgumentException("long \"regressionRangeDays\" cannot be larger than "
                    + ChronoUnit.DAYS.between(LocalDate.MIN, endDate)
                    + "(This limit increases by 1 every day)");
        }

        double increment = user // a = dy / dx
                .getTransactionHistory()
                .getTransactions()
                .stream()
                .filter(transaction -> !transaction.getDate().isBefore(startDate))
                .mapToDouble(transaction -> (double) transaction.getAmount())
                .sum() / regressionRangeDays;
        regressedFunction = (localDate -> increment //f(x) = a(x - x0) + y0
                * (ChronoUnit.DAYS.between(startDate, localDate)
                - regressionRangeDays)
                + user.getCurrentFunds());
    }

    /**
     * Extrapolates an estimated amount of funds at a given date from the regressed graph.
     *
     * @param date The date to extrapolate data for.
     * @return The estimated amount of funds the user has at the given date.
     */
    public double extrapolate(LocalDate date) {
        return regressedFunction.applyAsDouble(date);
    }
}
