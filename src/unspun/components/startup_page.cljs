(ns unspun.components.startup-page
  (:require-macros [rum.core :refer [defc]])
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index]]
            [graphics.svg :refer [svg circle rect]]
            ))

(defn title-style [palette]
  {:color          (:light-primary palette)
   :marginVertical 6
   :fontSize       50
   })

(defn startup-page-style [palette]
  {:flex            1
   :backgroundColor (:primary palette)})

;(def svg (aget cljs-exponent.core/exponent "Components" "Svg"))
;(def circle (r/adapt-react-class (aget svg "Circle")))


(defc startup-title < rum/reactive [& {:keys [title style]
                                       :or   {title "Relative Risks Unspun"
                                              style (title-style (get-palette (rum/react palette-index)))}}]
  (text {
         :style style} title))

(defc startup-page < rum/reactive [& {:keys [title logo launcher style]
                                      :or   {title startup-title
                                             style (startup-page-style (get-palette (rum/react palette-index)))}}]
  (view {:style style}
        (title)
        (svg {:flex 1}
             (rect {:x 50 :y 50 :width 200 :height 200 :fill "red"})
             (circle {:cx   360 :cy   460 :r    50 :fill "blue"}))))

