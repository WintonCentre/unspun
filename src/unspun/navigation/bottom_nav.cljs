(ns unspun.navigation.bottom-nav
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [text view] :as rn]
            [themes.palettes :refer [get-palette]]
            [shared.ui :refer [button n-icon pixel-ratio font-scale txt tffsz ios? rn-button]]
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


(rum/defc story-links < rum/static [palette]

  #_(view {:style {:flex            1
                   :justifyContent  "space-around"
                   :alignItems      "center"
                   :flexDirection   "row"
                   :backgroundColor (:dark-primary palette)
                   }}
          (rn-button {:key      "prev-but"
                      :title    "< Previous"
                      :fontSize tffsz
                      :color    (:accent palette)
                      :onPress  previous-story}
                     )
          (rn-button {:key     "next-but"
                      :title   "Next >"
                      :color   (:accent palette)
                      :onPress next-story}
                     )
          )
  (view {:key   "story-links"
         :style {:flex           0
                 :flexDirection  "row"
                 :justifyContent "space-around"
                 :alignItems     "center"
                 :height         45
                 ;:backgroundColor (:dark-primary palette)
                 :margin         0
                 :transform      [{:scale (min 1 (/ tffsz 16))}]
                 }
         }
        (button {:key       "prev-but"
                 :bordered  true
                 :small     (not (ios?))
                 :textStyle {:color (:text-icons palette)}
                 :style     {:marginTop    0
                             :marginBottom 0
                             :borderWidth  2
                             :borderColor  (:text-icons palette)}
                 :onPress   next-story}
                (previous-icon palette)
                (txt {:key   "prev-txt"
                      :style {:color (:text-icons palette)}} "Previous"))
        (button {:key       "next-but"
                 :bordered  true
                 :small     (not (ios?))
                 :textStyle {:color (:text-icons palette)}
                 :style     {:marginTop    0
                             :marginBottom 0
                             :borderWidth  2
                             :borderColor  (:text-icons palette)
                             }

                 :onPress   previous-story
                 }
                (next-icon palette)
                (txt {:key   "next-txt"
                      :style {:color (:text-icons palette)
                              ;:fontSize (text-field-font-size)
                              }} "Next"))))