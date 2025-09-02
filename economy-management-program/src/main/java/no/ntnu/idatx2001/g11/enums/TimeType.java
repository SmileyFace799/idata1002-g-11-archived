package no.ntnu.idatx2001.g11.enums;

/**
 * An enum representing a type of time interval.
 * To be used with recurring purchases.
 */
public enum TimeType {
    /** Daily time interval. */
    DAYS("Days"),
    /** Weekly time interval. */
    WEEKS("Weeks"),
    /** Monthly time interval. */
    MONTHS("Months"),
    /** Yearly time interval. */
    YEARS("Years");

    private String label;

    /**
     * Constructor.
     *
     * @param label the visual label of a recurring purchase.
     */
    private TimeType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
