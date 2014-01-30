import org.junit.*;
import static org.junit.Assert.*;
import se.soy.gpg.*;
import java.io.File;
import junitx.framework.FileAssert;

public class EncryptDecryptFile extends SearchInHeap {
  @Test public void encryptDecryptFile() throws GPGException {
    File toEncrypt = new File(System.getProperty("test.resources") + "/test");
    File encryptedTest = new File(System.getProperty("test.resources") + "/EncryptDecryptFile.asc");
    File decryptedTest = new File(System.getProperty("test.resources") + "/EncryptDecryptFile_decrypted");
    GPG.encrypt(toEncrypt).armor().sign().output(encryptedTest);
    GPG.decrypt(encryptedTest).output(decryptedTest);
    FileAssert.assertBinaryEquals(toEncrypt, decryptedTest);
  }
}
