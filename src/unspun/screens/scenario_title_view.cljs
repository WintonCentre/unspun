(ns unspun.screens.scenario-title-view
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [view text] :as rn]
            [shared.ui :refer [tffsz n-icon]]
            ))

(rum/defc star
  [filled]
  (n-icon {:name  (if filled "ios-star" "ios-star-outline")
           :style {:color "orange"}}
          ))

(rum/defc stars [r n]
  "Render r full stars out of n"
  (view {:style {
                 :justifyContent "flex-end"
                 :flexDirection  "row"
                 :transform      [{:translateX 0}
                                  {:scale 1                 ;(/ tffsz 24)
                                   }
                                  ]}}
        (map-indexed #(rum/with-key (star (< %1 r)) %1) (range n))))

(rum/defc scenario-title [title text-field qoe]

  (view {:flex           1
         :flexDirection  "column"
         :justifyContent "flex-start"
         :alignItems     "center"
         :paddingTop     0
         :paddingBottom  0}
        (text {:style {:flex           0.9
                       :justifyContent "flex-end"
                       :paddingLeft    20
                       :paddingRight   20
                       :paddingTop     5
                       :paddingBottom  0
                       :alignItems     "center"
                       :fontSize       (* (if qoe 1.2 1) tffsz)}}
              (text-field :text-icons "bold" (str title " ")))
        (when (and qoe (pos? qoe))
          (view {:style {:flex           0
                         :alignItems     "center"
                         :flexDirection  "row"
                         :justifyContent "flex-end"}}
                (text {:style {:fontSize (* 0.8 tffsz)}}
                      (text-field :text-icons "bold"
                                  (str "Quality of evidence "
                                       (condp = qoe
                                         1 "low "
                                         2 "moderate "
                                         3 "high "))))
                (stars qoe 3)
                ))
        ))