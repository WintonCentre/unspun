(ns unspun.components.bars
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


(defn animate-function [key f initial-value]
  (letfn [(upd [state]
            (let [[_ _ value] (:rum/args state)]
              (.log js/console value)
              (.start (animated-spring (key state) #js {:toValue (f value) :friction 10}))
              state))]
    {:init        (fn [state props]
                    (.log js/console (new animated-value initial-value))
                    ;state
                    (assoc state key (new animated-value initial-value)))
     :did-mount   upd
     :will-update upd}))

(defc header < rum/reactive []
  (let [palette (get-palette (rum/react palette-index))
        header-style {:height          header-height
                      :backgroundColor (:dark-primary palette)}]
    (view {:style header-style})
    ))


(def inner-label-options {:color-key    :text-icons
                          :position-key :bottom})

(def outer-label-options {:color-key    :light-primary
                          :position-key :top})

(defn bar-value-label [options palette formatter value]
  (view {:style {:flex                   1
                 :position               "absolute"
                 (:position-key options) 0
                 :flexDirection          "row"
                 :justifyContent         "center"}}
        (text {:style {:color      ((:color-key options) palette)
                       :fontSize   26
                       :flex       1
                       :textAlign  "center"
                       :fontWeight "400"}
               } (formatter value))))


(defcs top-bar < rum/static
                 (animate-function ::height #(- 1 %) 0.5)
  [state palette formatter value]
  (animated-view {:style {:flex            (::height state)
                 :backgroundColor (:primary palette)}}
        (when (< value 0.1)
          (bar-value-label {:color-key :text-icons :position-key :bottom} palette formatter value))))

(defcs bottom-bar < rum/static
                    (animate-function ::height identity 0.5)
  [state palette formatter value]
  (animated-view {:style {:flex            (::height state)
                           :backgroundColor (:light-primary palette)}}
        (when (>= value 0.1)
          (bar-value-label {:color-key :dark-primary :position-key :top} palette formatter value))))

(defcs labelled-vertical-bar < rum/static
  [state palette formatter value]
  (view {:style {:flex 1}}
        (top-bar palette formatter value)
        (bottom-bar palette formatter value)
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
                   :barStyle        "dark-content"
                   :backgroundColor "red"})
      (header)
      ;(.log js/console (::height state))
      ;(.log js/console animated-value)
      (view {:style page-style}
            (view {:style {:flex            0.3
                           :backgroundColor (:dark-primary palette)}}
                  (text {:style {:color      (:light-primary palette)
                                 :fontWeight "400"
                                 :padding    20
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
                  (view {:style {:flex 0.2}} (labelled-vertical-bar palette #(str (to-pc %) "%") br))
                  (view {:style {:flex 0.04}})
                  (view {:style {:flex 0.2}} (labelled-vertical-bar palette #(str (to-pc %) "%") er))
                  (view {:style {:flex           0.3
                                 :justifyContent "center"}}
                        (text {:style {:color     (:text-icons palette)
                                       :fontSize  24
                                       :padding   10
                                       :textAlign "left"}}
                              "With")))
            (view {:style {:flex            0.1
                           :backgroundColor (:dark-primary palette)}})))))