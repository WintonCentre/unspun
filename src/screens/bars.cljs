(ns unspun.screens.bars
  (:require-macros [rum.core :refer [defc defcs]])
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight status-bar animated-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index baseline-risk relative-risk to-pc clamp]]
            [graphics.svg :refer [svg circle rect]]
            ))

(def header-height 23)
(def react-native (js/require "react-native"))
(def animated (aget react-native "Animated"))
(def animated-value (aget react-native "Animated" "Value"))
(def animated-timing (aget react-native "Animated" "timing"))
(def animated-spring (aget react-native "Animated" "spring"))
(def ease (aget react-native "Easing" "ease"))
(def ease-out (aget react-native "Easing" "out"))


(defn animate-function [key f initial-value]
  (letfn [(upd [state]
            (let [[_ value] (:rum/args state)]
              #_(.start (animated-spring (key state) #js {:toValue (f value) :friction 10 :tension 60}))
              (.start (animated-timing (key state) #js {:toValue (f value) :duration 200
                                                        :easing  (ease-out ease)}))
              state))]
    {:init        (fn [state props]
                    (assoc state key (new animated-value initial-value)))
     :did-mount   upd
     :will-update upd}))

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
                 (animate-function ::height #(- 1 %) 0.5)
                 "The top and bottom bars split the vertical flex space in the ratio (1-value) : value.
                 Both are animated views which animate height as a function of value.
                 Here we only allow 2 bars and so we can label them inside if there is space, or above if not.
                 The top bar colours are chosen to be the same as the background"
  [state palette value]
  (animated-view {:style {:flex            (::height state)
                          :backgroundColor (:primary palette)}}
                 (when (< value 0.1)
                   (inner-top-label palette value))))

(defcs bottom-bar < rum/static
                    (animate-function ::height identity 0.5)
  [state palette value]
  (animated-view {:style {:flex            (::height state)
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
      (status-bar {:hidden          false
                   :barStyle        "light-content"})

      ;(.log js/console (::height state))
      ;(.log js/console animated-value)
      (view {:style page-style}
            (view {:style {:flex            0.3
                           :backgroundColor (:dark-primary palette)}}
                  (text {:style {:color      (:light-primary palette)
                                 :fontWeight "400"
                                 :padding    20
                                 :paddingTop 40
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