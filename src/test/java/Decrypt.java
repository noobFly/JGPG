import org.junit.*;
import static org.junit.Assert.*;
import se.soy.gpg.GPG;
import java.io.File;
import java.io.PrintStream;
import java.io.OutputStream;

public class Decrypt extends SearchInHeap {
  @Test public void decrypt() {
    // FIXME Disable stdout temporarily since Gradles test output makes the
    // secret stored on the heap.
    PrintStream old_out = System.out;
    System.setOut(new PrintStream(new OutputStream() { public void write(int b) {}}));
    GPG.decrypt(new File(System.getProperty("test.resources") + "/test.asc")).localUser("0xF870C097").output(System.out, "println");
    System.setOut(old_out);
  }
}
