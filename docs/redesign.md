# `bb-install` redesign

Goals:

- Support installing script from different sources
- Support uninstalling the given scripts

Philosophy:

1. Make a good mental model for understanding what scripts are installed, and support uninstalling scripts

2. With a good mental model, we want to make bugs trivial to catch.

Proposed implementation:

1. Support installing babashka scripts to folders on path.
   Rely on `babashka.fs/which` and a fingerprint to remove scripts.
   Do something like this in the beginning of babashka scripts:

   ```clojure
   {:teodorlu.bb-install/fingerprint "18acc212-3ae7-4603-9720-d1b615edc2bf"}
   ```

   Then:

   1. When the fingerprint is found, support uninstalling.

   2. Otherwise, uninstall only with --force.

2. Alternative approach: symlinks to a folder we control.

    1. Store all bb installed scripts under _share_.

    2. Symlink from `~/.local/bin` to `~/.local/share/teodorlu.bb-install/bin`

    3. When uninstalling, we check whether the script has been installed as a symlink.
       If it is a symlink to our share directory, we delete and cleanup both.

3. Or -- delete only scripts that start with a bb shebang.

    This is perhaps the best solution.

    First line is a bb shebang.
    Next line is our fingerprint.

4. Cleanly support the different places _from where_ we want to install things.

    1. Start out by aiming to understand how `babashka/bbin` does it.

    2. Then consider how to do this.

## Fingerprinting

I believe it makes sense to fingerprint all files we install.
With something like this:

```clojure
#!/usr/bin/env bb
{:teodorlu.bb-install/fingerprint "18acc212-3ae7-4603-9720-d1b615edc2bf"}
```

**Why fingerprint scripts?**

- Support safe uninstallation.
  We shouldn't accidentally remove things we didn't install.

- (possibly) support listing installed scripts.
  Listing installed scripts could also be solved with a transaction log.

## A transaction log

An alternative to using the fingerprinted scripts themselves in order to build information about known installed scripts, is to keep a transaction log.
By reducing over the transaction log, we'll know:

1. All installed scripts
2. All uninstalled scripts
3. All other commands run by the system.

The transaction log would work nicely in combination with a command-based architecture.
A transaction is simply a command that has been written down.
Perhaps the transaction log needs two things --- both a _command has started_ message, and a _command has finished_ message.

## Required work: understand how each of the existing procurers work.

1. Does the procurer install with a single file, or with multiple files?

2. Does the procurer support updates?
   If yes, how?

   1. Updates appears _not to_ be supported by `bbin 0.1.13`.
      There is no update or upgrade subcommand.
