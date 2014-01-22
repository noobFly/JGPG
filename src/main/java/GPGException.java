package se.soy.gpg;

public class GPGException extends RuntimeException {
  public GPGException() {
  }

  public GPGException(String msg) {
    super(msg);
  }
}
