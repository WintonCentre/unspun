(ns unspun.screens.tabs
  (:require [rum.core :as rum]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index stories story-index text-generator compare1 to-pc clamp]]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar] :as rn]
            [shared.ui :refer [sliding-tab-navigation sliding-tab-navigation-item ionicon]]
            [unspun.screens.rum-bars :as bars]
            [unspun.screens.number-needed :as nn]))


(defn get-color [is-selected]
  (if is-selected "red" "green"))

(defn render-title [is-selected title]
  (text {:style {:color (get-color is-selected)}}
        title))

(defn render-icon [name is-selected]
  (ionicon {:name  name
            :size  30
            :color (if is-selected "blue" "orange")}))

(defn get-style [is-selected]
  {:backgroundColor (if is-selected "#0084FF" "#888")})

(rum/defcs page [state]
  (let [navigator (aget (:rum/react-component state) "props" "navigator")]
    (prn navigator)
    (view {:style {
                   :flex            1
                   :backgroundColor "#CCF"
                   }}
          (sliding-tab-navigation
            {:id                 "tab-navigation"
             :navigatorUID       "tab-navigation"
             :barBackgroundColor "black"
             :indicatorStyle     {:backgroundColor "red"}
             :initialTab         "bars"}
            (sliding-tab-navigation-item
              {:id            "icons"
               :title         "Number Needed"
               :renderTitle   render-title
               :render-icon   (partial render-icon "md-keypad")
               :selectedStyle (:backgroundColor "#0084FF")}
              (nn/page)
              )
            (sliding-tab-navigation-item
              {:id            "bars"
               :title         "Compare With and without"
               :renderTitle   render-title
               :render-icon   (partial render-icon "ios-stats")
               :selectedStyle (:backgroundColor "#0084FF")
               }
              (bars/page))))))