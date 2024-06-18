(ns babashka.bbin2.install
  (:require
   [babashka.bbin.dirs :as bbin1.dirs]
   [babashka.bbin.protocols :as bbin1.protocols]
   [babashka.bbin.scripts :as bbin1.scripts]
   [babashka.bbin.util :as bbin1.util]
   [clojure.string :as str]))

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

(def install-helptext
  (str/trim "
bbin install [script]

By default, scripts are installed to `~/.local/bin`. The bin scripts can be
configured using the CLI options or the `:bbin/bin` key in `bb.edn`.

EXAMPLE `bb.edn` CONFIG

```clojure
{:bbin/bin {neil {:main-opts [\"-f\" \"neil\"]}}}
```

SUPPORTED OPTIONS

`bbin` throws an error if conflicting options are provided, such as setting both
`--git/sha` and `--mvn/version`.

If neither `--git/tag` or `--git/sha` is provided, the latest tag from the Git
repo is used. If no tags exist, the latest SHA will be used.

`--as`          Local script name

`--git/sha`     Git SHA for the Git repo the script is installed from

`--git/tag`     Git tag for the Git repo the script is installed from

`--git/url`     The URL for a Git repo

`--latest-sha`  If provided, find the latest SHA from the Git repo

`--local/root`  The path of a local directory containing a `deps.edn` file

`--main-opts`   The provided options (EDN format) will be passed to the `bb`
                command-line when the installed script is run. By default,
                `--main-opts` will be set to `[\"-m\" ...]`, inferring the main
                function from the lib name. For example, if you provide a lib
                name like `io.github.rads/watch`, `bbin` will infer
                `rads.watch/-main`. Project authors can provide a default in the
                `:bbin/bin` key in `bb.edn`.

`--mvn/version` The version of a Maven dependency

`--ns-default`  The namespace to use to find functions (tool mode only) Project
                authors can provide a default in the `:bbin/bin` key in `bb.edn`

`--tool`        If this option is provided, the script will be installed using
                **tool mode** When enabled, the installed script acts as an
                entry point for functions in a namespace, similar to `clj -T` If
                no function is provided, the installed script will infer a help
                message based on the function docstrings
"))

(defn cmd-install [args+opts]
  (if-let [opts (parse-opts args+opts)]
    (install opts)
    (do
      (println install-helptext)
      (System/exit 1))))
