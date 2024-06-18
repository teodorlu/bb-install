(ns babashka.bbin2.install
  (:require
   [babashka.bbin.scripts :as bbin1.scripts]
   [babashka.bbin.util :as bbin1.util]
   [babashka.bbin.dirs :as bbin1.dirs]
   [babashka.bbin.protocols :as bbin1.protocols]))

(defn install [cli-opts]
  (if-not (:script/lib cli-opts)
    (bbin1.util/print-help)
    (do
      (bbin1.dirs/ensure-bbin-dirs cli-opts)
      (when-not (bbin1.util/edn? cli-opts)
        (println)
        (println (bbin1.util/bold "Starting install..." cli-opts)))
      (let [cli-opts' (bbin1.util/canonicalized-cli-opts cli-opts)
            script (bbin1.scripts/new-script cli-opts')]
        (try
          (bbin1.protocols/install script)
          (catch Exception raw-exception
            (let [e (ex-data raw-exception)]
              (case (:error e)
                :babashka.bbin.scripts.common/main-opts-not-found
                (do
                  (println "Error: Main opts not found.")
                  (println)
                  (println "Use --main-opts MAIN-OPTS or :bbin/bin in `deps.edn` to provide main opts.")
                  (System/exit 1))

                ;; Cannot handle exception, re-throw.
                (throw raw-exception)
                ))))))))
