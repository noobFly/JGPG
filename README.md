Introduction
------------

J<del>RPG</del><ins>GPG</ins> is a Java wrapper for GPG2 and gpg-agent with focus on
client-side use.

It uses the builder pattern.

TODO
----
* Make the tests check so that encrypted data is the same decrypted.
* Replace all println's with [logback](http://logback.qos.ch/)
* Make it possible to add a List<String> of recipients
* Add as tests and implement
 * GPG.{en,de}crypt(java.io.File)
 * GPG.{en,de}crypt("string")
 * GPG.output(System.out, "println");
 * GPG.output(java.io.File);
* Figure out how to disable the output of Gradles tests since it screws with
  the searching of the secret in the heap.
* Do something about all the e.printStackTrace's.

License
-------

JGPG is licensed under the ISC license, see LICENSE.
