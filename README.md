Introduction
------------

J<del>RPG</del><ins>GPG</ins> is a Java wrapper for GPG2 and gpg-agent with focus on
client-side usage.

It uses the builder pattern, and uses
[SecurerString](https://github.com/simmel/SecurerString) to securely scrub
secrets from memory/heap.

TODO
----
* Figure out how to disable the output of Gradles tests since it screws with
  the searching of the secret in the heap.
* Fix storing non-armored GPG data in byte arrays. No matter how I tried I
  could not get it to decrypt properly, this is what gpg said:
```
gpg: encrypted with 1024-bit RSA key, ID 49E17571, created 2014-01-09
      "test test (test) <test@test>"
gpg: Signature made Wed 29 Jan 14:30:22 2014 CET using RSA key ID F870C097
gpg: Good signature from "test test (test) <test@test>"
gpg: [don't know]: indeterminate length for invalid packet type 7
gpg: mdc_packet with invalid encoding
gpg: decryption failed: Invalid packet
gpg: [don't know]: invalid packet (ctb=64)
```
* Change from RuntimeException? Investigate why it doesn't need to be catched and what we should use instead.

License
-------

JGPG is licensed under the ISC license, see LICENSE.
