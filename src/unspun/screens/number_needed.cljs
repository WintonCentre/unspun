(ns unspun.screens.number-needed
  (:require [cljs-exponent.components :refer [element text view image touchable-highlight status-bar scroll-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [shared.ui :refer [n-icon]]
            [unspun.db :refer [app-state palette-index to-pc number-needed stories story-index text-generator nn1 nn2]]
            [rum.core :as rum]))


(defn draw-icon [scenar color scale]
  (n-icon {:name  (:icon scenar)
           :style {:color     color
                   :backgroundColor "rgba(0,0,0,0)"
                   :width  24
                   :transform [{:scale scale}]}}))

(defn icon-square-size [nn]
  (Math.ceil (Math.sqrt nn)))

(defn col-blocks [nn]
  (min 3 (* (quot (icon-square-size nn) 5))))

(defn cols-rows-blocked [nn]
  (let [cols (* 5 (col-blocks nn))
        rows (Math.ceil (/ nn cols))
        cblocks (Math.ceil (/ cols 5))
        rblocks (Math.ceil (/ rows 5))]
    [cols rows cblocks rblocks]
    ))


(rum/defc page < rum/reactive []
  (let [scenar ((rum/react stories) (rum/react story-index))
        br (:baseline-risk scenar)
        rr (:relative-risk scenar)
        nn (number-needed rr br)
        [cols rows cblocks rblocks] (cols-rows-blocked nn)
        palette (get-palette (rum/react palette-index))
        page-style {:flex            1
                    :backgroundColor (:primary palette)}]

    (letfn [(draw-block [block count]
              #_(draw-icon scenar (:light-primary palette) 0.66)
              (for [r (range 5)]
                (view {:key   r
                       :style {:flex           1
                               :flexDirection  "row"
                               :alignItems     "center"
                               :justifyContent "space-between"
                               :maxHeight      20
                               }}
                      (for [c (range 5)
                            :let [k (+ c (* r 5))]
                            ]
                        (view {:key   c
                               :style {:flex           1
                                       :flexDirection "column"
                                       :maxWidth       20
                                       :alignItems     "center"
                                       :justifyContent "space-between"
                                       }}
                              (if (< k count)
                                (if (and (zero? k) (zero? block))
                                  (draw-icon scenar (:text-icons palette) 0.9)
                                  (draw-icon scenar (:light-primary palette) 0.5))
                                (view {:style {:width 20}})))))))]

      ;view {:style {:flex 1}}
      #_(status-bar {:key      10
                     :hidden   false
                     :barStyle "light-content"})

      (view {:style {:flex            1
                     :backgroundColor (:primary palette)}
             :key   1}
            (view {:style {:flex            0.3
                           :justifyContent  "center"
                           :alignItems      "center"
                           :backgroundColor (:dark-primary palette)}}
                  (text {:style {:color      (:light-primary palette)
                                 :fontWeight "400"
                                 :padding    20
                                 :fontSize   (:fontSize scenar)}}
                        (text-generator nn2 scenar)))
            (view {:key   2
                   :style {:flex            0.7
                           ;:padding         20
                           :backgroundColor (:primary palette)}}
                  (view {:key   1
                         :style {:position "absolute"
                                 :top      0
                                 :bottom   0
                                 :left     0
                                 :right    0
                                 :zIndex   1}}
                        (view {:style {:flex           1
                                       :flexDirection  "column"
                                       :justifyContent "center"
                                       :alignItems     "center"}}
                              (view {:style {:flex           1
                                             :flexDirection  "row"
                                             :justifyContent "center"
                                             :alignItems     "center"}}
                                    (text {:style {:fontSize        120
                                                   :color           (:light-primary palette)
                                                   :backgroundColor "rgba(0,0,0,0)"
                                                   :opacity         0.5
                                                   }
                                           }
                                          (str nn)))))
                  (view {:key   2
                         :style {:position        "absolute"
                                 :top             0
                                 :bottom          0
                                 :left            0
                                 :right           0
                                 :zIndex          0
                                 :backgroundColor (:primary palette)}}

                        (for [rb (range rblocks)]
                          (view {:key   rb
                                 :style {:flex           (/ 1 rblocks)
                                         :flexDirection  "row"
                                         :alignItems     "center"
                                         :justifyContent "space-around"
                                         }}
                                (for [cb (range cblocks)]
                                  (view {:key   cb
                                         :style {:flex           (/ 1 cblocks)
                                                 :flexDirection  "column"
                                                 :alignItems     "center"
                                                 :justifyContent "space-around"
                                                 }}
                                        (let [k (+ cb (* rb cblocks))]
                                          (draw-block k (- nn (* 25 k))))))))))))))


