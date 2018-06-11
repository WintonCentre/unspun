(ns unspun.screens.rum-bars
  (:require-macros [rum.core :refer [defc defcs]])
  (:require [rum.core :as rum]
            [shared.ui :refer [font-scale pixel-ratio get-dimensions tffsz ios? button txt rn-button]]
            [shared.icons :refer [ionicon]]
            [cljs-exponent.components :refer [element text view image status-bar animated-view scroll-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index stories story-index compare-text-vector to-pc clamp two-sf dimensions]]
            [unspun.navigation.bottom-nav :refer [story-links]]
            [graphics.scales :refer [create-linear-scale bounded-ticks i->o o->i tick-format-specifier]]
            [unspun.gesture-responders :refer [pan-responder-mixin pan-logger]]
            [cljs.pprint :refer [cl-format]]
            [unspun.screens.scenario-title-view :refer [scenario-title]]
            [unspun.screens.mixins :refer [monitor]]
            ))

;; vector-icons
(def vector-icons (js/require "@expo/vector-icons"))



(def ac-unit (ionicon (clj->js {:name  "md-checkmark-circle"
                                :size  30
                                :style {:transform [{:rotate "90deg"} {:scale 0.8}]}
                                :color "white"})))

(def header-height 23)
;(def react-native (js/require "react-native"))

(defn easeOutQuad
  [elapsed-t duration]
  (let [dt (/ elapsed-t duration)]
    (* dt (- 2 dt))))

(defn animate-to-new-value! [state easing interval duration key new-value]
  (let [anim-key (keyword (str (name key) "-anim"))
        initial-value (key @state)]
    (letfn [(tick [_]
              (let [a-map (anim-key @state)
                    t (- (.now js/Date) (::t0 a-map))]
                (if (< t duration)
                  (let [progress (easing t duration)
                        new-val (+ (::initial-value a-map) (* progress (::delta a-map)))]
                    (swap! state assoc key new-val))
                  (do
                    (js/clearInterval (::ticker a-map))
                    (swap! state dissoc anim-key)
                    (swap! state assoc key new-value)
                    ))))]
      (swap! state assoc anim-key {::t0            (.now js/Date.)
                                   ::ticker        (js/setInterval tick interval)
                                   ::delta         (- new-value initial-value)
                                   ::initial-value initial-value}))))

(defc header < rum/reactive []
  (let [palette (get-palette (rum/react palette-index))
        header-style {:height          header-height
                      :backgroundColor (:dark-primary palette)}]
    (view {:style header-style})))


(defn bar-value-label [{:keys [font-size font-weight text-color on-edge formatter]} palette value]
  (view {:style {:flex           1
                 :position       "absolute"
                 on-edge         0
                 :flexDirection  "row"
                 :justifyContent "center"}}
        (text {:style {:color      (text-color palette)
                       :fontSize   font-size
                       :flex       1
                       :textAlign  "center"
                       :fontWeight font-weight}}
              (formatter value))))

#_(defn percentage [value]
    (str (.toPrecision (js/Number. (* 100 value)) 2) "%"))

(defn percentage [value]
  (str (two-sf (* 100 value)) "%"))

(def inner-top-label (partial bar-value-label {:font-size   (/ tffsz 0.9)
                                               :font-weight "400"
                                               :text-color  :text-icons
                                               :on-edge     :bottom
                                               :formatter   percentage}))

(def outer-bottom-label (partial bar-value-label {:font-size   (/ tffsz 0.6)
                                                  :font-weight "400"
                                                  :text-color  :dark-primary
                                                  :on-edge     :top
                                                  :formatter   percentage}))

(def error-label (partial bar-value-label {:font-size   (/ tffsz 0.6)
                                           :font-weight "400"
                                           :text-color  :error
                                           :on-edge     :top
                                           :formatter   percentage}))
(defc top-bar < rum/static
                "The top and bottom bars split the vertical flex space in the ratio (1-value) : value.
                Both are animated views which animate height as a function of value.
                Here we only allow 2 bars and so we can label them inside if there is space, or above if not.
                The top bar colours are chosen to be the same as the background"
  [palette value scale]
  (view {:style {:flex            (- 1 (* scale value))
                 :backgroundColor (:primary palette)}}
        (when (< value 0.9)
          (inner-top-label palette value))))

