(ns unspun.screens.number-needed
  (:require [cljs-exponent.components :refer [element text view image touchable-highlight status-bar] :as rn]
            [themes.palettes :refer [get-palette]]
            [shared.ui :refer [n-icon]]
            [unspun.db :refer [app-state palette-index to-pc number-needed stories story-index story-icon text-generator nn1 nn2]]
            [rum.core :as rum]))


(defn draw-icon [scenar color scale]
  (n-icon {:name  (story-icon scenar)
           :style {:color     color
                   :transform [{:scale scale}]}}))

(defn icon-square-size [nn]
  (Math.ceil (Math.sqrt nn)))

(defn col-blocks [nn]
  (* (quot (icon-square-size nn) 5)))

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
        grid-size (icon-square-size nn)
        grid-factor (/ 1 grid-size)
        [cols rows cblocks rblocks] (cols-rows-blocked nn)


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
                            (text-generator nn2 scenar)))
                (view {:key   2
                       :style {:flex            0.7
                               :padding         20
                               :backgroundColor (:primary palette)}}
                      ;;
                      ;; start of better blocking code
                      ;;
                      #_(map
                        (fn [rb]
                          (view {:key   rb
                                 :style {:flex           (/ 1 rblocks)
                                         :flexDirection  "row"
                                         :alignItems     "center"
                                         :justifyContent "center"
                                         :marginBottom   (if (= rb (dec rblocks)) 0 10)}}
                                (map
                                  (fn [cb]
                                    (view {:key   cb
                                           :style {:flex           (/ 1 cblocks)
                                                   :alignItems     "center"
                                                   :justifyContent "center"
                                                   :marginRight    (if (= cb (dec cblocks)) 0 10)}}
                                          (view {:style {
                                                         :width       20
                                                         :height      20
                                                         :borderWidth 1
                                                         :borderColor "white"}}
                                                ))) (range cblocks)
                                  ))) (range rblocks))

                      (map (fn [m] (view {:key   m
                                            :style {:flex           grid-factor
                                                    :flexDirection  "row"
                                                    :alignItems     "center"
                                                    :justifyContent "center"
                                                    :marginBottom   (if (zero? (mod (inc m) 5)) 10 0)}}
                                           (map (fn [n] (view {:key   n
                                                               :style {:flex           grid-factor
                                                                       :alignItems     "center"
                                                                       :justifyContent "center"
                                                                       :marginRight    (if (zero? (mod (inc n) 5)) 10 0)}}
                                                              (let [k (+ n (* m cols))]
                                                                (cond
                                                                  (and (> k 0) (< k nn)) (draw-icon scenar (:light-primary palette) 0.66)
                                                                  (= k 0) (draw-icon scenar (:text-icons palette) 1.2)
                                                                  :else nil)
                                                                )
                                                              )
                                                  ) (range cols))
                                           )) (range rows))
                      )))))


