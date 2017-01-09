(ns unspun.screens.number-needed
  (:require [cljs-exponent.components :refer [element text view image touchable-highlight status-bar animated-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index to-pc number-needed stories story-index text-generator nn1]]
            [rum.core :as rum]))

(rum/defc page < rum/reactive []
  (let [scenar ((rum/react stories) (rum/react story-index))
        br (:baseline-risk scenar)
        rr (:relative-risk scenar)
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
                                     :fontSize   24}}
                            (text-generator nn1 scenar)))
                (view {:key   2
                       :style {:flex            0.6
                               :backgroundColor (:primary palette)}}
                      )
                (view {:style {:flex            0.1
                               :backgroundColor (:dark-primary palette)}})))))


