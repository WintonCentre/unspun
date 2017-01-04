(ns unspun.screens.settings
  (:require [cljs-exponent.components :refer [element text view image touchable-highlight status-bar] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index scenario to-pc number-needed]]
            [rum.core :as rum]))

(def picker (partial element (aget cljs-exponent.core/react-native "Picker")))
(def picker-item (partial element (aget cljs-exponent.core/react-native "Picker" "Item")))

(rum/defc page < rum/reactive []
  (let [scenar (rum/react scenario)
        br (:baseline-risk scenar)
        rr (:relative-risk scenar)
        palette (get-palette (rum/react palette-index))
        page-style {:flex            1
                    :color "white"
                    :backgroundColor (:primary palette)}]
    (view {:style {:flex 1}}
          (:status-bar {:hidden   false
                        :barStyle "light-content"})

          (view {:style page-style}
                (text {:style {:color    (:text-icons palette)
                               :fontSize 30}} "hello")
                (apply picker
                       {:selectedValue "js"
                        :onValueChange #(.log js/console %)}
                  [(picker-item {:label "Java" :value "java"})
                   (picker-item {:label "JavaScript" :value "js"})
                   (picker-item {:label "Objective C" :value "objc"})
                   (picker-item {:label "Swift" :value "swift"})])))))


