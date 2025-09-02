package no.ntnu.idatx2001.g11.usersaves;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import no.ntnu.idatx2001.g11.controllers.ui.ExceptionCommunicator;
import no.ntnu.idatx2001.g11.exceptions.UnsupportedVersionException;
import no.ntnu.idatx2001.g11.generics.TransactionHistory;
import no.ntnu.idatx2001.g11.generics.User;

/**
 * Loads and saves purchase histories.
 *
 * @see TransactionHistory
 */
public class SaveManager {
    /**
     * Current save version.
     */
    public static final String SAVE_VERSION = "1.0.0";

    /**
     * Maximum possible save version.
     */
    public static final String MAX_VERSION = "63.31.31";

    /**
     * Default save path.
     */
    public static final String SAVE_PATH = System.getProperty("user.home")
            + "/economyManagementProgram/";

    /**
     * The default base file name.
     */
    public static final String FILE_NAME_BASE = "save";

    /**
     * The default file extension to be used for save files.
     */
    public static final String FILE_EXTENSION = ".purchases";

    /**
     * The default file extension to be used for backups.
     */
    public static final String BACKUP_EXTENSION = ".bak";

    /**
     * The amount of backups to keep.
     */
    public static final int BACKUP_COUNT = 3;

    // Singletons
    private static ExceptionCommunicator exceptionCommunicator = ExceptionCommunicator.getInstance();

    private static String getFilepath(String filename) {
        return SAVE_PATH + filename;
    }

    /**
     * Gets the path to a save file given a save slot.
     *
     * @param saveSlot slot to be checked
     * @return the file path to the save data associated with the slot
     */
    public static String getFilepath(int saveSlot) {
        return getFilepath(FILE_NAME_BASE + saveSlot + FILE_EXTENSION);
    }

    private static String getBackupFilepath(int saveSlot, int backupNum) {
        return getFilepath(saveSlot) + BACKUP_EXTENSION + backupNum;
    }

    /**
     * Converts a version string of X.Y.Z version into an array of bytes.
     *
     * @param version The version string to convert.
     * @return The version string, as an array of bytes
     */
    public static int[] getVersionNums(String version) {
        return Arrays.stream(version.split("\\.")).mapToInt(Integer::parseInt).toArray();
    }

    /**
     * Attempts to create a new save file for the specified save slot.
     * Will fail to create save file if it already exists, will not overwrite any files.
     *
     * @param saveSlot The save slot to create a save file for.
     */
    private static void createSaveFile(int saveSlot) throws IOException {
        Files.createDirectories(Paths.get(SAVE_PATH));
        File f = new File(getFilepath(saveSlot));
        if (!f.createNewFile()) {
            throw new FileAlreadyExistsException("Save file already exists: " + f.getName());
        }
    }

    /**
     * Removes the version bytes from an array of save data.
     *
     * @param saveBytes The array of bytes to remove the version bytes from.
     * @return The array of bytes, without the version bytes.
     */
    private static byte[] getVersionLessBytes(byte[] saveBytes) {
        byte[] saveBytesVersionLess = new byte[saveBytes.length - 2];
        System.arraycopy(saveBytes, 2, saveBytesVersionLess, 0, saveBytesVersionLess.length);
        return saveBytesVersionLess;
    }

    /**
     * Loads a user from a file.
     * Can load data from any supported save version in {@link VersionTemplate}.
     *
     * @param saveSlot The save slot to load the purchase history for.
     * @return The loaded user. Will be null if the user doesn't exist
     * @throws NoSuchFileException if the save file does not exist
     * @see VersionTemplate
     */
    public static UserBytes loadUserBytes(int saveSlot) throws NoSuchFileException {
        UserBytes user = null;
        try {
            byte[] saveBytes = Files.readAllBytes(new File(getFilepath(saveSlot)).toPath());
            short versionBits = ByteBuffer.wrap(Arrays.copyOfRange(saveBytes, 0, 2)).getShort();
            String[] saveVersionArray = new String[3];
            saveVersionArray[0] = Integer.toString((versionBits & 0b1111110000000000) >> 10);
            saveVersionArray[1] = Integer.toString((versionBits & 0b0000001111100000) >> 5);
            saveVersionArray[2] = Integer.toString(versionBits & 0b0000000000011111);
            user = new UserBytes(
                    String.join(".", saveVersionArray),
                    getVersionLessBytes(saveBytes)
            );

        } catch (NoSuchFileException nsfe) {
            throw nsfe;
        } catch (IOException ioe) {
            exceptionCommunicator.throwErrorDialogue(
                "Failed to load",
                "Failed to load the user's save data.");
        }

        return user;
    }

    /**
     * Stores a user as a series of bytes.<br/>
     * The bytes are stored as follows:<br/>
     * <b>Byte 0:</b> Major save version.<br/>
     * <b>Byte 1, bit 0 - 3:</b> Minor save version.<br/>
     * <b>Byte 1, bit 4 - 7:</b> Patch/debug save version.<br/>
     * <b>Byte 2 - {@code n}:</b> A user, encoded to bytes as documented in {@link User#asBytes()}.
     *
     * @param user The user to save.
     * @param saveSlot The save slot the purchase history if for.
     * @see User#asBytes()
     */
    public static void saveUser(Savable user, int saveSlot) {
        try {
            File oldestBackup = new File(getBackupFilepath(saveSlot, BACKUP_COUNT));
            if (oldestBackup.isFile()) {
                Files.delete(oldestBackup.toPath());
            }
            for (int i = BACKUP_COUNT; i > 1; i--) {
                File backup = new File(getBackupFilepath(saveSlot, i - 1));
                if (backup.isFile()) {
                    Files.move(backup.toPath(), Paths.get(getBackupFilepath(saveSlot, i)));
                }
            }
            Path path = Paths.get(getFilepath(saveSlot));
            if (Files.exists(path)) {
                Files.move(path, Paths.get(getBackupFilepath(saveSlot, 1)));
            }

            createSaveFile(saveSlot);
            try (FileOutputStream outputStream = new FileOutputStream(getFilepath(saveSlot))) {
                int[] versionNums = getVersionNums(SAVE_VERSION);
                int[] maxVersionNums = getVersionNums(MAX_VERSION);
                if (versionNums[0] <= maxVersionNums[0]
                        && versionNums[1] <= maxVersionNums[1]
                        && versionNums[2] <= maxVersionNums[2]) {
                    short versionBits = 0;
                    versionBits = (short) (versionBits | versionNums[2]);
                    versionBits = (short) (versionBits | (versionNums[1] << 5));
                    versionBits = (short) (versionBits | (versionNums[0] << 10));

                    outputStream.write(ByteBuffer.allocate(2).putShort(versionBits).array());
                    outputStream.write(user.asBytes());
                } else {
                    throw new UnsupportedVersionException(SAVE_VERSION, MAX_VERSION);
                }
            }
        } catch (IOException e) {
            exceptionCommunicator.throwErrorDialogue(
                "Error saving user",
                "The program failed to save your data."
            );
        }
    }
}
