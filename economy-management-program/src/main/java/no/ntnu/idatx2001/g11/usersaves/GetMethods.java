package no.ntnu.idatx2001.g11.usersaves;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import no.ntnu.idatx2001.g11.enums.TimeType;
import no.ntnu.idatx2001.g11.exceptions.UnsupportedVersionException;
import no.ntnu.idatx2001.g11.generics.Frequency;
import no.ntnu.idatx2001.g11.generics.Transaction;
import no.ntnu.idatx2001.g11.generics.TransactionHistory;
import no.ntnu.idatx2001.g11.generics.User;

/**
 * Stores the corresponding load methods for every supported save version.
 * <br/><br/>
 * Currently supported save versions are:
 * <ul>
 *     <li>1.0.1</li>
 *     <li>1.0.0</li>
 * </ul>
 */
public class GetMethods {
    /**
     * Loads user save data.
     * Interprets and returns loaded save data in accordance to the specified save version.
     *
     * @param saveBytes The bytes of save data to load the user from.
     * @param version The save version of the save data.
     * @return The loaded user.
     *         Will be {@code null} if an unsupported version is specified.
     */
    public static User getUser(byte[] saveBytes, String version) {
        User user;
        switch (version) {
            case "1.0.1":
                user = getUser1Dot0Dot1(saveBytes);
                break;
            case "1.0.0":
                user = getUser1Dot0Dot0(saveBytes);
                break;
            default:
                throw new UnsupportedVersionException(version);
        }
        return user;
    }

    /**
     * Identical to version 1.0.0, only exists as a way to test different versions.
     *
     * @param saveBytes The bytes of save data to get the purchase history from.
     * @return The loaded purchase history.
     */
    private static User getUser1Dot0Dot1(byte[] saveBytes) {
        return getUser1Dot0Dot0(saveBytes);
    }

    /**
     * Loads the purchase history from an array of bytes.
     * The purchase history must be of save version 1.0.0.
     *
     * @param saveBytes The bytes of save data to get the purchase history from.
     * @return The loaded purchase history.
     */
    private static User getUser1Dot0Dot0(byte[] saveBytes) {
        int i = 9 + saveBytes[8]; //Assigns i at the first byte where the transaction history starts.
        User user = new User(
                new String(Arrays.copyOfRange(saveBytes, 9, i), StandardCharsets.UTF_8),
                ByteBuffer.wrap(Arrays.copyOfRange(saveBytes, 0, 8)).getDouble()
        );
        TransactionHistory transactionHistory = user.getTransactionHistory();
        while (i < saveBytes.length) {
            int transactionLength =
                    11 + saveBytes[i + 8] + saveBytes[i + 9] + saveBytes[i + 10];
            byte[] transactionBytes = Arrays.copyOfRange(
                    saveBytes,
                    i,
                    i + transactionLength
            );
            i += transactionLength;

            Transaction transaction = new Transaction(
                    new String(
                            Arrays.copyOfRange(
                                    transactionBytes,
                                    11 + transactionBytes[8],
                                    11 + transactionBytes[8] + transactionBytes[9]
                            ),
                            StandardCharsets.UTF_8
                    ),
                    new String(
                            Arrays.copyOfRange(transactionBytes, 11, 11 + transactionBytes[8]),
                            StandardCharsets.UTF_8
                    ),
                    ByteBuffer.wrap(Arrays.copyOfRange(transactionBytes, 4, 8)).getFloat(),
                    LocalDate.of(
                            ByteBuffer
                                    .wrap(Arrays.copyOfRange(transactionBytes, 0, 2))
                                    .getShort(),
                            transactionBytes[2],
                            transactionBytes[3]
                    )
            );
            if (transactionBytes[10] != 0) {
                byte[] freuencyBytes = Arrays.copyOfRange(
                        transactionBytes,
                        11 + transactionBytes[8] + transactionBytes[9],
                        11 + transactionBytes[8] + transactionBytes[9] + transactionBytes[10]
                );
                transaction.setFrequency(new Frequency(
                            ByteBuffer.wrap(Arrays.copyOfRange(freuencyBytes, 0, 2)).getShort(),
                            TimeType.valueOf(new String(
                                    Arrays.copyOfRange(freuencyBytes, 3, 3 + freuencyBytes[2])
                            )))
                );
            }
            transactionHistory.addTransaction(transaction);
        }

        return user;
    }
}
