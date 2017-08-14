(ns unspun.screens.number-needed
  (:require [cljs-exponent.components :refer [element text view image touchable-highlight status-bar scroll-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [shared.ui :refer [n-icon screen-width screen-w-h text-field-font-size]]
            [unspun.db :refer [app-state palette-index to-pc number-needed stories story-index nn-text-vector anyway]]
            [unspun.screens.mixins :refer [monitor]]
            [shared.icons :refer [ionicon]]
            [rum.core :as rum]))




(defn draw-circle [scenar color size x y]
  "draw a icon selected by scenario."
  (ionicon {:name  "ios-disc"
            :size  size
            :color "green"
            :style {:position  "absolute"
                    :left      (+ x (/ size 2))
                    :top       (+ y (/ size 2))
                    }
            #_:style #_{:color           white
                        :backgroundColor "rgba(0,0,0,0)"
                        ;:flex            -1
                        :width           30                    ; the grid width - should be 30 for a packed icon whatever the scale
                        :transform       [{:translateX 0}        ;(* 15 scale kk)
                                          {:translateY 0}        ;(* 15 scale kk)
                                          {:scale 1}]}}))        ; the scale. higher = denser, but on same centres.


#_(defn draw-icon [scenar color scale kk]
    "draw a icon selected by scenario."
    (n-icon {:name  (:icon scenar)
             :style {:color           color
                     :backgroundColor "rgba(0,0,0,0)"
                     :width           30                    ;(* 30 scale kk)
                     :transform       [{:translateX 0}      ;(* 15 scale kk)
                                       {:scale (* scale kk)}]}}))


(rum/defc page < rum/reactive []
  (let [scenar ((rum/react stories) (rum/react story-index))
        br (:baseline-risk scenar)
        rr (:relative-risk scenar)
        nn (number-needed rr br)
        [w h] (screen-w-h)
        w (- w 0)
        h  (- h 168)                                        ; adjustment for existing header and footer
        isz 30
        row-n (dec (js/Math.floor (/ w isz)))
        col-n (dec (js/Math.floor (/ h isz)))
        dw (- w (* row-n isz))
        dh (- h (* col-n isz))]
    (prn [w h])
    (view {:style {:backgroundColor "black"
                   :borderWidth "1"
                   :borderColor "cyan"
                   :width           w
                   :height          h
                   :position "relative"
                   }}
          (for [i (range row-n)]
            (for [j (range col-n)]
              (draw-circle scenar "red" isz (+ 10 (* i isz)) (+ 10 (* j isz))))))))


#_(rum/defc page < rum/reactive []
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

          [nn-head nn-one nn-one-to-group nn-group nn-group-to-anyway nn-anyway nn-tail :as texts] (nn-text-vector scenar)

          text-field (fn [palette-key weight content]
                       (text {:key   (gensym "text-field")
                              :style {:color      (palette-key palette)
                                      :fontWeight weight
                                      }} content))
          [w h] (screen-w-h)
          sw (min w (* (- h 210) 0.9 0.7))
          kk (/ sw 500 (Math.pow (/ nn 225) 0.4))
          ]
      ;(prn (str "kk = " kk))
      (letfn [(draw-block [block count]

                (for [r (range block-height)]
                  (view {:key   r
                         :style {:flex           1
                                 :flexDirection  "row"
                                 :alignItems     "center"
                                 :justifyContent "space-between"
                                 :maxHeight      (* 20 kk)
                                 }}
                        (for [c (range block-width)
                              :let [k (+ c (* r 5))]
                              ]
                          (view {:key   c
                                 :style {:flex           1
                                         :flexDirection  "column"
                                         :maxWidth       (* 20 kk)
                                         :alignItems     "center"
                                         :justifyContent "space-between"
                                         }}
                                (if (< k count)
                                  (if (and (zero? k) (zero? block))
                                    (draw-circle scenar ((if (> rr 1) :accent :text-icons) palette) 0.9 kk)
                                    (draw-circle scenar (if (highlight (+ k (* block-width block-height block)))
                                                          (:accent palette)
                                                          (:light-primary palette)) 0.5 kk))
                                  (view {:style {:width (* 20 kk)}})))))))]


        (view {:style {:flex            1
                       :backgroundColor (:primary palette)}
               :key   1}
              (view {:style {:flex            0.3
                             :justifyContent  "center"
                             :alignItems      "center"
                             :backgroundColor (:dark-primary palette)}}
                    (text {:style {:padding  20
                                   :fontSize (text-field-font-size)}}
                          (text-field :light-primary "normal" nn-head)
                          (text-field (if (> rr 1) :accent :text-icons) "bold" nn-one)
                          (text-field :light-primary "normal" nn-one-to-group)
                          (text-field :light-primary "bold" nn-group)
                          (text-field :light-primary "normal" nn-group-to-anyway)
                          (text-field :accent "bold" nn-anyway)
                          (text-field :light-primary "normal" nn-tail)
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
                                      (text {:style {:fontSize        (* (text-field-font-size) 10)
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


(comment
  ; belongs above commented out block above
  (defn icon-square-size [nn]
    (Math.ceil (Math.sqrt nn)))

  (defn col-blocks [nn]
    (max 1 (Math.round (/ (icon-square-size nn) 5))))

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

  #_(rum/defc page < rum/reactive []
      (view {:style {:flex           1
                     :flexDirection  "row"
                     :alignItems     "center"
                     :justifyContent "space-between"
                     :maxHeight      100
                     }}))

  (comment
    ; testing for nn small
    (def br 0.269)
    (def rr 2.1)
    (def nn 3)
    (col-blocks 3)
    (cols-rows-blocked 3)
    (icon-square-size 3)
    )

  )