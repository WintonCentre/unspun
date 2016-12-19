(ns unspun.components.bars
  (:require-macros [rum.core :refer [defc]])
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index]]
            [graphics.svg :refer [svg circle rect]]
            ))

(def header-height 23)

(defc header < rum/reactive []
  (let [palette (get-palette (rum/react palette-index))
        header-style {:height           header-height
                      :backgroundColor (:dark-primary palette)}]
    (view {:style header-style})
    ))

(defc page < rum/reactive []
  (let [palette (get-palette (rum/react palette-index))
        page-style {:flex             1
                    :backgroundColor (:text-icons palette)
                    :paddingTop header-height
                    }]
    (view {:style {:flex 1}}
          (header)
          (view {:style page-style}
                (text {:style {:color      (:primary-text palette)
                               :fontWeight "400"
                               :fontSize   26}} "Hi")))))