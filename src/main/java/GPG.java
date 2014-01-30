package se.soy.gpg;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
import se.soy.securerstring.SecurerString;
import java.lang.reflect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Pattern;

public class GPG {
  public static final Logger log = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass().getSimpleName());

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
  private char[] buf;
  private File file;
  private boolean checkVerification = false;
  private boolean verified = false;
  private String signedBy = null;

  private void pre_output() throws GPGException {
    if (null != file) {
      command.add(file.getAbsolutePath());
    }

    log.debug("Command: " + pre_command + command);

    try {
      List<String> commandline = new ArrayList<String>(pre_command);
      commandline.addAll(command);

      ProcessBuilder pb = new ProcessBuilder(commandline);

      Process p = pb.start();
      if (null != gpg.data) {
        p.getOutputStream().write(gpg.data.getBytes());
        p.getOutputStream().flush();
      }
      p.getOutputStream().close();

      String line = null;
      String stderr_out = null;
      BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
      String signatureRegex = "^gpg: Signature made .* key ID (.*)$";
      String nameEmailRegex = "^gpg: Good signature from \"(.*)\"$";
      while ((line = stderr.readLine ()) != null) {
        if (Pattern.matches(signatureRegex, line)) {
          signedBy = "0x" + line.replaceAll(signatureRegex, "$1");
        }
        else if (Pattern.matches(nameEmailRegex, line)) {
          signedBy = line.replaceAll(nameEmailRegex, "$1") + " " + signedBy;
          verified = true;
        }
        stderr_out += "\n" + line;
      }
      if (checkVerification) {
        log.debug("signedBy: " + signedBy);
        log.debug("verified?: " + verified);
        if (!verified) {
          throw new GPGException("Verification requested but FAILED:\n" + signedBy);
        }
      }
      stderr.close();

      int code = p.waitFor();
      if (code != 0) {
        String exception = String.format("Exit status: %d", code);
        exception += stderr_out;
        SecurerString.secureErase(gpg.data);
        gpg = null;
        throw new GPGException(exception);
      }

      InputStream is = p.getInputStream();
      Reader stdout = new InputStreamReader(is);
      final int available = is.available();
      final int buf_size = available < 4096 ? 4096 : available;
      buf = new char[buf_size];
      int read_now = 0;
      int read_until_now = 0;
      log.debug("Buffer size: " + buf_size);
      while ((read_now = stdout.read(buf, read_until_now, buf.length - read_until_now)) != -1) {
        if (buf.length - read_until_now < buf_size) {
          log.debug("Expanding char");
          char newbuff[] = new char[buf.length << 1];
          System.arraycopy(buf, 0, newbuff, 0, read_until_now);
          SecurerString.secureErase(buf);
          buf = newbuff;
        }
        read_until_now += read_now;
      }
      stdout.close();
      log.debug("Data read into buf: " + read_until_now);
    }
    catch (IOException|InterruptedException e) {
      SecurerString.secureErase(buf);
      SecurerString.secureErase(gpg.data);
      gpg = null;
      throw new GPGException(e.toString());
    }
  }

  public void output(Object className, String methodName) throws GPGException {
    pre_output();

    try {
      /*   // http://stackoverflow.com/a/5428621 */
      Class<?> c;
      Method method;
      try {
        try {
          c = (Class<?>)className;
        }
        catch (ClassCastException e) {
          c = className.getClass();
        }

        log.debug("Invoking " + c.getSimpleName () + "::[" + methodName + "]");
        method = c.getDeclaredMethod(methodName, buf.getClass());
        method.invoke(className, buf);
      }
      catch (NoSuchMethodException|IllegalAccessException|InvocationTargetException e) {
        throw new GPGException(e.toString());
      }
    }
    finally {
      SecurerString.secureErase(buf);
      SecurerString.secureErase(gpg.data);
      gpg = null;
    }
  }

  public void output(File file) throws GPGException {
    command.add("--output");
    command.add(file.getAbsolutePath());

    pre_output();

    SecurerString.secureErase(buf);
    SecurerString.secureErase(gpg.data);
    gpg = null;
  }

  public static GPG encrypt(String data) {
    gpg = (null == gpg) ? new GPG() : gpg;
    gpg.command.add("--encrypt");
    gpg.data = data;
    return gpg;
  }

  public static GPG encrypt(File file) {
    gpg = (null == gpg) ? new GPG() : gpg;
    gpg.command.add("--encrypt");
    gpg.file = file;
    return gpg;
  }

  public static GPG decrypt(String data) {
    gpg = (null == gpg) ? new GPG() : gpg;
    gpg.command.add("--decrypt");
    gpg.data = data;
    return gpg;
  }

  public static GPG decrypt(File file) {
    gpg = (null == gpg) ? new GPG() : gpg;
    gpg.command.add("--decrypt");
    gpg.file = file;
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

  public static GPG sign() {
    gpg = (null == gpg) ? new GPG() : gpg;
    gpg.command.add("--sign");
    return gpg;
  }

  public GPG armor() {
    command.add("--armor");
    return this;
  }

  public GPG recipient(String recipient) {
    command.add("--recipient");
    command.add(recipient);
    return this;
  }

  public GPG recipient(List<String> recipients) {
    for (String recipient: recipients) {
      recipient(recipient);
    }
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

  public GPG verify() {
    checkVerification = true;
    return this;
  }
}
