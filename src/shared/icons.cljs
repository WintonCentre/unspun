(ns shared.icons)

(def vector-icons (js/require "@expo/vector-icons"))
(def Ionicons (aget vector-icons "Ionicons"))
(defn ionicon [attrs] (.createElement js/React Ionicons (clj->js attrs)))