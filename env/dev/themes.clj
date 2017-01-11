(ns themes
  (:require [clojure.java.io :refer [make-parents]]))

(defn copy-native-themes []
  (let [src "js/nativetheme.js"
        dest1 "target/env/themes/nativetheme.js"
        dest2 "themes/nativetheme.js"]
    (make-parents dest1)
    (spit dest1 (slurp src))
    (make-parents dest2)
    (spit dest2 (slurp src))))

(defn -main []
  (copy-native-themes))