(defc bottom-bar < rum/static
  [palette value scale]
  (view {:style {:flex            (* scale value)
                 :backgroundColor (:light-primary palette)}}
        (when (>= value 0.9)
          (if (> value 1)
            (error-label palette 1)
            (outer-bottom-label palette value scale)))))

(defc labelled-vertical-bar < rum/static
  [palette value scale]
  (view {:style {:flex 1}}
        (top-bar palette value scale)
        (bottom-bar palette value scale)
        ))

(defcs page < rum/reactive
              (rum/local 1 :scale)
              (rum/local 1 :scale0)
              (rum/local false :zooming)
              (rum/local nil :rescaler)
              (pan-responder-mixin ::zoomer (:height (get-dimensions)))
  ([state]
   (let [scenar ((rum/react stories) (rum/react story-index))
         db (rum/react app-state)                           ;unused?
         br (:baseline-risk scenar)
         rr (:relative-risk scenar)
         brpc (to-pc br)
         er (* br rr)
         erpc (to-pc (clamp [0 1] er))
         palette (get-palette (rum/react palette-index))
         page-style {:flex            1
                     :backgroundColor (:primary palette)}
         {w :width h :height} (rum/react dimensions)
         portrait #(> h w)

         ;scale 1.9
         ub (/ 100 (rum/react (:scale state)))
         zooming (rum/react (:zooming state))
         axis-scale (create-linear-scale [0 ub] [0 1] 4)
         ticks (bounded-ticks axis-scale)
         [cmp-head cmp-brpc cmp-brpc-to-change cmp-change cmp-change-to-erpc cmp-erpc] (compare-text-vector scenar)
         text-field (fn [palette-key weight content]
                      (text {:key   (gensym "text-field")
                             :style {:color      (palette-key palette)
                                     :fontWeight weight
                                     }} content))
         ]

     (view {:style (merge page-style {:flex           1
                                      :justifyContent "flex-start"
                                      :flexDirection  (if (portrait) "column" "row")})}

           (view {:key   1
                  :style {:flex            (if (> h w) 0.4 0.5)
                          :position        "relative"
                          :justifyContent  "flex-start"
                          :alignItems      "stretch"
                          :backgroundColor (:dark-primary palette)
                          }}
                 (story-links palette)

                 (view {:key   1
                        :style {:position        "absolute"
                                :backgroundColor "rgba(0,0,0,0)"
                                :top             (* 3 tffsz)
                                :bottom          0
                                :left            0
                                :right           0
                                :zIndex          1}}
                       (scroll-view {:style {:backgroundColor (:dark-primary palette)
                                             :flex            1}}
                                    (scenario-title (:title scenar) text-field (:qoe scenar))
                                    (text {:style {:padding    20
                                                   :paddingTop 5
                                                   :fontSize   tffsz}}
                                          (text-field :light-primary "normal" cmp-head)
                                          (text-field :text-icons "bold" cmp-brpc)
                                          (text-field :light-primary "normal" cmp-brpc-to-change)
                                          (text-field :light-primary "bold" cmp-change)
                                          (text-field :light-primary "normal" cmp-change-to-erpc)
                                          (text-field :text-icons "bold" cmp-erpc))
                                    )
                       )
                 (view {:key   2
                        :style {:position "absolute"
                                :height   (* 3 tffsz)
                                :bottom   (* 0.2 tffsz)
                                :left     0
                                :right    0
                                :zIndex   2}}
                       (view {:style          {:flex 1}
                              :flexDirection  "column"
                              :justifyContent "flex-end"
                              :alignItems     "center"
                              }

                             (ionicon {:name  "ios-arrow-down"
                                       :size  50
                                       :style {:width           50
                                               :textAlign       "center"
                                               :opacity         0.3
                                               :color           (:text-icons palette)
                                               :backgroundColor "rgba(0,0,0,0)"
                                               }}))
                       )

                 )

           (view {:key   2
                  :style {:flex (if (portrait) 0.6 0.5)}}
                 ;;;
                 ;; top and bottom buttons
                 ;;;
                 (view (merge {:key   1
                               :style {:position       "absolute"
                                       :top            0
                                       :bottom         0
                                       :left           0
                                       :right          0
                                       :zIndex         2
                                       :flex           1
                                       :alignItems     "center"
                                       :justifyContent "space-between"}}
                              (js->clj (.-panHandlers (::zoomer state)))))
                 ;;;
                 ;; ticks
                 ;;;
                 (view {:key   2
                        :style {:position       "absolute"
                                :top            0
                                :bottom         0
                                :left           0
                                :right          0
                                :flex           0.9
                                :alignItems     "stretch"
                                :justifyContent "space-between"
                                :zIndex         1
                                }
                        }
                       (view {:key   2
                              :style {:flex 1}}
                             (for [[y1 y0] (partition 2 1 (reverse (map (i->o axis-scale) ticks)))]
                               (view {:key   (rand-int 100000)
                                      :style {:flex          (- y1 y0)
                                              :flexDirection "row"
                                              :alignItems    "flex-start"
                                              }}
                                     #_(println axis-scale
                                              (tick-format-specifier axis-scale)
                                              (str (cl-format nil (tick-format-specifier axis-scale)
                                                              (let [y ((o->i axis-scale) y1)]
                                                                (if (> y 1) (Math.round y) y))
                                                              ) "%"))
                                     (text {:key   1
                                            :style {:flex            0.1
                                                    :color           (:light-primary palette)
                                                    :backgroundColor "rgba(0,0,0,0)"
                                                    :fontSize        (/ tffsz 1.4)
                                                    :textAlign       "center"}}
                                           (str (cl-format nil (tick-format-specifier axis-scale)
                                                           (let [y ((o->i axis-scale) y1)]
                                                             (if (> y 1) (Math.round y) y))
                                                           ) "%"))
                                     (view {:key   1.5
                                            :style {:flex           0.8
                                                    :height         1
                                                    :alignItems     "flex-start"
                                                    :borderTopColor (:light-primary palette)
                                                    :borderTopWidth 1}})
                                     (text {:key   2
                                            :style {:flex            0.1
                                                    :color           (:light-primary palette)
                                                    :backgroundColor "rgba(0,0,0,0)"
                                                    :fontSize        (/ tffsz 1.4)
                                                    :textAlign       "center"}}
                                           (str (cl-format nil (tick-format-specifier axis-scale)
                                                           (let [y ((o->i axis-scale) y1)]
                                                             (if (> y 1) (Math.round y) y))) "%"))))))
                 ;;;
                 ;; bars
                 ;;;
                 (view {:key   3
                        :style {:position "absolute"
                                :top      0
                                :bottom   0
                                :left     0
                                :right    0
                                :flex     1
                                :zIndex   0
                                }}
                       (view {:key   2
                              :style {:flex              1
                                      :flexDirection     "row"
                                      :borderBottomWidth 1
                                      :borderTopWidth    1
                                      :borderBottomColor (:light-primary palette)
                                      :borderTopColor    (:light-primary palette)
                                      }
                              }
                             (view {:key   1
                                    :style {:flex           0.3
                                            :justifyContent "center"}}
                                   (text {:style {:color        (:text-icons palette)
                                                  :fontSize     (/ tffsz 1)
                                                  :textAlign    "right"
                                                  :paddingRight 10}}
                                         (:without-label scenar)))
                             (view {:key   2
                                    :style {:flex 0.2}}
                                   (labelled-vertical-bar palette br (rum/react (:scale state))))
                             (view {:key   3
                                    :style {:flex 0.1}})
                             (view {:key   4
                                    :style {:flex 0.2}}
                                   (labelled-vertical-bar palette er (rum/react (:scale state))))
                             (view {:key   5
                                    :style {:flex           0.3
                                            :justifyContent "center"}}
                                   (text {:style {:color     (:text-icons palette)
                                                  :fontSize  (/ tffsz 1)
                                                  :padding   10
                                                  :textAlign "left"}}
                                         (:with-label scenar)))))

                 (view {:key   4
                        :style {:position "absolute"
                                :top      0
                                :bottom   0
                                :left     0
                                :right    0
                                :flex     1
                                :zIndex   1
                                :opacity  (if zooming 1 0.3)
                                }}
                       (view {:style {:flex           1
                                      :flexDirection  "column"
                                      :justifyContent "flex-start"
                                      :alignItems     "center"}}
                             (ionicon (clj->js {:name  "ios-arrow-up"
                                                :size  50
                                                :style {:backgroundColor "rgba(0,0,0,0)"
                                                        :color           (:text-icons palette)}})))))))))