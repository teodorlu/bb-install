(ns babashka.bbin2.install
  (:require
   [babashka.bbin.dirs :as bbin1.dirs]
   [babashka.bbin.protocols :as bbin1.protocols]
   [babashka.bbin.scripts :as bbin1.scripts]
   [babashka.bbin.util :as bbin1.util]))

(defn install [cli-opts]
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
            ))))))

(defn parse-opts [args+opts]
  (let [script-lib (:script/lib (:opts args+opts))
        script-name (or (:as (:opts args+opts))
                        (:script/name (:opts args+opts)))]
    (if-not script-lib
      nil ; invalid args+opts, return nil.

      (cond-> {:script/lib script-lib}

        (:local/root (:opts args+opts))
        (assoc :local/root (:local/root (:opts args+opts)))

        ;; --as mybin can be used to control where the script is installed.
        ;;
        ;; Example:
        ;;
        ;;   bbin2 install . --as script2
        ;;
        ;; I'd perfer if this was called `:script/name` or something a bit
        ;; longer, at least when we refer to it in code. `:as` is often used in
        ;; `require` forms.
        ;;
        ;; For now, we keep the current behavior exactly as-is.
        script-name
        (assoc :as script-name)

        #_ ""))))

(defn cmd-install [args+opts]
  (if-let [opts (parse-opts args+opts)]
    (install opts)
    (do
      (bbin1.util/print-help)
      (System/exit 1))))
