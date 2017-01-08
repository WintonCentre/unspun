(ns unspun.screens.not-yet
  (:require [cljs-exponent.components :refer [element text view image touchable-highlight status-bar animated-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index to-pc clamp]]
            [rum.core :as rum]))

(rum/defc page []
  (status-bar {:key 10
               :hidden   false
               :barStyle "light-content"})
  (text {:style {:color "black"}} "not-yet"))


