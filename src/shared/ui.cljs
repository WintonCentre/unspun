(ns shared.ui
  (:require [cljs-exponent.core :refer [react-native]]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar] :as rn]))


;; ex-navigation
(def ex-navigation (js/require "@exponent/ex-navigation"))
(def create-router (aget ex-navigation "createRouter"))

(def navigation-provider (partial element (aget ex-navigation "NavigationProvider")))
(def stack-navigation (partial element (aget ex-navigation "StackNavigation")))
(def drawer-navigation-layout (partial element (aget ex-navigation "DrawerNavigationLayout")))
(def drawer-navigation (partial element (aget ex-navigation "DrawerNavigation")))
(def drawer-navigation-item (partial element (aget ex-navigation "DrawerNavigationItem")))

;; vector-icons
(def vector-icons (js/require "@exponent/vector-icons"))
(def Ionicons (aget vector-icons "Ionicons"))
(def ionicon (partial element Ionicons))
(def Entypo (aget vector-icons "Entypo"))
(def entypo (partial element Entypo))
;(defn ionicon [attrs] (.createElement js/React Ionicons attrs))


;; react-native-settings-list
(def SettingsList (js/require "react-native-settings-list"))
(def settings-list (partial element SettingsList))
(def settings-list-header (partial element (aget SettingsList "Header")))
(def settings-list-item (partial element (aget SettingsList "Item")))


(def picker (partial element (aget react-native "Picker")))
(def picker-item (partial element (aget react-native "Picker" "Item")))

(def ListView (aget react-native "ListView"))
(def list-view (partial element ListView))

(def SwipeableListView (js/require "SwipeableListView"))
(def swipeable-list-view (partial element SwipeableListView))


;; Page jumps
(def goto-palette-screen "")