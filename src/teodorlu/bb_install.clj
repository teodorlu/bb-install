(ns teodorlu.bb-install
  (:require [babashka.bbin.cli :as bbin]))

(defn -main [& args]
  (apply bbin/-main args))
