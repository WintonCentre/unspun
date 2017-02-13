(ns unspun.screens.number-needed
  (:require [cljs-exponent.components :refer [element text view image touchable-highlight status-bar scroll-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [shared.ui :refer [n-icon]]
            [unspun.db :refer [app-state palette-index to-pc number-needed stories story-index text-generator nn2 anyway]]
            [rum.core :as rum]))


(defn draw-icon [scenar color scale]
  (n-icon {:name  (:icon scenar)
           :style {:color           color
                   :backgroundColor "rgba(0,0,0,0)"
                   :width           24
                   :transform       [{:scale scale}]}}))

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

(defn picked-before? [a-set a-choice]
  (a-set a-choice))

(defn pick-n-in-nn
  "we can assume n << nn"
  ([nn n]
   (loop [picked-set (sorted-set)
          n-left n]
     (if (zero? n-left)
       picked-set
       (let
         [selection (inc (rand-int (dec nn)))]
         (if (picked-before? picked-set selection)
           (recur picked-set n-left)
           (recur (conj picked-set selection) (dec n-left))))))))

(rum/defc page < rum/reactive []
  (let [scenar ((rum/react stories) (rum/react story-index))
        br (:baseline-risk scenar)
        rr (:relative-risk scenar)
        nn (number-needed rr br)
        [cols rows cblocks rblocks] (cols-rows-blocked nn)
        palette (get-palette (rum/react palette-index))
        page-style {:flex            1
                    :backgroundColor (:primary palette)}
        block-width 5
        block-height 5
        highlight (pick-n-in-nn nn (max 0 ((if (> rr 1) identity dec) (anyway rr br))))
        ptext (fn [colour-key] (partial text {:style {:color      (colour-key palette)
                                                      :fontWeight (if (= colour-key :light-primary) "normal" "bold")
                                                      :padding    20
                                                      :fontSize   (:fontSize scenar)
                                                      }}))
        [s1 s2 s3 s4 s5] (rest (re-find #"(.*)<mark-one>(.*)</mark-one>(.*)<mark-anyway>(.*)</mark-anyway>(.*)" (text-generator nn2 scenar)))
        ]

    (letfn [(draw-block [block count]

              (for [r (range block-height)]
                (view {:key   r
                       :style {:flex           1
                               :flexDirection  "row"
                               :alignItems     "center"
                               :justifyContent "space-between"
                               :maxHeight      20
                               }}
                      (for [c (range block-width)
                            :let [k (+ c (* r 5))]
                            ]
                        (view {:key   c
                               :style {:flex           1
                                       :flexDirection  "column"
                                       :maxWidth       20
                                       :alignItems     "center"
                                       :justifyContent "space-between"
                                       }}
                              (if (< k count)
                                (if (and (zero? k) (zero? block))
                                  (draw-icon scenar ((if (> rr 1) :accent :text-icons) palette) 0.9)
                                  (draw-icon scenar (if (highlight (+ k (* block-width block-height block)))
                                                      (:accent palette)
                                                      (:light-primary palette)) 0.5))
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
                                 :fontSize   (:fontSize scenar)
                                 }}
                        ((ptext :light-primary) s1)
                        ((ptext (if (> rr 1) :accent :text-icons)) s2)
                        ((ptext :light-primary) s3)
                        ((ptext :accent) s4)
                        ((ptext :light-primary) s5)
                        ;(map (ptext :light-accent) nn-split)
                        ))
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


