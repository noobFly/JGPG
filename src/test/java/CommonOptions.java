import org.junit.*;
import static org.junit.Assert.*;
import se.soy.gpg.GPG;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import junitx.framework.FileAssert;

public class CommonOptions {
  @Test public void localUsers() {
    List<String> recipients = Arrays.asList("F870C097", "7A41522E");
    File toEncrypt = new File(System.getProperty("test.resources") + "/test");
    for (String recipient: recipients) {
      File toDecrypt = new File(System.getProperty("test.resources") + "/test.asc");
      File decryptedTest = new File(System.getProperty("test.resources") + "/CommonOptions-localUsers-" + recipient + ".asc");
      GPG.decrypt(toDecrypt).localUser(recipient).output(decryptedTest);
      FileAssert.assertBinaryEquals(decryptedTest, toEncrypt);
    }
  }
}
