(ns babashka.bbin2
  (:require
   [babashka.cli :as cli]
   [babashka.bbin.scripts :as bbin1.scripts]
   [babashka.bbin2.install :as install]))

(defn cmd-install [opts]
  )

(def dispatch-table
  [{:cmds ["install"] :fn cmd-install}])

(defn -main [& args]
  (binding [*print-namespace-maps* false]
    (cli/dispatch dispatch-table args)))

(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
