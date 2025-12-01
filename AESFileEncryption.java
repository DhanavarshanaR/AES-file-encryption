import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Scanner;

public class AESFileEncryption {

    private static final int KEY_SIZE = 128;   // AES key size in bits (128 = 16 bytes)
    private static final int ITERATIONS = 65536; // PBKDF2 iteration count
    private static final int SALT_LENGTH = 16;   // Salt size in bytes
    private static final int IV_LENGTH = 16;     // AES block size (IV size)

    // Derive AES key from password + salt using PBKDF2
    private static SecretKeySpec deriveKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_SIZE);
        byte[] secret = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(secret, "AES");
    }

    // Encrypt file
    public static void encryptFile(String password, String inputFile, String outputFile) throws Exception {
            SecureRandom random = new SecureRandom();
    
            // Generate random salt
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
    
            // Derive key
            SecretKeySpec key = deriveKey(password, salt);
    
            // Generate random IV
            byte[] iv = new byte[IV_LENGTH];
            random.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
    
            // Setup AES in CBC mode with PKCS5 padding
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
    
            // Write salt + IV at start of file (needed for decryption)
            try (FileOutputStream fos = new FileOutputStream(outputFile);
                 BufferedOutputStream bos = new BufferedOutputStream(fos);
                 CipherOutputStream cos = new CipherOutputStream(bos, cipher);
                 FileInputStream fis = new FileInputStream(inputFile)) {

            bos.write(salt);
            bos.write(iv);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
        }
    }

    // Decrypt file
    public static void decryptFile(String password, String inputFile, String outputFile) throws Exception {
    try (FileInputStream fis = new FileInputStream(inputFile)) {  // FIXED: read encrypted file
        // Read salt + IV from file
        byte[] salt = new byte[SALT_LENGTH];
        fis.read(salt);

        byte[] iv = new byte[IV_LENGTH];
        fis.read(iv);

        SecretKeySpec key = deriveKey(password, salt);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        try (CipherInputStream cis = new CipherInputStream(fis, cipher);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }
}


    // Main
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
    
        try {
            System.out.print("Enter mode (encrypt/decrypt): ");
            String mode = scanner.nextLine();
    
            // Validate mode immediately
            if (!(mode.equalsIgnoreCase("encrypt") || mode.equalsIgnoreCase("decrypt"))) {
                System.out.println(" Unknown mode. Use encrypt or decrypt.");
                return; // Exit program early
            }
    
            System.out.print("Enter input file path: ");
            String inputFile = scanner.nextLine();
    
            System.out.print("Enter output file path: ");
            String outputFile = scanner.nextLine();
    
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
    
            // Only reach here if mode is valid
            if (mode.equalsIgnoreCase("encrypt")) {
                encryptFile(password,inputFile, outputFile);
                System.out.println("File encrypted successfully.");
            } else {
                decryptFile(inputFile, outputFile, password);
                System.out.println("File decrypted successfully.");
            }
    
        } catch (Exception e) {
            // Handles any unexpected errors (wrong password, missing file, etc.)
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}   
