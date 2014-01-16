package se.soy.gpg;

import java.util.List;
import java.util.ArrayList;

public class GPG {
  // FIXME Remove when done
  static<T> void println(T arg) { System.out.println(arg); }

  /*
  FIXME: Add as tests
  GPG.decrypt(java.io.File).output();
  GPG.decrypt("string").output(System.out, "println");
  GPG.decrypt("string").output(java.io.File);
  */

  private final List<String> pre_command = new ArrayList<String>(Arrays.asList(
    "gpg",
    "--default-recipient-self",
    "--no-tty",
    "--batch",
    "--yes"
    )
  );
  private List<String> command = new ArrayList<String>();
  private static GPG gpg = null;
  private String data;

  public void output() {
    println("Command: " + pre_command + command);
    println("----------");
    gpg = null;
  }

  public <T> void output(T file) {
    println("Outputing to: " + file);
    output();
  }

  public static GPG encrypt(String data) {
    gpg = (null == gpg) ? new GPG() : gpg;
    gpg.command.add("--encrypt");
    // FIXME secureErase()
    gpg.data = data;
    return gpg;
  }

  public static GPG decrypt(File file) {
    gpg = (null == gpg) ? new GPG() : gpg;
    gpg.command.add("--decrypt");
    // FIXME secureErase()
    try {
      gpg.data = readFileAsString(file.toString(), null);
    }
    catch (IOException e) {
      gpg.data = null;
    }
    return gpg;
  }

  // http://ptspts.blogspot.com/2009/11/how-to-read-whole-file-to-string-in.html
  public static String readFileAsString(String fileName, String charsetName)
    throws java.io.IOException {
    java.io.InputStream is = new java.io.FileInputStream(fileName);
    try {
      final int bufsize = 4096;
      int available = is.available();
      byte data[] = new byte[available < bufsize ? bufsize : available];
      int used = 0;
      while (true) {
        if (data.length - used < bufsize) {
          byte newData[] = new byte[data.length << 1];
          System.arraycopy(data, 0, newData, 0, used);
          data = newData;
        }
        int got = is.read(data, used, data.length - used);
        if (got <= 0) break;
        used += got;
      }
      return charsetName != null ? new String(data, 0, used, charsetName)
        : new String(data, 0, used);
    } finally {
      is.close();
    }
  }


  public static GPG decrypt(String data) {
    gpg = (null == gpg) ? new GPG() : gpg;
    gpg.command.add("--decrypt");
    // FIXME secureErase()
    gpg.data = data;
    return gpg;
  }

  public static GPG sign() {
    gpg = (null == gpg) ? new GPG() : gpg;
    gpg.command.add("--sign");
    return gpg;
  }

  public GPG armor() {
    command.add("--armor");
    return this;
  }

  // TODO: Add recipients(List<String> recipients)
  public GPG recipient(String recipient) {
    command.add("--recipient");
    command.add(recipient);
    return this;
  }

  public GPG localUser(String localUser) {
    command.add("--local-user");
    command.add(localUser);
    return this;
  }

  public GPG home(String home) {
    command.add("--home");
    command.add(home);
    return this;
  }
}
