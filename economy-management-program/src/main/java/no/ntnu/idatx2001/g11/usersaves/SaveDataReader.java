package no.ntnu.idatx2001.g11.usersaves;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 *     Reads save data with version control, and converts the data to the current version.
 *     The data will be ordered,
 *     but it might not be in the same order as the current version template.
 * </p><p>
 *     Data is interpreted using a {@link VersionTemplate}.
 * </p>
 *
 * @see VersionTemplate
 * @see VersionTemplate#CURRENT_VERSION
 */
public class SaveDataReader {
    private SaveDataReader() {
        throw new IllegalStateException("Utility class");
    }

    private static int index;

    private static <T> Map<String, T> addStringToKeys(Map<String, T> map, String stringToAdd) {
        return map
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey() + stringToAdd,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    /**
     * <p>
     *     Gets an empty byte map of a specified object type.
     *     "Empty" in this context means a map where all the values are 0-length byte arrays.
     * </p><p>
     *     This byte map will use template version {@link VersionTemplate#CURRENT_VERSION}.
     * </p>
     *
     * @param type The object type to get the byte map for
     * @return An empty byte map
     */
    private static Map<String, byte[]> getEmptyByteMapOfType(String type) {
        Map<String, byte[]> byteMap = new LinkedHashMap<>();
        Map<String, Integer> template = VersionTemplate.get(type, VersionTemplate.CURRENT_VERSION);
        for (Map.Entry<String, Integer> entry : template.entrySet()) {
            if (entry.getValue() >= -1) {
                byteMap.put(entry.getKey(), new byte[0]); //streams don't support null values
            } else if (entry.getValue() == -2) {
                byteMap.putAll(getEmptyByteMapOfType("TRANSACTION_HISTORY"));
            } else if (entry.getValue() == -3) {
                byteMap.putAll(addStringToKeys(
                        getEmptyByteMapOfType("TRANSACTION"),
                        "!transactionIndex")
                );
            } else if (entry.getValue() == -4) {
                byteMap.putAll(getEmptyByteMapOfType("FREQUENCY"));
            }
        }
        return byteMap;
    }

    /**
     * Puts save data into a byte map of a specified object type.
     *
     * @param type The object type to get the byte map for
     * @param saveVersion The version of the save data.
     *                    The returned byte map will also be of this version
     * @param saveBytes The save data
     * @return A loaded byte map for the specified version
     *
     * @see VersionTemplate
     */
    private static Map<String, byte[]> getVersionByteMapOfType(
            String type, String saveVersion, byte[] saveBytes) {
        Map<String, byte[]> byteMap = new LinkedHashMap<>();
        Map<String, Integer> template = VersionTemplate.get(type, saveVersion);
        for (Map.Entry<String, Integer> entry : template.entrySet()) {
            if (entry.getValue() >= 0) {
                int dataLength = entry.getValue();
                byteMap.put(entry.getKey(),
                        Arrays.copyOfRange(saveBytes, index, index + dataLength));
                index += dataLength;
            } else if (entry.getValue() == -1) {
                int dataLength = saveBytes[index];
                index++;
                byteMap.put(entry.getKey(),
                        Arrays.copyOfRange(saveBytes, index, index + dataLength));
                index += dataLength;
            } else if (entry.getValue() == -2) {
                byteMap.putAll(getVersionByteMapOfType(
                        "TRANSACTION_HISTORY", saveVersion, saveBytes));
            } else if (entry.getValue() == -3) {
                int repeatAmount = ByteBuffer
                        .wrap(Arrays.copyOfRange(saveBytes, index, index + 2))
                        .getShort();
                index += 2;
                for (int i = 0; i < repeatAmount; i++) {
                    byteMap.putAll(addStringToKeys(
                            getVersionByteMapOfType("TRANSACTION", saveVersion, saveBytes),
                            "|transactionIndex&" + i
                    ));
                }
            } else if (entry.getValue() == -4) {
                index++;
                if (saveBytes[index - 1] != 0) {
                    byteMap.putAll(getVersionByteMapOfType(
                            "FREQUENCY", saveVersion, saveBytes));
                }
            }
        }
        return byteMap;
    }

    /**
     * <p>
     *     Gets an empty byte map of type {@link VersionTemplate#TOP_LEVEL}.
     * </p><p>
     *     This byte map will use the template of version {@link VersionTemplate#CURRENT_VERSION}.
     * </p>
     *
     * @return An empty byte map of the top level type
     */
    private static Map<String, byte[]> getEmptyByteMap() {
        return getEmptyByteMapOfType(VersionTemplate.TOP_LEVEL);
    }

    /**
     * Puts save data into a byte map of type {@link VersionTemplate#TOP_LEVEL}.
     *
     * @param saveVersion The version of the save data.
     *                    The returned byte map will also be of this version
     * @param saveBytes The save data
     * @return A loaded byte map of the top level type. for the specified version
     */
    private static Map<String, byte[]> getVersionByteMap(String saveVersion, byte[] saveBytes) {
        index = 0;
        return getVersionByteMapOfType(
                VersionTemplate.TOP_LEVEL, saveVersion, saveBytes);
    }

    /**
     * <p>
     *     Puts save data into a byte map of type {@link VersionTemplate#TOP_LEVEL}.
     * </p><p>
     *     This byte map will use the template of version {@link VersionTemplate#CURRENT_VERSION}.
     *     Save data of older versions will be converted to the current version.
     * </p>
     *
     * @param saveVersion The version of the save data
     * @param saveBytes The save data
     * @return A loaded byte map of the top level type
     */
    public static Map<String, byte[]> getLatestByteMap(String saveVersion, byte[] saveBytes) {
        Map<String, byte[]> versionByteMap = getVersionByteMap(saveVersion, saveBytes);
        Map<String, byte[]> latestByteMap = getEmptyByteMap();

        //If multiple keys with the same name are missing some key indexes,
        //Keys indexes are created, so they don't overwrite each other.
        Map<String, Integer> missingKeyIndexes = new HashMap<>();

        for (Map.Entry<String, byte[]> entry : versionByteMap.entrySet()) {
            String[] keySplit = entry.getKey().split("\\|");
            String keyName = keySplit[0];
            String[] keyIndexes = Arrays.copyOfRange(keySplit, 1, keySplit.length);

            Optional<String> latestKeyOptional = latestByteMap
                    .keySet()
                    .stream()
                    .filter(key -> key.startsWith(keyName))
                    .findFirst();
            String latestKey = latestKeyOptional.orElse("");
            if (latestKey.equals(keyName)) {
                latestByteMap.put(latestKey, entry.getValue());
            } else if (!latestKey.isBlank()) {
                String[] latestKeySplit = latestKey.split("!");
                String latestKeyName = latestKeySplit[0];
                String[] latestKeyIndexes = Arrays
                        .copyOfRange(latestKeySplit, 1, latestKeySplit.length);

                for (int i = 0; i < latestKeyIndexes.length; i++) {
                    String latestKeyIndex = latestKeyIndexes[i];
                    Optional<String> keyIndexOptional = Arrays.stream(keyIndexes)
                            .filter(ki -> ki.startsWith(latestKeyIndex))
                            .findFirst();
                    if (keyIndexOptional.isPresent()) {
                        latestKeyIndexes[i] = keyIndexOptional.get().split("&")[1];
                        //CheckStyle complains about cognitive complexity if I put the
                        //below code into an "else"-block
                        continue;
                    }
                    String precedingText = latestKeyName + String.join("|",
                            Arrays.copyOfRange(latestKeyIndexes, 0, i));
                    if (!missingKeyIndexes.containsKey(precedingText)) {
                        missingKeyIndexes.put(precedingText, 0);
                    }
                    int missingKeyIndex = missingKeyIndexes.get(precedingText);
                    latestKeyIndexes[i] = Integer.toString(missingKeyIndex);
                    missingKeyIndexes.put(precedingText, missingKeyIndex + 1);
                }
                latestByteMap.put(
                        latestKeyName + "|" + String.join("|", latestKeyIndexes),
                        entry.getValue()
                );
            }
        }
        List<String> latestKeysToRemove = latestByteMap
                .keySet()
                .stream()
                .filter(key -> key.contains("!"))
                .toList();
        for (String keyToRemove : latestKeysToRemove) {
            latestByteMap.remove(keyToRemove);
        }
        return latestByteMap;
    }
}
