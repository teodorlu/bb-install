(ns teodorlu.bb-install.core
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [clojure.string :as str]))

;; bb-install core logic
;;
;; `teodorlu.bb-install.core` is used both by `teodorlu.bb-install` and directly from `bbin` source.

(def bb-shebang "#!/usr/bin/env bb")
(def fingerprint {:teodorlu.bb-install/fingerprint "18acc212-3ae7-4603-9720-d1b615edc2bf"})

(defn script-fingerprint? [data]
  (= (:teodorlu.bb-install/fingerprint data)
     (:teodorlu.bb-install/fingerprint fingerprint)))

(comment
  (script-fingerprint? fingerprint)
  )

(defn ^:private edn-read-string-orelse [s orelse]
  (try (edn/read-string s)
       (catch java.lang.RuntimeException _e
         orelse)))

(comment
  (edn-read-string-orelse "{:x 1" ::invalid))

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
