(ns unspun.screens.tabs
  (:require [rum.core :as rum]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index stories story-index to-pc clamp]]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar] :as rn]
            [shared.ui :refer [sliding-tab-navigation sliding-tab-navigation-item ionicon add-page-title screen-w-h]]
            [unspun.navigation.bottom-nav :refer [bottom-button-bar]]
            [unspun.screens.rum-bars :as bars]
            [unspun.screens.number-needed :as nn]
            [unspun.screens.stats :as stats]
            [unspun.screens.mixins :refer [monitor]]
            [clojure.string :refer [upper-case]]))

(rum/defcs page < rum/reactive (add-page-title "Show") [state]
  (let [navigator (aget (:rum/react-component state) "props" "navigator")
        palette (get-palette (rum/react palette-index))
        [w h] (screen-w-h)
        landscape? (> w h)]
    (view {:style {:flex            1
                   :flexDirection      (if landscape? "row" "column")}}
          (sliding-tab-navigation
            {:flex               (if landscape? 0.75 0.9)
             :key                "tabs"
             :id                 "tab-navigation"
             :navigatorUID       "tab-navigation"
             :barBackgroundColor (:primary palette)
             :indicatorStyle     {:backgroundColor (:accent palette)}
             :labelStyle         {:color (:text-icons palette)}
             :initialTab         "bars"
             }
            (sliding-tab-navigation-item
              {:id         "stats"
               :key        "stats"
               :title      "Numbers"}
              (stats/page)
              )
            (sliding-tab-navigation-item
              {:id    "bars"
               :key   "bars"
               :title "Graph"
               }
              (bars/page))
            (sliding-tab-navigation-item
              {:id    "icons"
               :key   "icons"
               :title "Pictures"}
              (nn/page)
              ))

          (view {:key   "bottom-bar"
                 :style {:flex            (if landscape? 0.25 0.1)
                         :backgroundColor (:dark-primary palette)}}
                (bottom-button-bar)))))