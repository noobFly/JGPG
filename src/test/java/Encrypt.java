import org.junit.*;
import static org.junit.Assert.*;
import se.soy.gpg.GPG;

public class Encrypt extends SearchInHeap {
  @Test public void encrypt() {
    GPG.encrypt("really_secret_string").armor().sign().output(System.out, "println");
  }

  void theAssert(int found) {
      assertEquals("Secret not found!", 0, found);
  }
}
