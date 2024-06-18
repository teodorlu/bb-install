(ns babashka.bbin2
  (:require
   [babashka.cli :as cli]
   [babashka.bbin2.install]))

(def dispatch-table
  [{:cmds ["install"] :fn babashka.bbin2.install/install}])

(defn -main [& args]
  (binding [*print-namespace-maps* false]
    (cli/dispatch dispatch-table args)))

(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
