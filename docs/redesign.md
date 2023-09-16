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

3. Cleanly support the different places _from where_ we want to install things.

    1. Start out by aiming to understand how `babashka/bbin` does it.

    2. Then consider how to do this.
