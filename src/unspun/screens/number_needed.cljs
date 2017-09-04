(ns unspun.screens.number-needed
  (:require [cljs-exponent.components :refer [element text view image touchable-highlight status-bar scroll-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [shared.ui :refer [n-icon screen-w-h tffsz]]
            [unspun.db :refer [app-state dimensions palette-index to-pc number-needed stories story-index nn-text-vector anyway]]
            [unspun.screens.mixins :refer [monitor]]
            [unspun.screens.scenario-title-view :refer [scenario-title]]
            [unspun.navigation.bottom-nav :refer [story-links]]
            [shared.icons :refer [ionicon]]
            [rum.core :as rum]
            [cljs.pprint :refer [pp]]))

(defn draw-square
  [{:keys [size draw? key]
    :or   {size 100 draw? true key nil}}]
  (view {:key   key
         :style {:backgroundColor "orange"
                 :paddingTop      1
                 :aspectRatio     1
                 :opacity         (if draw? 1 0)
                 :flexDirection   "column"
                 :justifyContent  "center"
                 :alignItems      "center"
                 :width           size                      ;size
                 :borderRadius    (/ size 2)
                 }}
        (view
          {:style {:transform (clj->js [{:scale (/ size 100)}])}}
          (ionicon {:name  "ios-wine"                       ;"ios-radio-button-on"
                    :size  100
                    :style {:width           100
                            :textAlign       "center"
                            :color           "white"
                            :backgroundColor "rgba(0,0,0,0)"
                            }}))))



(def testn 3)

(def row0 [0 0 0 0 0])
(def row1 [0 0 1 0 0])
(def row2 [0 1 0 1 0])
(def row3 [1 0 1 0 1])
(def row4 [1 1 0 1 1])
(def row5 [1 1 1 1 1])

(def row10 "row of 10" (vec (concat row5 [0] row5)))
(def row15 "row of 15" (vec (concat row5 [0] row5 [0] row5)))
(def rown "access row0..row5 by index" [row0 row1 row2 row3 row4 row5])

(defn row-n-m*
  "Layout for row of length m with n icons showing"
  [n m k]
  (->> (range m)
       (map-indexed #(if (< %1 n) 1 0))
       (partition k)
       (interpose 0)
       (flatten)
       (into [])))

(defn row-n-m
  "Layout for row of length m with n icons showing"
  [n m]
  (->> (range m)
       (map-indexed #(if (< %1 n) 1 0))
       (partition 5)
       (interpose 0)
       (flatten)
       (into [])))

(defn block-n-k
  [n k]
  (->>                                                      ;(row-n-m* n 20 20)
    (range (* k (Math.ceil (/ n k))))
    (map-indexed #(if (< %1 n) 1 0))
    (partition-all k)
    (partition-all 2)
    (interpose (repeat k 0))
    (flatten)
    (partition-all k)
    (map (partial partition-all 5))
    (map (partial interpose [0]))
    (map flatten)
    (mapv vec)))

(defn blocks
  [n cols w h]
  (->>
    (range (* cols (Math.ceil (/ n cols))))
    (map-indexed #(if (< %1 n) (inc %2) 0))
    (partition-all cols)
    (partition-all h)
    (interpose (repeat cols 0))
    (flatten)
    (partition-all cols)
    (map (partial partition-all w))
    (map (partial interpose [0]))
    (map flatten)
    (mapv vec)))

(defn blocks*
  "Create a layout for n icons, in cols columns, organised into blocks of w width and h height. Blocks are separated
  by rows or columns of zeroes. Full cells are indicated by non-sero entries (mostly 1s)."
  [n cols w h]
  (let [blocks-per-row (Math.ceil (/ cols w))
        block-size (* w h)
        full-block-count (quot n block-size)
        remainder (rem n block-size)
        row-blocks-capacity (* cols h)
        rows-of-full-blocks (quot n row-blocks-capacity)
        partial-row-full-blocks (rem n row-blocks-capacity)
        full-blocks-in-partial-row (quot partial-row-full-blocks block-size)
        empty-row (->> (repeat cols 0)
                       (partition w)
                       (interpose 0)
                       (flatten))
        full-row-of-blocks (->> (repeat cols 1)
                                (partition w)
                                (interpose 0)
                                (flatten)
                                (repeat h)
                                (repeat rows-of-full-blocks)
                                (interpose empty-row))
        last-block-count (- n (* rows-of-full-blocks row-blocks-capacity) (* full-blocks-in-partial-row block-size))]
    [rows-of-full-blocks
     full-row-of-blocks
     "Unfinished"]

    )
  )

(comment
  (blocks 500 20 5 5)
  (blocks* 500 20 5 5)
  )

(comment
  (blocks 33 10 5 2)
  (blocks* 33 10 5 2)

  (row-n-m 3 5)
  ; => [1 1 1 0 0]
  (block-n-k 3 5)
  ;=> [[1 1 1 0 0]]
  (block-n-k 13 5)
  ;=> [[1 1 1 1 1] [1 1 1 1 1] [0 0 0 0 0] [1 1 1 0 0]]
  (block-n-k 13 10)
  ;=> [[1 1 1 1 1 0 1 1 1 1 1] [1 1 1 0 0 0 0 0 0 0 0]]
  (block-n-k 43 15)
  ;=>
  #_[[1 1 1 1 1 0 1 1 1 1 1 0 1 1 1 1 1]
     [1 1 1 1 1 0 1 1 1 1 1 0 1 1 1 1 1]
     [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
     [1 1 1 1 1 0 1 1 1 1 1 0 1 1 1 0 0]]
  )

(def dice {1 [[1]]
           2 [[1 0]
              [0 2]]
           3 [[1 2]
              [0 3]]
           4 [[1 2]
              [3 4]]
           5 [[1 0 2]
              [0 3 0]
              [4 0 5]]
           6 [[1 2 3]
              nil
              [4 5 6]]
           7 [[1 2 0]
              [3 4 5]
              [0 6 7]]
           8 [[1 2 3]
              [4 0 5]
              [6 7 8]]
           9 [[1 2 3]
              [4 5 6]
              [7 8 9]]
           })

(defn dicen
  "layout n icons in an easy to count manner (dice-like for small n).
  Full cells are represented by a non-zero value. Positive for a normal
  cell, and negative for a highlighted cell. A cell is highlighted if its
  index (in 0..(n-1)) is in the highlight set."
  [n highlight]
  (mapv
    #(mapv (fn [k] (if (highlight (dec k)) (- k) k)) %)
    (cond
      (< n 10) (get dice n)
      (<= n 20) (blocks n 5 5 2)
      (<= n 100) (blocks n 10 5 2)
      :else (blocks n 10 10 10))))

(comment
  (dicen 5 #{0 1 3})
  => [[-1 0 -2] [0 3 0] [-4 0 5]]
  )

(defn icon-top
  "Determine vertical position of icons, taking account of their size a, the layout for the number n,
  and the row number i"
  [i a n]
  (- (* i a) (cond
               (= n 6)
               (condp = i
                 0 (- (/ a 3))
                 1 0
                 2 (* 2 (/ a 3))
                 3 0)
               :else 0)))

(defn ffloyd-sample
  "Pick n random different integers from 0..nn"
  [nn n]
  (let [count (inc nn)]
    (loop [i (- count n)
           res #{}]
      (if (< i count)
        (let [j (inc i)
              k (rand-int j)]
          (if (res k)
            (recur j (conj res i))
            (recur j (conj res k))))
        res))))
(comment
  (ffloyd-sample 10 3)
  ; => #{7 1 8} for example
  (ffloyd-sample 10 9)
  ; => #{0 1 2 5 6 7 8 9 10} for example
  )

(rum/defc draw-icon
  [{:keys [scenar size draw? key color back]
    :or   {size 100 draw? true key nil color "white" back "black"}}]
  "draw a icon selected by scenario."
  (view {:key   key
         :style {:backgroundColor back
                 :paddingTop      1
                 :aspectRatio     1
                 :opacity         (if draw? 1 0)
                 :flexDirection   "column"
                 :justifyContent  "center"
                 :alignItems      "center"
                 :width           size                      ;size
                 :borderRadius    (/ size 2)
                 }}
        (view
          {:style {:transform (clj->js [{:scale (/ size 100)}])}}
          (ionicon {:name  (:icon scenar)                   ;"ios-radio-button-on"
                    :size  100
                    :style {:width           100
                            :textAlign       "center"
                            :color           color
                            :backgroundColor "rgba(0,0,0,0)"
                            }})))
  )


(rum/defc nested-n-square** < rum/static
  [scenar palette rr w h n highlight]
  (let [w (min w h)
        padding 20
        cols (count ((dicen n highlight) 0))
        rows (count (dicen n highlight))
        a (/ (- w (* 2 padding)) cols)
        ]

    (view {:style {:flex          1
                   :flexDirection "column"
                   :alignItems    "center"
                   :opacity       0.8}}
          (view {:style {:flex    0
                         :width   w
                         :height  (+ (* 2 padding) (icon-top (count (dicen n highlight)) a n))
                         :padding padding
                         ;:transform [{:scale (/ h (+ (* 2 padding) (icon-top (count (dicen n highlight)) a n)))}]
                         }}
                (for [[i row] (zipmap (range) (dicen n highlight))]
                  (view {:key   (str "r" i)
                         :style {:flex     0
                                 :position "relative"}}
                        (when row
                          (for [[j col] (zipmap (range) row)]
                            (view {:key   (str "c" j)
                                   :style {:position "absolute"
                                           :top      (icon-top i a n)
                                           :left     (* j a)}}
                                  (draw-icon {:scenar scenar
                                              :size   a
                                              :draw?  (not (zero? col))
                                              :back   (if (and (zero? i) (zero? j))
                                                        ((if (> rr 1) :accent :text-icons) palette)
                                                        "rgba(0,0,0,0)")
                                              :color  (if (and (zero? i) (zero? j))
                                                        ((if (> rr 1) :text-icons :dark-primary) palette)
                                                        (if (pos? col)
                                                          (:light-primary palette)
                                                          (:accent palette)
                                                          )) ; (if (highlight (+ j (* i rows))) "black" "white")
                                              }))))))))))


(rum/defc page < rum/reactive []

  (let [scenar ((rum/react stories) (rum/react story-index))
        palette (get-palette (rum/react palette-index))
        br (:baseline-risk scenar)
        rr (:relative-risk scenar)
        nn (number-needed rr br)
        ; we don't want to highlight the first icon - as that is already allocated to be the 'one'.
        highlight (into #{} (map inc (ffloyd-sample (- nn 2)
                                                    (max 0 ((if (> rr 1) identity dec) (anyway rr br))))))
        {w :width h :height} (rum/react dimensions)
        h (- h 168)                                         ; adjustment for existing header and footer
        isz 16
        row-n (js/Math.floor (/ w isz))
        col-n (js/Math.floor (/ h isz))
        dw (- w (* row-n isz))
        dh (- h (* col-n isz))

        [nn-head nn-one nn-one-to-group nn-group nn-group-to-anyway nn-anyway nn-tail :as texts] (nn-text-vector scenar)

        text-field (fn [palette-key weight content]
                     (text {:key   (gensym "text-field")
                            :style {:color      (palette-key palette)
                                    :fontWeight weight
                                    }} content))

        padding 10
        cols (count ((dicen nn highlight) 0))
        rows (count (dicen nn highlight))
        a (/ (- w (* 2 padding)) cols)
        ]
    (println "nn-rerender")
    (letfn [(handle-scroll [event]
              (this-as this
                (.log js/console (-> event (.-nativeEvent) (.-contentOffset) (.-y)))
                ))]


      (view {:style {:flex            1
                     :flexDirection   "column"
                     :justifyContent  "flex-start"
                     :backgroundColor (:primary palette)
                     }}
            (view {:style {:flex            0.3
                           :justifyContent  "center"
                           :alignItems      "stretch"
                           :backgroundColor (:dark-primary palette)}}
                  (scroll-view {:style           {:flex 0.7}
                                :key             1
                                :backgroundColor (:dark-primary palette)
                                }
                               (scenario-title (:title scenar) text-field (:qoe scenar))
                               (text {:style {:padding  20
                                              :fontSize tffsz}}
                                     (text-field :light-primary "normal" nn-head)
                                     (text-field (if (> rr 1) :accent :text-icons) "bold" nn-one)
                                     (text-field :light-primary "normal" nn-one-to-group)
                                     (text-field :light-primary "bold" nn-group)
                                     (text-field :light-primary "normal" nn-group-to-anyway)
                                     (text-field :accent "bold" nn-anyway)
                                     (text-field :light-primary "normal" nn-tail)))
                  (view {:style {:flex 0.3}}
                        (story-links palette))
                  )

            (view {:style {:flex          0.7
                           :flexDirection "column"}}

                  (view {:key   1
                         :style {:position "absolute"
                                 :top      0
                                 :bottom   0
                                 :left     0
                                 :right    0
                                 :zIndex   1
                                 }}
                        (scroll-view {;:onScroll            handle-scroll
                                      :scrollEventThrottle 16
                                      :key                 1
                                      :style               {:flex          1
                                                            :flexDirection "column"
                                                            :opacity       1}}

                                     (nested-n-square** scenar palette rr w h nn highlight)))

                  (view {:key   2
                         :style {:position "absolute"
                                 :top      0
                                 :bottom   0
                                 :left     0
                                 :right    0
                                 :zIndex   0}}
                        (view {:style {:flex           1
                                       :flexDirection  "column"
                                       :justifyContent "center"
                                       :alignItems     "center"}}
                              (view {:style {:flex           1
                                             :flexDirection  "row"
                                             :justifyContent "center"
                                             :alignItems     "center"}}
                                    (text {:style {:fontSize        (* tffsz 10)
                                                   :color           (:light-primary palette)
                                                   :backgroundColor "rgba(0,0,0,0)"
                                                   :opacity         0.7
                                                   }
                                           }
                                          (str nn)))))

                  )))))


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
                                   :fontSize tffsz}}
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
                                      (text {:style {:fontSize        (* tffsz 10)
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

  (defn ffloyd-sample
    "Pick n random integers in 0..nn"
    [nn n]
    (let [count (inc nn)]
      (loop [i (- count n)
             res #{}]
        (if (< i count)
          (let [j (inc i)
                k (rand-int j)]
            (if (res k)
              (recur j (conj res i))
              (recur j (conj res k))))
          res))))

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