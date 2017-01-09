(ns themes)

(defn copy-native-themes []
  (spit "target/env/themes/nativetheme.js" (slurp "js/nativetheme.js")))

