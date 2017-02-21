(ns shared.ui
  (:require [cljs-exponent.core :refer [react-native]]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar] :as rn]))


(def platform (aget react-native "Platform"))

(defn get-platform
  []
  (.-OS platform))

(defn ios?
  []
  (= "ios" (get-platform)))

(defn android?
  []
  (= "android" (get-platform)))

;; AsyncStorage
;; see https://github.com/glittershark/core-async-storage
(def async-storage (aget react-native "AsyncStorage"))
(def store-set-item (aget async-storage "setItem"))
(def store-get-item (aget async-storage "getItem"))

;; ex-navigation
(def ex-navigation (js/require "@exponent/ex-navigation"))
(def create-router (aget ex-navigation "createRouter"))

(def navigation-provider (partial element (aget ex-navigation "NavigationProvider")))
(def stack-navigation (partial element (aget ex-navigation "StackNavigation")))
(def drawer-navigation-layout (partial element (aget ex-navigation "DrawerNavigationLayout")))
(def drawer-navigation (partial element (aget ex-navigation "DrawerNavigation")))
(def drawer-navigation-item (partial element (aget ex-navigation "DrawerNavigationItem")))

(def tab-navigation (partial element (aget ex-navigation "TabNavigation")))
(def tab-navigation-item (partial element (aget ex-navigation "TabNavigationItem")))
(def sliding-tab-navigation (partial element (aget ex-navigation "SlidingTabNavigation")))
(def sliding-tab-navigation-item (partial element (aget ex-navigation "SlidingTabNavigationItem")))

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

;; react-native-settings-list
(def native-base (js/require "native-base"))
(def my-theme (js/require "./themes/nativetheme"))

(def container (partial element (aget native-base "Container")))
(def content (partial element (aget native-base "Content")))
(def n-icon (partial element (aget native-base "Icon")))
(def card (partial element (aget native-base "Card")))
(def card-item (partial element (aget native-base "CardItem")))
(def txt (partial element (aget native-base "Text")))
(def n-list (partial element (aget native-base "List")))
(def n-list-item (partial element (aget native-base "ListItem")))
(def radio (partial element (aget native-base "Radio")))
(def right (partial element (aget native-base "Right")))
(def grid (partial element (aget native-base "Grid")))
(def row (partial element (aget native-base "Row")))
(def button (partial element (aget native-base "Button")))

;; Screen size dependencies
(def PixelRatio (aget react-native "PixelRatio"))
(def pixel-ratio (.get PixelRatio))
(def font-scale (.getFontScale PixelRatio))

(defn get-dimensions [] (into {} (map (fn [[k, v]] [(keyword k), v]) (js->clj (.get (aget react-native "Dimensions") "window")))))
(defn status-bar-height [] (if (ios?) 20 25))
(defn tab-bar-height [] 56)
(defn tab-content-height [screen-height content-flex]
  (* content-flex (- screen-height (tab-bar-height) (status-bar-height))))

(defn screen-area []
  (let [{:keys [width height]} (get-dimensions)]
    (* width height)
    ))

(defn screen-w-h []
  (let [{:keys [width height]} (get-dimensions)]
    [width height]
    ))

(defn screen-width []
  (:width (get-dimensions)))


(defn text-field-font-size []
  (let [{:keys [width height scale]} (get-dimensions)]
    (Math.sqrt (/ (* width height) 1300))))

(defn view-flex-area [content-flex view-flex]
  (let [{:keys [width height]} (get-dimensions)
        area (* content-flex view-flex width height)]
    area))

(defn default-font-size [] 20)

;; Gesture Responders
(def pan-responder (aget react-native "PanResponder"))

;;;
;; mixin to update navbar title
;;;
(defn add-page-title [title]
  {:will-mount (fn [state]
                 ;(.log js/console (:rum/react-component state))
                 (aset (:rum/react-component state) "props" "route" "config" "navigationBar" "title" title)
                 state)})




