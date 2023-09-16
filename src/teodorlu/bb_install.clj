(ns teodorlu.bb-install
  (:require [babashka.bbin.cli :as bbin]
            [clojure.string :as str]
            [clojure.edn :as edn]
            [babashka.fs :as fs]))


(defn ^:private edn-read-string-orelse [s orelse]
  (try (edn/read-string s)
       (catch java.lang.RuntimeException _e
         orelse)))

(def fingerprint {:teodorlu.bb-install/fingerprint "18acc212-3ae7-4603-9720-d1b615edc2bf"})
(def bb-shebang "#!/usr/bin/env bb")

(comment
  (edn-read-string-orelse "{:x 1" ::invalid))

(defn script-fingerprint? [data]
  (= (::fingerprint data)
     (::fingerprint fingerprint)))

(comment
  (script-fingerprint? fingerprint)
  )

(defn installed-script-file? [s]
  (let [[first-line second-line] (str/split-lines s)]
    (boolean
     (and (str/starts-with? first-line bb-shebang)
          (when-let [bb-install-manifest (edn-read-string-orelse second-line nil)]
            (script-fingerprint? bb-install-manifest))))))

(comment
  (let [script-file-string (str bb-shebang
                                "\n"
                                (pr-str fingerprint)
                                "\n"
                                (pr-str '(prinln (+ 1 2)))
                                "\n")]
    (installed-script-file? script-file-string))
  ;; => true

  (installed-script-file?
   (slurp (fs/file (fs/which "mblog"))))
  ;; => true

  (installed-script-file?
   (slurp (fs/file (fs/which "bbin"))))
  ;; => false

  (let [script-file-string (str bb-shebang
                                "\n"
                                (pr-str fingerprint)
                                "\n"
                                (pr-str '(prinln (+ 1 2)))
                                "\n")]
    (count script-file-string))
  ;; => 110


  )

;; Ideas:
;; 1. start fingerprinting the scripts
;; 2. Be able to identify whether this is a script we've installed or not



(defn -main [& args]
  (apply bbin/-main args))
