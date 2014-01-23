import org.junit.*;
import static org.junit.Assert.*;
import se.soy.gpg.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import junitx.framework.FileAssert;

public class CommonOptions {
  private List<String> recipients = Arrays.asList("F870C097", "7A41522E");
  private File toEncrypt = new File(System.getProperty("test.resources") + "/test");

  @Test public void localUsers() {
    for (String recipient: recipients) {
      File toDecrypt = new File(System.getProperty("test.resources") + "/test.asc");
      File decryptedTest = new File(System.getProperty("test.resources") + "/CommonOptions-localUsers-" + recipient);
      GPG.decrypt(toDecrypt).localUser(recipient).output(decryptedTest);
      FileAssert.assertBinaryEquals(decryptedTest, toEncrypt);
    }
  }

  @Test public void multipleRecipients() {
    File encryptedTest = new File(System.getProperty("test.resources") + "/multipleRecipients.asc");
    GPG.encrypt(toEncrypt).armor().recipient(recipients).output(encryptedTest);
    for (String recipient: recipients) {
      File decryptedTest = new File(System.getProperty("test.resources") + "/multipleRecipients-" + recipient);
      GPG.decrypt(encryptedTest).localUser(recipient).output(decryptedTest);
      FileAssert.assertBinaryEquals(decryptedTest, toEncrypt);
    }
  }

  @Test(expected=GPGException.class) public void generateGPGException() {
    File toDecrypt = new File(System.getProperty("test.resources") + "/generateGPGException");
    try {
      toDecrypt.createNewFile();
    }
    catch (Exception e) {}
    GPG.decrypt(toDecrypt).output(System.out, "println");
  }
}
