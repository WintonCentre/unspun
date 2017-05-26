(ns unspun.screens.old-palette
  (:require [cljs-exponent.components :refer [element text view image touchable-highlight status-bar] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index scenario to-pc number-needed]]
            [shared.ui :refer [picker picker-item]]
            [rum.core :as rum]))


(rum/defc page < rum/reactive []
  (let [scenar (rum/react scenario)
        br (:baseline-risk scenar)
        rr (:relative-risk scenar)
        palette (get-palette (rum/react palette-index))
        page-style {:flex            1
                    ;:color "white"
                    :backgroundColor (:primary palette)}]
    (view {:style {:flex 1}}
          (status-bar {:key 10
                       :hidden   false
                       :barStyle "light-content"})
          (view {:style page-style
                 :key 1}
                (text {:style {:color    (:text-icons palette)
                               :fontSize 30}} "hello")
                (apply picker
                  [{:selectedValue "js"
                    ;:onValueChange #(.log js/console %)
                    }
                   (picker-item {:label "Java" :value "java"})
                   (picker-item {:label "JavaScript" :value "js"})
                   (picker-item {:label "Objective C" :value "objc"})
                   (picker-item {:label "Swift" :value "swift"})])))))


