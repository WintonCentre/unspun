(ns graphics.svg
  (:require [cljs-exponent.core :refer [exponent]]
            [cljs-exponent.components :refer [element]]
            [clojure.string :refer [lower-case]]
            ))

(def svg-components
  ["Circle"
   "Ellipse"
   "G"
   "LinearGradient"
   "RadialGradient"
   "Line"
   "Path"
   "Polygon"
   "Polyline"
   "Rect"
   "Symbol"
   "Text"
   "Use"
   "Defs"
   "Stop"])

(def exp-svg
  (partial aget exponent "Components" "Svg"))


(def svg
  (partial element
           (aget exponent "Components" "Svg")))

(defn wrap-svg-component [name]
  (partial element (exp-svg name)))

#_(doseq [svg-component svg-components]
  (def (symbol (lower-case svg-component)) (exp-svg svg-component)))


(def circle
  (partial element (exp-svg "Circle")))

(def rect
  (partial element
           (aget exponent "Components" "Svg" "Rect")))