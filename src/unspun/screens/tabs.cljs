(ns unspun.screens.tabs
  (:require [rum.core :as rum]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index stories story-index to-pc clamp]]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar] :as rn]
            [shared.ui :refer [sliding-tab-navigation sliding-tab-navigation-item ionicon add-page-title ios?]]
            [unspun.navigation.bottom-nav :refer [bottom-button-bar]]
            [unspun.screens.rum-bars :as bars]
            [unspun.screens.number-needed :as nn]
            [unspun.screens.stats :as stats]
            [unspun.screens.mixins :refer [monitor]]
            [clojure.string :refer [upper-case]]))

(def ios false)

(rum/defc button-bar
  [palette]
  (view {:key   "bottom-bar"
         :style {:flex            0.1
                 :backgroundColor (:dark-primary palette)}}
        (bottom-button-bar)))

(rum/defcs page < rum/reactive (add-page-title "Show") [state]
  (let [navigator (aget (:rum/react-component state) "props" "navigator")
        palette (get-palette (rum/react palette-index))]
    (view {:style {:flex 1}}
          (when ios (button-bar palette))
          (sliding-tab-navigation
            {:flex               0.9
             :key                "tabs"
             :id                 "tab-navigation"
             :navigatorUID       "tab-navigation"
             :barBackgroundColor (if ios "black" (:primary palette))
             :indicatorStyle     {:backgroundColor (:accent palette)
                                  :height          (if ios "100%" 4)
                                  }
             :position           (if ios "bottom" "top")
             ;:pressColor         (:accent palette)
             :labelStyle         {:color (:text-icons palette)}
             :initialTab         "bars"
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
               :title "Graph"
               }
              (bars/page))
            (sliding-tab-navigation-item
              {:id    "icons"
               :key   "icons"
               :title "Pictures"}
              (nn/page)
              ))

          (when-not ios (button-bar palette)))))