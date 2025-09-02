package no.ntnu.idatx2001.g11.usersaves;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * <p>
 *     Stores version templates on how objects are supposed to be loaded for different versions.
 *     Each template contains a key string and an integer value,
 *     where the key is a reference keyword that describes what data the entry refers to,
 *     while the value is an instruction on how to read this data from a save file.
 *     The keys and values are ordered.</p>
 *
 * <p>The instruction integer can refer to the following actions:
 *     <ul>
 *         <li><b>n:</b> For any natural number (int >= 0), read that many bytes forwards</li>
 *         <li><b>-1:</b> The next byte is the length, followed by the bytes to read</li>
 *         <li><b>-2:</b> The transaction history template should be applied</li>
 *         <li><b>-3:</b> The next two bytes is how many times the transaction template
 *         should be applied, followed by the bytes to apply the template to</li>
 *         <li><b>-4:</b> If the next byte is not 0, the frequency template should be applied</li>
 *     </ul>
 */
public enum VersionTemplate {
    //Map.of() doesn't work; doesn't keep insertion order.

    //NB: Make sure all versions are in ascending order, with "CURRENT" at the bottom.
    //Enums care about insertion order,
    //and this allows newer versions to re-use templates of older versions.

    //NB: Don't remove any "unused" methods here,
    //they are used through VersionTemplate.valueOf(String)

    /**
     * Save template for users.
     */
    USER_1_0_0(List.of(
            new AbstractMap.SimpleEntry<>("startingFunds", 8),
            new AbstractMap.SimpleEntry<>("username", -1),
            new AbstractMap.SimpleEntry<>("transactionHistory", -2)
    )),

    /**
     * Save template for transaction history.
     */
    TRANSACTION_HISTORY_1_0_0(List.of(
            new AbstractMap.SimpleEntry<>("transaction", -3)
    )),

    /**
     * Save template for individual transactions.
     */
    TRANSACTION_1_0_0(List.of(
            new AbstractMap.SimpleEntry<>("transactionYear", 2),
            new AbstractMap.SimpleEntry<>("transactionMonth", 1),
            new AbstractMap.SimpleEntry<>("transactionDay", 1),
            new AbstractMap.SimpleEntry<>("transactionAmount", 4),
            new AbstractMap.SimpleEntry<>("transactionCategory", -1),
            new AbstractMap.SimpleEntry<>("transactionName", -1),
            new AbstractMap.SimpleEntry<>("transactionFrequency", -4)
    )),

    /**
     * Save template for frequency objects.
     */
    FREQUENCY_1_0_0(List.of(
            new AbstractMap.SimpleEntry<>("frequencyAmount", 2),
            new AbstractMap.SimpleEntry<>("frequencyType", -1)
    ));

    /**
     * The top level template.
     */
    public static final String TOP_LEVEL = "USER";
    
    /**
     * Current save version.
     */
    public static final String CURRENT_VERSION = "1.0.0";
    private final Map<String, Integer> template;

    VersionTemplate(List<Map.Entry<String, Integer>> template) {
        this.template = template
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    //Exists so newer templates can reference older by passing
    //"OLD_VERSION.getTemplate()" in their constructor.
    VersionTemplate(Map<String, Integer> template) {
        this.template = template;
    }

    private Map<String, Integer> getTemplate() {
        return template;
    }

    /**
     * Gets a template for the specified type and version.
     *
     * @param type The type of the template to get
     * @param version The version of the template to get
     * @return The template found of the specified type and version
     * @throws IllegalArgumentException If {@code type} or {@code version} is null or blank
     * @throws NoSuchElementException if no template is found of the specified type and version
     */
    public static Map<String, Integer> get(String type, String version) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("String \"type\" cannot be null or blank");
        }
        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("String \"version\" cannot be null or blank");
        }
        String name = type + "_" + version.replace(".", "_");
        VersionTemplate value;
        try {
            value = valueOf(name);
        } catch (IllegalArgumentException iae) {
            throw new NoSuchElementException("No template found of type \"" + type
                    + "\" & version \"" + version + "\"");
        }
        return value.getTemplate();
    }
}
