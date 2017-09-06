(ns unspun.screens.tabs
  (:require [rum.core :as rum]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index stories story-index to-pc clamp dimensions]]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar] :as rn]
            [shared.ui :refer [sliding-tab-navigation sliding-tab-navigation-item ionicon add-page-title ios? get-dimensions]]
            [unspun.navigation.bottom-nav :refer [story-links]]
            [unspun.screens.rum-bars :as bars]
            [unspun.screens.number-needed :as nn]
            [unspun.screens.stats :as stats]
            [unspun.screens.mixins :refer [monitor]]
            [clojure.string :refer [upper-case]]))

(def ios (ios?))

(rum/defcs page < rum/reactive (add-page-title "Scenario") [state]
  (let [navigator (aget (:rum/react-component state) "props" "navigator")
        palette (get-palette (rum/react palette-index))]

    (view {:style {:flex 1}}
          (sliding-tab-navigation
            {:flex               0.9
             :key                "tabs"
             :id                 "tab-navigation"
             :navigatorUID       "tab-navigation"
             :barBackgroundColor "black"
             :indicatorStyle     {:backgroundColor (:accent palette)
                                  :height          (if ios "100%" 4)
                                  }
             :position           (if ios "bottom" "top")
             :labelStyle         {:color "white"}
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

          #_(when-not ios (button-bar palette)))))