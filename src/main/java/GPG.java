package se.soy.gpg;

public class GPG {
  // FIXME Remove when done
  static<T> void println(T arg) { System.out.println(arg); }

  public static void main(String[] args) {
    println("main");
    GPG.encrypt().armor().recipient("0xrecipient").output();
    /*
    GPG.encrypt().armor().recipient("0xrecipient").output();
    GPG.decrypt().armor().local-user("0xlocaluser").output("/tmp/a-file");
    GPG.sign().armor().();
    */
  }
  private boolean armor = false;
  private String recipient;

  public void output() {
    println("OPTIONS:");
    println(String.format("armor?: %s", (armor) ? "true" : "false"));
    println(String.format("recipient?: %s", recipient));
  }

  public static GPG encrypt() {
    GPG gpg = new GPG();
    println("encrypt()");
    return gpg;
  }

  public GPG armor() {
    this.armor = true;
    return this;
  }

  public GPG recipient(String recipient) {
    this.recipient = recipient;
    return this;
  }
}
