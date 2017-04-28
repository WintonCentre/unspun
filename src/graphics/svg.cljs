(ns graphics.svg
  (:require [cljs-exponent.core :refer [expo]]
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

(def wrap-svg (partial aget expo "Components" "Svg"))

(defn wrap-svg-component [name]
  (partial element (wrap-svg name)))

(def svg (partial element (wrap-svg)))
(def circle (wrap-svg-component "Circle"))
(def ellipse (wrap-svg-component "Ellipse"))
(def g (wrap-svg-component "G"))
(def linearGradient (wrap-svg-component "LinearGradient"))
(def radialGradient (wrap-svg-component "RadialGRadient"))
(def line (wrap-svg-component "Line"))
(def path (wrap-svg-component "Path"))
(def polygon (wrap-svg-component "Polygon"))
(def polyline (wrap-svg-component "PolyLine"))
(def rect (wrap-svg-component "Rect"))
(def svg-symbol (wrap-svg-component "Symbol"))
(def text (wrap-svg-component "Text"))
(def svg-use (wrap-svg-component "Use"))
(def defs (wrap-svg-component "Defs"))
(def stop (wrap-svg-component "Stop"))



