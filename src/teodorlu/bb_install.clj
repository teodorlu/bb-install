(ns teodorlu.bb-install
  (:require
   [babashka.bbin.cli :as bbin]
   [babashka.cli :as cli]
   [teodorlu.bb-install.core :as core]))

;; Ideas:
;; 1. start fingerprinting the scripts
;; 2. Be able to identify whether this is a script we've installed or not

(defn parsed-cmds+opts->raw-cmds [parsed-cmds+opts]
  (apply concat (:cmds parsed-cmds+opts)
         (for [[option value] (:opts parsed-cmds+opts)]
           [(str "--" (name option))
            (pr-str value)])))

(comment
  (parsed-cmds+opts->raw-cmds '{:cmds ("install" "myfile.clj"),
                                :args ("myfile.clj"),
                                :rest-cmds ("myfile.clj"),
                                :opts {:dry-run true, :verbose true},
                                :dispatch ["install"]})
  ;; => ("install" "myfile.clj" "--dry-run" "true" "--verbose" "true")
  )

(defn command-install [opts]
  (apply bbin/-main (parsed-cmds+opts->raw-cmds opts)))

(defn command-uninstall [opts]
  (apply bbin/-main (parsed-cmds+opts->raw-cmds opts)))

(defn command-ls
  "Should this use a bbin command log to produce the listing?

  ~/.local/share/teodorlu.bb-install/tx-log.txt

  One EDN map each line. A timestamp, a session ID, some other stuff."
  [opts]
  (apply bbin/-main (parsed-cmds+opts->raw-cmds opts)))

(defn command-bin [opts]
  (apply bbin/-main (parsed-cmds+opts->raw-cmds opts)))

(defn command-version [opts]
  (apply bbin/-main (parsed-cmds+opts->raw-cmds opts)))

(defn command-help [opts]
  (apply bbin/-main (parsed-cmds+opts->raw-cmds opts)))

(def subcommands
  [{:cmds ["install"] :fn command-install}
   {:cmds ["uninstall"] :fn command-uninstall}
   {:cmds ["ls"] :fn command-ls}
   {:cmds ["bin"] :fn command-bin}
   {:cmds ["version"] :fn command-version}
   {:cmds ["help"] :fn command-help}])

(defn -main [& args]
  (cli/dispatch subcommands args))
