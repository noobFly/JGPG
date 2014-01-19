import org.junit.*;
import static org.junit.Assert.*;
import se.soy.gpg.GPG;
import java.io.File;

public class EncryptToFile extends SearchInHeap {
  @Test public void encryptFile() {
    GPG.encrypt("really_secret_string").armor().sign().output(new File(System.getProperty("temporaryDir") + "/test"));
  }
}
