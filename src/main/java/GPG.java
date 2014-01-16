package se.soy.gpg;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

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

    try {
      List<String> commandline = new ArrayList<String>(pre_command);
      commandline.addAll(command);

      ProcessBuilder pb = new ProcessBuilder(commandline);

      Process p = pb.start();
      p.getOutputStream().write(gpg.data.getBytes());
      p.getOutputStream().flush();
      p.getOutputStream().close();

      String line = null;
      int code = p.waitFor();
      if (code != 0) {
        String exception = String.format("Exit status: %d", code);

        BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        while ((line = stderr.readLine ()) != null) {
          exception += "\n" + line;
        }
        stderr.close();
        throw new GPGException(exception);
      }

      /* Reader stdout = new InputStreamReader(p.getInputStream()); */
      BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
      /* char buf[] = new char[1024]; */
      /* char meow[] = new char[expectedSize]; */
      /* int read_now = 0; */
      /* int read_until_now = 0; */
      /* try (SecurerString decrypted = new SecurerString(meow)) { */
      /*   try (SecurerString ss_buf = new SecurerString(buf)) { */
        /*   while ((read_now = stdout.read(ss_buf.chars, 0, 1024)) != -1) { */
        /*     System.arraycopy(ss_buf.chars, 0, decrypted.chars, read_until_now, read_now); */
        /*     read_until_now += read_now; */
        /*   } */
        /* } */
        /* System.out.println(read_until_now); */
      while ((line = stdout.readLine ()) != null) {
        System.out.println(line);
      }
      stdout.close();

      /*   // http://stackoverflow.com/a/5428621 */
      /*   Class<?> c; */
      /*   Method method; */
      /*   try { */
      /*     try { */
      /*       c = (Class<?>)className; */
      /*     } */
      /*     catch (ClassCastException e) { */
      /*       c = className.getClass (); */
      /*     } */

      /*     System.out.println("invoking " + c.getSimpleName () + "::[" + methodName + "]..."); */
      /*     method = c.getDeclaredMethod(methodName, decrypted.chars.getClass()); */
      /*     method.invoke(className, decrypted.chars); */
      /*   } */
      /*   // FIXME Which exceptions? */
      /*   catch (Exception e) { */
      /*     e.printStackTrace(); */
      /*   } */
      /* } */

    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    finally {
      gpg = null;
    }
  /* } */

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
