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

;; Namespace status: DRAFT.
;;
;; Extracting "configuration" from `bbin` would require quite a few changes
;; throughout the code.

(defn infer-is-tty []
  (let [fd 1 key :out]
    (-> ["test" "-t" (str fd)]
        (process/process {key :inherit :env {}})
        deref
        :exit
        (= 0))))

(defn create-configuration
  "Create the whole configuration in one place.

  `create-configuration` should not do any side effects, all configuration
  parameters are passed as arguments."
  [cli-opts environment is-tty is-windows]
  (merge
   {:bbin.configuration/is-tty? is-tty
    :bbin.configuration/no-color? (or (false? (:color cli-opts))
                                      is-windows
                                      (:plain cli-opts)
                                      (not is-tty)
                                      (get environment "NO_COLOR")
                                      (= "dumb" (get environment "TERM")))}
   (when-let [local-root (:local/root cli-opts)]
     {:local/root (str (fs/canonicalize local-root {:nofollow-links true}))})))

(comment
  (let [cli-opts {}
        conf (create-configuration cli-opts (System/getenv) (infer-is-tty) (fs/windows?))]
    ;; now the configuration can be used like this:
    (babashka.bbin.configuration/is-tty? conf)
    (babashka.bbin.configuration/no-color? conf)
    #_ "..."))

(defn is-tty? [conf]
  (get conf :bbin.configuration/is-tty?))

(defn no-color? [conf]
  (get conf :bbin.configuration/no-color?))
