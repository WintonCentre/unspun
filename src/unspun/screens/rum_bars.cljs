(ns unspun.screens.rum-bars
  (:require-macros [rum.core :refer [defc defcs]])
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar animated-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index baseline-risk relative-risk to-pc clamp]]
            [graphics.svg :refer [svg circle rect]]
            ))


#_[icon-button {:name             "facebook"
                :width            184.5
                :background-color "#3b5998"
                :on-press         (fn []
                                    (login/login-with-facebook))}
   "Sign in with Facebook"]
;(def wrap-svg (partial aget exponent "Components" "Svg"))
;
;(defn wrap-svg-component [name]
;  (partial element (wrap-svg name)))
;
;(def svg (partial element (wrap-svg)))
;(def circle (wrap-svg-component "Circle"))


;; vector-icons
(def vector-icons (js/require "@exponent/vector-icons"))
(def Ionicons (aget vector-icons "Ionicons"))

(defn ionicon [attrs] (.createElement js/React Ionicons attrs))

(def ac-unit (ionicon (clj->js {:name  "md-checkmark-circle"
                                :size  30
                                :style {:transform [{:rotate "90deg"} {:scale 0.8}]}
                                :color "white"})))


#_(def checkmark (ion-icons {:name  "md-checkmark-circle"
                             :size  32
                             :color "green"}))


;(def MaterialIcons (js/require "@exponent/vector-icons/MaterialIcons"))
;(defn material-icon-class [name] (aget MaterialIcons "default" name))
;(defn material-icon [name] (partial element (material-icon-class name)))
#_(def material-icons (aget vector-icons "MaterialIcons"))


;(def FontAwesome (js/require "@exponent/vector-icons/FontAwesome"))

;(def icon (r/adapt-react-class (aget FontAwesome "default")))
;(def FontAwesomeButton (aget FontAwesome "default" "Button"))
;(def icon-button (r/adapt-react-class FontAwesomeButton))

;(def MaterialIcons (js/require "@exponent/vector-icons/MaterialIcons"))


#_(defn m-icon [name]
    (partial element (aget m name)))


#_(def MaterialIconButton (aget MaterialIcons "default" "Button"))
;(def material-icon-button (r/adapt-react-ass MaterialIconButton))

#_(comment
    (def wrap-material (partial aget MaterialIcons))

    (defn wrap-svg-component [name]
      (partial element (wrap-svg name)))

    (def svg (partial element (wrap-svg)))
    (def circle (wrap-svg-component "Circle")))


(def header-height 23)
(def react-native (js/require "react-native"))

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

#_(comment
    (defn log-val [key ref old-state new-state]
      (prn (:val new-state)))

    (def foo (atom {:val 0}))

    (add-watch foo :goo log-val)

    (animate-to-new-value! foo easeOutQuad 50 1000 :val 20)

    (remove-watch foo :goo))



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
        ac-unit
        (text {:style {:color      (text-color palette)
                       :fontSize   font-size
                       :flex       1
                       :textAlign  "center"
                       :fontWeight font-weight}}
              (formatter value))))

(defn percentage [value]
  (str (to-pc value) "%"))

(def inner-top-label (partial bar-value-label {:font-size   26
                                               :font-weight "400"
                                               :text-color  :text-icons
                                               :on-edge     :bottom
                                               :formatter   percentage}))

(def outer-bottom-label (partial bar-value-label {:font-size   26
                                                  :font-weight "400"
                                                  :text-color  :dark-primary
                                                  :on-edge     :top
                                                  :formatter   percentage}))

(def error-label (partial bar-value-label {:font-size   26
                                           :font-weight "400"
                                           :text-color  :error
                                           :on-edge     :top
                                           :formatter   percentage}))
(defcs top-bar < rum/static
                 "The top and bottom bars split the vertical flex space in the ratio (1-value) : value.
                 Both are animated views which animate height as a function of value.
                 Here we only allow 2 bars and so we can label them inside if there is space, or above if not.
                 The top bar colours are chosen to be the same as the background"
  [state palette value]
  (view {:style {:flex            (- 1 value)
                 :backgroundColor (:primary palette)}}
        (when (< value 0.1)
          (inner-top-label palette value))))

(defcs bottom-bar < rum/static
  [state palette value]
  (view {:style {:flex            value
                 :backgroundColor (:light-primary palette)}}
        (when (>= value 0.1)
          (if (> value 1)
            (error-label palette 1)
            (outer-bottom-label palette value)))))

(defcs labelled-vertical-bar < rum/static
  [state palette value]
  (view {:style {:flex 1}}
        (top-bar palette value)
        (bottom-bar palette value)
        ))

(defcs page < rum/reactive [state]
  (let [br (rum/react baseline-risk)
        rr (rum/react relative-risk)
        brpc (to-pc br)
        er (* br rr)
        erpc (to-pc (clamp 0 1 er))
        palette (get-palette (rum/react palette-index))
        page-style {:flex            1
                    :backgroundColor (:primary palette)}]
    (view
      {:style {:flex 1}}
      (status-bar {:hidden   false
                   :barStyle "light-content"})

      (view {:style page-style}
            (view {:style {:flex            0.3
                           :justifyContent  "center"
                           :alignItems      "center"
                           :backgroundColor (:dark-primary palette)}}

                  (text {:style {:color      (:light-primary palette)
                                 :fontWeight "400"
                                 :padding    20
                                 ;:paddingTop 40
                                 :fontSize   24}}
                        (str "Without bacon sandwiches, the risk of heart attack or stroke is "
                             brpc
                             "%, "
                             (if (< brpc erpc) "increasing" "decreasing")
                             " to "
                             erpc
                             "% with bacon sandwiches"
                             )))
            (view {:style {:flex          0.6
                           :flexDirection "row"}}
                  (view {:style {:flex           0.3
                                 :justifyContent "center"}}
                        (text {:style {:color        (:text-icons palette)
                                       :fontSize     24
                                       :textAlign    "right"
                                       :paddingRight 10}}
                              "Without"))
                  (view {:style {:flex 0.2}} (labelled-vertical-bar palette br))
                  (view {:style {:flex 0.04}})
                  (view {:style {:flex 0.2}} (labelled-vertical-bar palette er))
                  (view {:style {:flex           0.3
                                 :justifyContent "center"}}
                        (text {:style {:color     (:text-icons palette)
                                       :fontSize  24
                                       :padding   10
                                       :textAlign "left"}}
                              "With")))
            (view {:style {:flex            0.1
                           :backgroundColor (:dark-primary palette)}})))))