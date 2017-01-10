(ns unspun.screens.number-needed
  (:require [cljs-exponent.components :refer [element text view image touchable-highlight status-bar] :as rn]
            [themes.palettes :refer [get-palette]]
            [shared.ui :refer [n-icon]]
            [unspun.db :refer [app-state palette-index to-pc number-needed stories story-index story-icon text-generator nn1]]
            [rum.core :as rum]))


(defn draw-icon [scenar color scale]
  (n-icon {:name  (story-icon scenar)
           :style {:color         color
                   :transform [{:scale scale}]}}))

(defn icon-square-size [nn]
  (Math.ceil (Math.sqrt nn)))

(rum/defc page < rum/reactive []
  (let [scenar ((rum/react stories) (rum/react story-index))
        br (:baseline-risk scenar)
        rr (:relative-risk scenar)
        nn (number-needed rr br)
        grid-size (icon-square-size nn)
        grid-factor (/ 1 grid-size)
        palette (get-palette (rum/react palette-index))
        page-style {:flex            1
                    :backgroundColor (:primary palette)}]
    (view {:style {:flex 1}}
          (status-bar {:key      10
                       :hidden   false
                       :barStyle "light-content"})

          (view {:style page-style
                 :key   1}
                (view {:style {:flex            0.3
                               :justifyContent  "center"
                               :alignItems      "center"
                               :backgroundColor (:dark-primary palette)}}
                      (text {:style {:color      (:light-primary palette)
                                     :fontWeight "400"
                                     :padding    20
                                     :fontSize   (:fontSize scenar)}}
                            (text-generator nn1 scenar)))
                (view {:key   2
                       :style {:flex            0.7
                               :padding         20
                               :backgroundColor (:primary palette)}}
                      (map (fn [m] (view {:key   m
                                          :style {:flex          grid-factor
                                                  :flexDirection "row"
                                                  :alignItems "center"
                                                  :justifyContent "center"}}
                                         (map (fn [n] (view {:key   n
                                                             :style {:flex grid-factor
                                                                     :alignItems "center"
                                                                     :justifyContent "center"}}
                                                            (let [k (+ n (* m grid-size))]
                                                              (cond
                                                                (< k (dec nn)) (draw-icon scenar (:light-primary palette) 0.75)
                                                                (= k (dec nn)) (draw-icon scenar (:text-icons palette) 1.2)
                                                                :else nil))
                                                            )
                                                ) (range grid-size))
                                         )) (range grid-size))

                      #_(map #(draw-icon scenar (:text-icons palette)) (range (number-needed rr br)))
                      )
                ))))


