import org.junit.*;
import static org.junit.Assert.*;
import se.soy.gpg.GPG;
import java.io.File;
import java.io.IOException;
import se.soy.securerstring.SecurerString;

public class EncryptDecryptString extends SearchInHeap {
  /* This is a horrible horrible hack, *NEVER* do this in production code! */
  public static String decryptedString = null;
  public static void copyToString(char[] output) {
    String unTrimmedString = new String(output);
    decryptedString = unTrimmedString.trim();
    SecurerString.secureErase(unTrimmedString);
  }

  @Test public void decrypt() throws IOException {
    String toEncrypt = GPG.readFileAsString(System.getProperty("test.resources") + "/test", null);
    GPG.encrypt(toEncrypt).armor().sign().output(new File(System.getProperty("test.resources") + "/EncryptDecryptString.asc"));
    // Re-read it again since .encrypt() secureErase's it.
    toEncrypt = GPG.readFileAsString(System.getProperty("test.resources") + "/test", null);
    GPG.decrypt(new File(System.getProperty("test.resources") + "/EncryptDecryptString.asc"))
      .localUser("0xF870C097").output(EncryptDecryptString.class, "copyToString");
    assertEquals(toEncrypt, decryptedString);
    SecurerString.secureErase(toEncrypt);
    SecurerString.secureErase(decryptedString);
  }
}
