import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {

    public static byte[] readAllBytes(String plaintextPath) {
        byte[] bytesRead = {};
        try {
            bytesRead = Files.readAllBytes(Paths.get(plaintextPath));
        } catch (Exception e) {}
        return bytesRead; // returns {} if file does not exist
    }

    public static void write(String algo_suffix, String plaintextFileName, byte[] output, String iv_suffix) {
        String outFile = plaintextFileName + "." + algo_suffix + "." + iv_suffix;
        try {
            Files.write(Paths.get(outFile), output);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void write(String algo_suffix, String plaintextFileName, byte[] output) {
        String outFile = plaintextFileName + "." + algo_suffix;
        try {
            Files.write(Paths.get(outFile), output);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void write(String plaintextFileName, byte[] output) {
        String outFile = plaintextFileName;
        try {
            Files.write(Paths.get(outFile), output);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void write(String plaintextFileName, byte[] output, String iv_suffix) {
        String outFile = plaintextFileName + "." + iv_suffix;
        try {
            Files.write(Paths.get(outFile), output);
        } catch (Exception e) { e.printStackTrace(); }
    }
}