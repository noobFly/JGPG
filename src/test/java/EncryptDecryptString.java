import org.junit.*;
import static org.junit.Assert.*;
import se.soy.gpg.GPG;
import java.io.File;
import java.io.IOException;
import se.soy.securerstring.SecurerString;

public class EncryptDecryptString extends SearchInHeap {
  /* This is a horrible horrible hack, *NEVER* do this in production code! */
  public static String encryptedString = null;
  public static String decryptedString = null;

  public static void encryptToString(char[] output) {
    String unTrimmedString = new String(output);
    encryptedString = unTrimmedString.trim();
    SecurerString.secureErase(unTrimmedString);
  }

  public static void decryptToString(char[] output) {
    String unTrimmedString = new String(output);
    decryptedString = unTrimmedString.trim();
    SecurerString.secureErase(unTrimmedString);
  }

  @Test public void encryptDecryptString() throws IOException {
    String toEncrypt = GPG.readFileAsString(System.getProperty("test.resources") + "/test", null);
    GPG.encrypt(toEncrypt).armor().sign().output(EncryptDecryptString.class, "encryptToString");
    toEncrypt = GPG.readFileAsString(System.getProperty("test.resources") + "/test", null);
    GPG.decrypt(encryptedString).output(EncryptDecryptString.class, "decryptToString");
    assertEquals(toEncrypt, decryptedString);
    SecurerString.secureErase(toEncrypt);
    SecurerString.secureErase(encryptedString);
    SecurerString.secureErase(decryptedString);
  }
}
