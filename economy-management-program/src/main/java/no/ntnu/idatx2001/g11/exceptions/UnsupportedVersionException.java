package no.ntnu.idatx2001.g11.exceptions;

/**
 * Thrown when a save file is loaded from a version no longer supported.
 */
public class UnsupportedVersionException extends IllegalArgumentException {
    /**
     * Constructor.
     *
     * @param version the version that is not supported.
     */
    public UnsupportedVersionException(String version) {
        super("No load function was found for version \"" + version + "\"");
    }

    /**
     * Thrown if any number in the save version is above it's max.
     *
     * @param version The save version.
     * @param maxVersion The max version.
     */
    public UnsupportedVersionException(String version, String maxVersion) {
        super("One or more of the numbers in the save version is above it's max value\n"
                + "Save version: " + version + "\n"
                + "Max version: " + maxVersion);
    }
}
