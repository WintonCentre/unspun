(ns unspun.screens.number-needed
  (:require [cljs-exponent.components :refer [element text view image touchable-highlight status-bar animated-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index scenario to-pc number-needed]]
            [rum.core :as rum]))

(rum/defc page < rum/reactive []
  (let [scenar (rum/react scenario)
        br (:baseline-risk scenar)
        rr (:relative-risk scenar)
        palette (get-palette (rum/react palette-index))
        page-style {:flex            1
                    :backgroundColor (:primary palette)}]
    (view {:style {:flex 1}}
          (:status-bar {:hidden   false
                        :barStyle "light-content"})

          (view {:style page-style}
                (text {:style {:color    (:text-icons palette)
                               :fontSize 30}} "hello")))))


