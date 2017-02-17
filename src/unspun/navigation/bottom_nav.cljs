(ns unspun.navigation.bottom-nav
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [text view] :as rn]
            [themes.palettes :refer [get-palette]]
            [shared.ui :refer [button n-icon pixel-ratio font-scale txt text-field-font-size ios?]]
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
  (let [palette (get-palette (rum/react palette-index))
        margin (Math.round (* 4 font-scale))]
    (view {:key "bottom-button-bar"
           :style {:flex            0.5
                   :flexDirection   "row"
                   :justifyContent  "space-around"
                   :alignItems      "center"
                   :backgroundColor (:dark-primary palette)}}
          (button {:key       "prev-but"
                   :bordered  true
                   :small     (not (ios?))
                   :textStyle {:color (:text-icons palette)}
                   :style     {:margin margin
                               :borderWidth 2
                               :borderColor (:text-icons palette)}
                   :onPress next-story}
                  (previous-icon palette)
                  (txt {:key "prev-txt"
                        :style {:color (:text-icons palette)}} "Previous")
                  )
          (button {:key       "next-but"
                   :bordered  true
                   :small     (not (ios?))
                   :textStyle {:color (:text-icons palette)}
                   :style     {:margin      margin
                               :borderWidth 2
                               :borderColor (:text-icons palette)}
                   :onPress   previous-story
                   }
                  (next-icon palette)
                  (txt {:key   "next-txt"
                        :style {:color (:text-icons palette)
                                ;:fontSize (text-field-font-size)
                                }} "Next")
                  ))))