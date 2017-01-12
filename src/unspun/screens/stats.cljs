(ns unspun.screens.stats
  (:require-macros [rum.core :refer [defc defcs]])
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar animated-view scroll-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index stories story-index text-generator compare1 nn1 nn2 to-pc clamp]]
            [unspun.navigation.bottom-nav :refer [bottom-button-bar]]
            ))


(defc header < rum/reactive []
  (let [palette (get-palette (rum/react palette-index))
        header-style {:height          23
                      :backgroundColor (:dark-primary palette)}]
    (view {:style header-style})))

(defn percentage [value]
  (str (to-pc value) "%"))


(defc page < rum/reactive []
  (let [scenar ((rum/react stories) (rum/react story-index))
        db (rum/react app-state)
        br (:baseline-risk scenar)
        rr (:relative-risk scenar)
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

      (scroll-view {:style page-style}
            (view {:key   1
                   :style {:flex            0.3
                           :justifyContent  "center"
                           :alignItems      "center"
                           :backgroundColor (:dark-primary palette)}}
                  (text {:style {:color      (:light-primary palette)
                                 :fontWeight "400"
                                 :padding    20
                                 :fontSize   (:fontSize scenar)}}
                        (text-generator compare1 scenar))
                  (text {:style {:color      (:light-primary palette)
                                 :fontWeight "400"
                                 :padding    20
                                 :fontSize   (:fontSize scenar)}}
                        (text-generator nn2 scenar)))
            (view {:key   2
                   :style {:flex          0.7
                           :flexDirection "row"}})

            ))))