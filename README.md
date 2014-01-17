Introduction
------------

J<del>RPG</del><ins>GPG</ins> is a Java wrapper for GPG2 and gpg-agent with focus on
client-side use.

It uses the builder pattern.

TODO
----
* Replace all println's with [logback](http://logback.qos.ch/)
* Make it possible to add a List<String> of recipients
* Add as tests and implement
** GPG.{en,de}crypt(java.io.File)
** GPG.{en,de}crypt("string")
** GPG.output(System.out, "println");
** GPG.output(java.io.File);

License
-------

JGPG is licensed under the ISC license, see LICENSE.
