Introduction
------------

J<del>RPG</del><ins>GPG</ins> is a Java wrapper for GPG2 and gpg-agent with focus on
client-side usage.

It uses the builder pattern, and uses
[SecurerString](https://github.com/simmel/SecurerString) to securely scrub
secrets from memory/heap.

TODO
----
* Replace all println's with [logback](http://logback.qos.ch/)
* Figure out how to disable the output of Gradles tests since it screws with
  the searching of the secret in the heap.
* Do something about all the e.printStackTrace's.

License
-------

JGPG is licensed under the ISC license, see LICENSE.
