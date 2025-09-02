package no.ntnu.idatx2001.g11.usersaves;

/**
 * Interface for any items that can be saved to a file.
 */
public interface Savable {
    /**
     * Gets this object as bytes that can be saved.
     *
     * @return this object as bytes that can be saved
     */
    byte[] asBytes();

    /**
     * Gets the length of this object's byte form.
     *
     * @return the length of this object's byte form.
     */
    int byteLength();
}
