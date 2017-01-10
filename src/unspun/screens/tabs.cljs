(ns unspun.screens.tabs
  (:require [rum.core :as rum]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index stories story-index text-generator compare1 to-pc clamp]]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar] :as rn]
            [shared.ui :refer [sliding-tab-navigation sliding-tab-navigation-item ionicon]]
            [unspun.navigation.bottom-nav :refer [bottom-button-bar]]
            [unspun.screens.rum-bars :as bars]
            [unspun.screens.number-needed :as nn]
            [clojure.string :refer [upper-case]]))


(defn get-color [is-selected]
  (if is-selected "red" "green"))

(defn render-title [is-selected title]
  (text {:style {:color (get-color is-selected)}}
        title))

(rum/defcs page < rum/reactive [state]
  (let [navigator (aget (:rum/react-component state) "props" "navigator")
        palette (get-palette (rum/react palette-index))]
    (view {:style {
                   :flex            1
                   :backgroundColor "#CCF"
                   }}
          (sliding-tab-navigation
            {:flex 0.9
             :id                 "tab-navigation"
             :navigatorUID       "tab-navigation"
             :barBackgroundColor (:dark-primary palette)
             :indicatorStyle     {:backgroundColor (:accent palette)}
             :initialTab         "bars"
             }
            (sliding-tab-navigation-item
              {:id          "icons"
               :title       "Number Needed"
               :renderTitle render-title}
              (nn/page)
              )
            (sliding-tab-navigation-item
              {:id          "bars"
               :title       "Comparing risks"
               :renderTitle render-title
               }
              (bars/page)))
          (view {:style {:flex 0.1
                         :backgroundColor (:dark-primary palette)}}
                (bottom-button-bar)))))