(ns babashka.bbin2
  (:require
   [babashka.cli :as cli]
   [babashka.bbin2.install]
   [clojure.string :as str]))

(declare dispatch-table)

(defn help [_]
  (println "Available subcommands:")
  (println)
  (doseq [row dispatch-table]
    (when (seq (:cmds row))
      (println "  " (str/join " " (:cmds row))))))

(def dispatch-table
  [{:cmds ["install"] :fn babashka.bbin2.install/install :args->opts [:script/lib]}
   {:cmds []          :fn help}])

(defn -main [& args]
  (binding [*print-namespace-maps* false]
    (cli/dispatch dispatch-table args)))

(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
