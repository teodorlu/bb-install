(ns babashka.bbin2
  (:require
   [babashka.cli :as cli]
   [babashka.bbin.scripts :as scripts]))

(defn cmd-install [opts]
  (scripts/install opts))

(def dispatch-table
  [{:cmds ["install"] :fn cmd-install}])

(defn -main [& args]
  (binding [*print-namespace-maps* false]
    (cli/dispatch dispatch-table args)))

(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
