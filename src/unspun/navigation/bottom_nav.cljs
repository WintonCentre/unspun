(ns unspun.navigation.bottom-nav
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [text view] :as rn]
            [themes.palettes :refer [get-palette]]
            [shared.ui :refer [button n-icon]]
            [unspun.db :refer [app-state palette-index stories story-index]]
            ))

(defn previous-icon [palette]
  (n-icon {:name  "ios-arrow-back"
           :key   2
           :style {:color (:text-icons palette)}
           }))

(defn next-icon [palette]
  (n-icon {:name  "ios-arrow-forward"
           :key   2
           :style {:color (:text-icons palette)}
           }))



(defn next-story []
  (reset! story-index (mod (inc @story-index) (count @stories))))

(defn previous-story []
  (reset! story-index (mod (dec @story-index) (count @stories))))

(rum/defc bottom-button-bar < rum/reactive []
  (let [palette (get-palette (rum/react palette-index))]
    (view {:style {:flex            0.5
                   :flexDirection   "row"
                   :justifyContent  "space-around"
                   :alignItems      "center"
                   :backgroundColor (:dark-primary palette)}}
          (button {:key       1
                   :bordered  true
                   :small     true
                   :textStyle {:color (:text-icons palette)}
                   :style     {:marginTop 13
                               :borderWidth 2
                               :borderColor (:text-icons palette)}
                   :onPress next-story}
                  (previous-icon palette)
                  "Previous"
                  )
          (button {:key       2
                   :flex      1
                   :bordered  true
                   :small     true
                   :textStyle {:color (:text-icons palette)}
                   :style     {:marginTop 13
                               :borderWidth 2
                               :borderColor (:text-icons palette)}
                   :onPress previous-story
                   }
                  (next-icon palette)
                  "Next"
                  ))))