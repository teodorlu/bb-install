(ns teodorlu.bb-install
  (:require
   [babashka.bbin.cli :as bbin]
   [clojure.string :as str]
   [clojure.edn :as edn]
   [babashka.fs :as fs]
   [babashka.cli :as cli]))


(defn ^:private edn-read-string-orelse [s orelse]
  (try (edn/read-string s)
       (catch java.lang.RuntimeException _e
         orelse)))

(def bb-shebang "#!/usr/bin/env bb")
(def fingerprint {:teodorlu.bb-install/fingerprint "18acc212-3ae7-4603-9720-d1b615edc2bf"})

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
