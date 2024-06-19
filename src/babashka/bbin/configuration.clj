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
  in `deps.edn` files."
  (:require [babashka.process :as process]
            [babashka.fs :as fs]))

(defn infer-is-tty []
  (let [fd 1 key :out]
    (-> ["test" "-t" (str fd)]
        (process/process {key :inherit :env {}})
        deref
        :exit
        (= 0))))

(defn create-configuration [cli-opts environment is-tty]
  {:bbin.configuration/is-tty? is-tty
   :bbin.configuration/no-color? (or (false? (:color cli-opts))
                                     (fs/windows?)
                                     (:plain cli-opts)
                                     (not is-tty)
                                     (get environment "NO_COLOR")
                                     (= "dumb" (get environment "TERM")))})

(defn is-tty? [conf]
  (get conf :bbin.configuration/is-tty?))

(defn no-color? [conf]
  (get conf :bbin.configuration/no-color?))

(comment
  ;; incomplete, a draft

  (defn create-configuration [cli-opts environment is-tty])

  ;; the idea is to let the toplevel main create a configuration by providing
  ;; the side-effecting stuff on the top level, ie

  (fn [cli-opts]
    (let [configuration (create-configuration cli-opts (System/getenv) (infer-is-tty))]
      ,,,,))

  ;; then use accessor functions to pull things out,

  (require '[babashka.bbin.configuration :as configuration])
  (fn [cli-opts]
    (let [conf (create-configuration cli-opts (System/getenv) (infer-is-tty))]
      (configuration/is-tty? conf)
      (configuration/no-color? conf)
      ,,,,))


  )
