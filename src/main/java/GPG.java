package se.soy.gpg;

import java.util.List;
import java.util.ArrayList;

public class GPG {
  // FIXME Remove when done
  static<T> void println(T arg) { System.out.println(arg); }

  public static void main(String[] args) {
    println("main");
    GPG.encrypt().armor().sign().recipient("0xrecipient").output();
    /*
    GPG.decrypt().armor().local-user("0xlocaluser").output("/tmp/a-file");
    GPG.sign().armor().();
    */
  }

  private String mode;
  private boolean armor = false;
  private List<String> recipients = new ArrayList<String>();
  private static GPG gpg = null;

  public void output() {
    println("OPTIONS:");
    println(String.format("mode: %s", mode));
    println(String.format("armor?: %s", (armor) ? "true" : "false"));
    println(String.format("recipients?: %s", recipients));
  }

  public static GPG encrypt() {
    gpg.mode = "encrypt";
    gpg = (null == gpg) ? new GPG() : gpg;
    return gpg;
  }

  public static GPG sign() {
    gpg = (null == gpg) ? new GPG() : gpg;
    gpg.mode = "clearsign";
    return gpg;
  }

  public GPG armor() {
    this.armor = true;
    return this;
  }

  // TODO: Add recipients(List<String> recipients)
  public GPG recipient(String recipient) {
    this.recipients.add(recipient);
    return this;
  }
}
