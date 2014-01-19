import org.junit.*;
import static org.junit.Assert.*;
import se.soy.gpg.GPG;
import java.io.File;

public class EncryptFile extends SearchInHeap {
  @Test public void encryptFile() {
    GPG.encrypt(new File(System.getProperty("test.resources") + "/test")).armor().sign().output(System.out, "println");
  }
}
