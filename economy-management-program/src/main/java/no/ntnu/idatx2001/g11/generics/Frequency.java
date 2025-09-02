package no.ntnu.idatx2001.g11.generics;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import no.ntnu.idatx2001.g11.enums.TimeType;
import no.ntnu.idatx2001.g11.usersaves.Savable;

/**
 * Used to represent a recurring transaction.
 */
public class Frequency implements Savable {
    private final short amount;
    private final TimeType timeType;

    /**
     * Makes a frequency.
     *
     * @param amount How many units of time between each recurrence
     * @param timeType The type of time units the amount is measured in
     */
    public Frequency(short amount, TimeType timeType) {
        if (amount <= 0) {
            throw new IllegalArgumentException("int \"amount\" must be greater than 0");
        }
        if (timeType == null) {
            throw new IllegalArgumentException("The time type cannot be null");
        }
        this.amount = amount;
        this.timeType = timeType;
    }

    /**
     * Constructor.
     *
     * @param amount the interval of the frequency
     * @param timeType the time type of the interval
     */
    public Frequency(int amount, TimeType timeType) {
        this((short) amount, timeType);
    }

    /**
     * Gets the interval of the frequency.
     *
     * @return the interval of the frequency
     */
    public short getAmount() {
        return amount;
    }

    /**
     * Gets the time type of the interval.
     *
     * @return the time type of the interval
     */
    public TimeType getTimeType() {
        return timeType;
    }

    /**
     * Converts the frequency into an array of bytes.<br/><br/>
     * The bytes are stored as follows:<br/>
     * <ul>
     *     <li><b>Byte 0 - 1:</b> The frequency's amount, as a short.</li>
     *     <li><b>Byte 2:</b> The length of the frequency type.</li>
     *     <li><b>Byte 3 - {@code n}:</b> The frequency type, encoded to bytes using UTF-8.</li>
     * </ul>
     *
     * @return The frequency, converted to an array of bytes.
     */
    @Override
    public byte[] asBytes() {
        byte[] timeTypeBytes = timeType.toString().getBytes(StandardCharsets.UTF_8);

        byte[] byteArray = new byte[byteLength()];
        byte[] amountBytes = ByteBuffer.allocate(2).putShort(amount).array();
        System.arraycopy(amountBytes, 0, byteArray, 0, amountBytes.length);
        byteArray[2] = (byte) timeTypeBytes.length;
        System.arraycopy(timeTypeBytes, 0, byteArray, 3, timeTypeBytes.length);
        return byteArray;
    }

    @Override
    public int byteLength() {
        return 3 + timeType.toString().getBytes(StandardCharsets.UTF_8).length;
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
        Frequency frequency = (Frequency) obj;
        return amount == frequency.getAmount()
                && timeType.equals(frequency.getTimeType());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Integer.hashCode(amount);
        hash = 31 * hash + timeType.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "Amount\n: " + getAmount()
                + "\nTime type: " + getTimeType();
    }
}
