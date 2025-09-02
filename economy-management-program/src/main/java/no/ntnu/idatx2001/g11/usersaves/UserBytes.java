package no.ntnu.idatx2001.g11.usersaves;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import no.ntnu.idatx2001.g11.enums.TimeType;
import no.ntnu.idatx2001.g11.generics.Frequency;
import no.ntnu.idatx2001.g11.generics.Transaction;
import no.ntnu.idatx2001.g11.generics.User;

/**
 * A byte map that stores all the bytes required to make a user.
 */
public class UserBytes {
    private final Map<String, byte[]> byteMap;

    /**
     * Constructor.
     *
     * @param version version of the save file
     * @param saveBytes bytes of the save file
     */
    public UserBytes(String version, byte[] saveBytes) {
        byteMap = SaveDataReader.getLatestByteMap(version, saveBytes);
    }

    /**
     * Gets a byte from the user bytes.
     *
     * @param key          The key associated with the byte.
     * @param defaultValue The default return value if the byte is not found.
     * @return The byte found at the specified key, or the default value if it wasn't found.
     */
    public byte getByte(String key, byte defaultValue) {
        byte returnValue = defaultValue;
        if (byteMap.containsKey(key)) {
            byte[] bytes = byteMap.get(key);
            if (bytes.length != 1) {
                throw new IllegalStateException(
                        "Data value is not 1 bytes in length, cannot get byte");
            }
            returnValue = bytes[0];
        }
        return returnValue;
    }

    /**
     * Gets a short from the user bytes.
     *
     * @param key          The key associated with the short.
     * @param defaultValue The default return value if the short is not found.
     * @return The short found at the specified key, or the default value if it wasn't found.
     */
    public short getShort(String key, short defaultValue) {
        short returnValue = defaultValue;
        if (byteMap.containsKey(key)) {
            byte[] bytes = byteMap.get(key);
            if (bytes.length != 2) {
                throw new IllegalStateException(
                        "Data value is not 2 bytes in length, cannot get short");
            }
            returnValue = ByteBuffer.wrap(bytes).getShort();
        }
        return returnValue;
    }

    //No "int"s are stored, getInt(...) not needed

    //No "long"s are stored, getLong(...) not needed

    /**
     * Gets a float from the user bytes.
     *
     * @param key          The key associated with the float.
     * @param defaultValue The default return value if the float is not found.
     * @return The float found at the specified key, or the default value if it wasn't found.
     */
    public float getFloat(String key, float defaultValue) {
        float returnValue = defaultValue;
        if (byteMap.containsKey(key)) {
            byte[] bytes = byteMap.get(key);
            if (bytes.length != 4) {
                throw new IllegalStateException(
                        "Data value is not 4 bytes in length, cannot get float");
            }
            returnValue = ByteBuffer.wrap(bytes).getFloat();
        }
        return returnValue;
    }

    /**
     * Gets a double from the user bytes.
     *
     * @param key          The key associated with the double.
     * @param defaultValue The default return value if the double is not found.
     * @return The double found at the specified key, or the default value if it wasn't found.
     */
    public double getDouble(String key, double defaultValue) {
        double returnValue = defaultValue;
        if (byteMap.containsKey(key)) {
            byte[] bytes = byteMap.get(key);
            if (bytes.length != 8) {
                throw new IllegalStateException(
                        "Data value is not 8 bytes in length, cannot get double");
            }
            returnValue = ByteBuffer.wrap(bytes).getDouble();
        }
        return returnValue;
    }

    /**
     * Gets a string from the user bytes.
     *
     * @param key          The key associated with the string.
     * @param defaultValue The default return value if the string is not found.
     * @return The string found at the specified key, or the default value if it wasn't found.
     */
    public String getString(String key, String defaultValue) {
        String returnValue = defaultValue;
        if (byteMap.containsKey(key)) {
            byte[] bytes = byteMap.get(key);
            returnValue = new String(bytes, StandardCharsets.UTF_8);
        }
        return returnValue;
    }

    /**
     * Gets an enum value from the user bytes.
     *
     * @param enumValues   The values of the enum class.
     * @param key          The key associated with the enum value.
     * @param defaultValue The default return value if the enum value is not found.
     * @param <E>          The enum passed to this function.
     * @return The enum value found at the specific key, or the default value if it wasn't found.
     */
    public <E extends Enum<E>> E getEnum(E[] enumValues, String key, E defaultValue) {
        String enumString = getString(key, null);
        E returnValue = defaultValue;
        if (enumString != null) {
            Optional<E> optionalEnum = Arrays
                    .stream(enumValues)
                    .filter(value -> value.toString().equalsIgnoreCase(enumString))
                    .findFirst();
            if (optionalEnum.isPresent()) {
                returnValue = optionalEnum.get();
            }
        }
        return returnValue;
    }

    /**
     * Makes a user from the user bytes.
     *
     * @return The user made.
     */
    public User makeUser() {
        User user = new User(
                getString("username", "(Unknown username)"),
                getDouble("startingFunds", 0)
        );
        int transactionIndex = 0;
        boolean hasMoreTransactions = byteMap
                .keySet()
                .stream()
                .anyMatch(key -> key.startsWith("transaction"));
        while (hasMoreTransactions) {
            int i = transactionIndex;
            Frequency frequency = null;
            if (byteMap
                    .keySet()
                    .stream()
                    .anyMatch(key -> key.startsWith("frequency")
                            && key.endsWith("|" + i))) {
                frequency = new Frequency(
                        getShort("frequencyAmount|" + i, (short) 1),
                        getEnum(TimeType.values(), "frequencyType|" + i, TimeType.MONTHS)
                );
            }
            user.getTransactionHistory().addTransaction(new Transaction(
                    getString("transactionName|" + i, "(No name)"),
                    getString("transactionCategory|" + i, "(No category"),
                    getFloat("transactionAmount|" + i, 0),
                    LocalDate.of(
                            getShort("transactionYear|" + i, (short) 1970),
                            getByte("transactionMonth|" + i, (byte) 1),
                            getByte("transactionDay|" + i, (byte) 1)
                    ),
                    frequency
            ));

            hasMoreTransactions = byteMap
                    .keySet()
                    .stream()
                    .anyMatch(key -> key.startsWith("transaction")
                            && key.endsWith("|" + (i + 1)));
            transactionIndex++;
        }
        return user;
    }
}
