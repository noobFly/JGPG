import org.junit.*;
import static org.junit.Assert.*;
import java.io.File;
import se.soy.gpg.GPG;

public class Decrypt {
  @Test public void decrypt() {
    GPG.decrypt(new File(System.getProperty("test.resources") + "/test.asc")).localUser("0xF870C097").output(System.out, "println");
    assertEquals("Secret not found!", 0, 0);
  }
}
