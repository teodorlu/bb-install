(ns babashka.bbin2.install
  (:require
   [babashka.bbin.dirs :as bbin1.dirs]
   [babashka.bbin.protocols :as bbin1.protocols]
   [babashka.bbin.scripts :as bbin1.scripts]
   [babashka.bbin.util :as bbin1.util]))

(defn install [{:keys [script-lib]}]
  (let [cli-opts {:script/lib script-lib}]
    (bbin1.dirs/ensure-bbin-dirs {:script/lib script-lib})
    (when-not (bbin1.util/edn? {:script/lib script-lib})
      (println)
      (println (bbin1.util/bold "Starting install..." {:script/lib script-lib})))
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
              )))))))

(defn parse-opts [args+opts]
  (let [script-lib (:script/lib (:opts args+opts))]
    (if-not script-lib
      nil ; we can't do anything

      (cond-> {:script/lib script-lib}))))

(defn cmd-install [args+opts]
  (if-let [opts (parse-opts args+opts)]
    (install opts)
    (do
      (bbin1.util/print-help)
      (System/exit 1))))
