(ns unspun.common)

(def react-native (js/require "react-native"))

(def platform (aget react-native "Platform"))

(defn get-platform
  []
  (.-OS platform))

(defn ios?
  []
  (= "ios" (get-platform)))

(defn android?
  []
  (= "android" (get-platform)))
