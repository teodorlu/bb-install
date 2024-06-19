(ns babashka.bbin.configuration
  "A bbin config is a collection of user choices for a bbin command

  This namespace is intended to encapsulate the choices that need to be made
  about what bbin is supposed to do. Those choices come from CLI arguments,
  environment variables and terminal context.

  The motivation for introducing this namespace is to let the code that *does
  things* be focused on the things it does, rather than pulling in configuration
  from the environment on the fly.

  The whole configuration is not known immediately: we know CLI arguments and
  environment variables when bbin starts, but certain configuration depends on
  the installation target, bbin script authors can provide certain configuration
  in `deps.edn` files.")
