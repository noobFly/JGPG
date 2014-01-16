package se.soy.gpg;

class GPGException extends RuntimeException {
  public GPGException() {
  }

  public GPGException(String msg) {
    super(msg);
  }
}
