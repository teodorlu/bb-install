(ns babashka.bbin.test-util
  (:require [babashka.bbin.cli :as bbin]
            [babashka.bbin.dirs :as dirs]
            [babashka.fs :as fs]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.test :as test]))

(defmethod clojure.test/report :begin-test-var [m]
  (println "===" (-> m :var meta :name))
  (println))

(defn reset-test-dir []
  (let [path (str (fs/file (fs/temp-dir) "bbin-test"))]
    (fs/delete-tree path)
    (fs/create-dirs path)
    (doto path (fs/delete-on-exit))))

(def test-dir (reset-test-dir))

(defn- relativize [original]
  (fs/file test-dir (fs/relativize (dirs/user-home) original)))

(defn bbin-dirs-fixture []
  (fn [f]
    (binding [dirs/*legacy-bin-dir* (relativize (dirs/legacy-bin-dir))
              dirs/*legacy-jars-dir* (relativize (dirs/legacy-jars-dir))
              dirs/*xdg-bin-dir* (relativize (dirs/xdg-bin-dir nil))
              dirs/*xdg-jars-dir* (relativize (dirs/xdg-jars-dir nil))]
      (f))))

(def git-wrapper-path
  (-> (if (fs/windows?)
        "test-resources/git-wrapper.bat"
        "test-resources/git-wrapper")
      fs/file
      fs/canonicalize
      str))

(defn- set-gitlibs-command []
  (System/setProperty "clojure.gitlibs.command" git-wrapper-path))

(defn bbin-private-keys-fixture []
  (fn [f]
    (set-gitlibs-command)
    (f)))

(defn bbin [main-args & {:as opts}]
  (let [out (str/trim (with-out-str (bbin/bbin main-args opts)))]
    (if (#{:edn} (:out opts))
      (edn/read-string out)
      out)))

(defn use-fixtures
  "A wrapper for `use-fixtures` to fix an issue with Kaocha."
  [type & fixtures]
  (let [k (case type
            :once ::test/once-fixtures
            :each ::test/each-fixtures)]
    (apply test/use-fixtures type fixtures)
    (alter-meta! *ns* assoc k fixtures)))