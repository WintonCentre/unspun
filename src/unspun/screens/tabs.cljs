(ns unspun.screens.tabs
  (:require [rum.core :as rum]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index stories story-index to-pc clamp]]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar] :as rn]
            [shared.ui :refer [sliding-tab-navigation sliding-tab-navigation-item ionicon add-page-title]]
            [unspun.navigation.bottom-nav :refer [bottom-button-bar]]
            [unspun.screens.rum-bars :as bars]
            [unspun.screens.number-needed :as nn]
            [unspun.screens.stats :as stats]
            [clojure.string :refer [upper-case]]))

(rum/defcs page < rum/reactive (add-page-title "Show") [state]
  (let [navigator (aget (:rum/react-component state) "props" "navigator")
        palette (get-palette (rum/react palette-index))]
    (view {:style {
                   :flex            1
                   :backgroundColor "#CCF"
                   }}
          (sliding-tab-navigation
            {:flex               0.9
             :key                "tabs"
             :id                 "tab-navigation"
             :navigatorUID       "tab-navigation"
             :barBackgroundColor (:dark-primary palette)
             :indicatorStyle     {:backgroundColor (:accent palette)}
             :initialTab         "stats"
             }
            (sliding-tab-navigation-item
              {:id    "stats"
               :key   "stats"
               :title "Numbers"}
              (stats/page)
              )
            (sliding-tab-navigation-item
              {:id    "bars"
               :key   "bars"
               :title "Graph"}
              (bars/page))
            (sliding-tab-navigation-item
              {:id    "icons"
               :key   "icons"
               :title "Pictures"}
              (nn/page)
              ))

          (view {:key   "bottom-bar"
                 :style {:flex            0.1
                         :backgroundColor (:dark-primary palette)}}
                (bottom-button-bar)))))