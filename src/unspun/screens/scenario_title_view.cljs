(ns unspun.screens.scenario-title-view
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [view text] :as rn]
            [shared.ui :refer [tffsz n-icon]]
            ))

(rum/defc star []
  (n-icon {:name  "ios-star"
           :style {:color     "orange"
                   :transform [{:scale 1}]}}))

(rum/defc stars [n]
  (view {:style {:flex           0.5
                 :justifyContent "flex-start"
                 :flexDirection  "row"
                 :transform      [{:translateX -35}
                                  {:scale 0.5}
                                  ]}}
        (map-indexed #(rum/with-key (star) %1) (range n))))

(rum/defc scenario-title [title text-field qoe]

  (view {:flex           1
         :flexDirection  "column"
         :justifyContent "flex-start"
         :paddingTop     0
         :paddingBottom  0}
        (text {:style {:flex           0.9
                       :justifyContent "flex-end"
                       :paddingLeft    20
                       :paddingRight   20
                       :paddingTop     0
                       :paddingBottom  0
                       :textAlign      "center"
                       :fontSize       (* 1.2 tffsz)}}
              (text-field :text-icons "bold" title))
        (when (pos? qoe)
          (view {:style {:flex           0.1
                         :flexDirection  "row"
                         :height         tffsz
                         :justifyContent "flex-start"
                         :alignItems     "center"}}
                (view {:style {:flex           0.5
                               :flexDirection  "row"
                               :justifyContent "flex-end"}}
                      (text {:style {:textAlign "right"
                                     :fontSize  (* 0.8 tffsz)}}
                            (text-field :text-icons "bold" "Quality of Evidence")))
                (view {:style {:flex           0.5
                               :flexDirection  "row"
                               :justifyContent "flex-start"}}
                      (stars qoe)
                      )))
        ))