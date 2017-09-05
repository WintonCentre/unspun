(ns shared.ui
  (:require [goog.object :as gobj]
            [cljs-exponent.core :refer [react-native expo]]
            [cljs-exponent.components :refer [element text text-input view image touchable-highlight status-bar refresh-control] :as rn]))

(def oget gobj/get)

(def platform (oget react-native "Platform"))
(def rn-button (partial element (oget react-native "Button")))
(def flat-list (partial element (oget react-native "FlatList")))

(defn get-platform
  []
  (.-OS platform))

(defn ios?
  []
  (= "ios" (get-platform)))

(defn android?
  []
  (= "android" (get-platform)))

(def permissions (oget expo "Permissions"))

;; AsyncStorage
;; see https://github.com/glittershark/core-async-storage
(def async-storage (oget react-native "AsyncStorage"))
(def store-set-item (oget async-storage "setItem"))
(def store-get-item (oget async-storage "getItem"))
(def legacy-async-storage (oget expo "LegacyAsyncStorage"))
(def secure-store (oget expo "SecureStore"))

;; ex-navigation
(def ex-navigation (js/require "@expo/ex-navigation"))
(def create-router (oget ex-navigation "createRouter"))

(def navigation-provider (partial element (oget ex-navigation "NavigationProvider")))
(def stack-navigation (partial element (oget ex-navigation "StackNavigation")))
(def drawer-navigation-layout (partial element (oget ex-navigation "DrawerNavigationLayout")))
(def drawer-navigation (partial element (oget ex-navigation "DrawerNavigation")))
(def drawer-navigation-item (partial element (oget ex-navigation "DrawerNavigationItem")))

(def tab-navigation (partial element (oget ex-navigation "TabNavigation")))
(def tab-navigation-item (partial element (oget ex-navigation "TabNavigationItem")))
(def sliding-tab-navigation (partial element (oget ex-navigation "SlidingTabNavigation")))
(def sliding-tab-navigation-item (partial element (oget ex-navigation "SlidingTabNavigationItem")))


;; Hyperlinks
(def linking (oget react-native "Linking"))
(defn openURL [url] (.openURL linking url))

;; vector-icons
(def vector-icons (js/require "@expo/vector-icons"))
(def Ionicons (oget vector-icons "Ionicons"))
(def ionicon (partial element Ionicons))
(def Entypo (oget vector-icons "Entypo"))
(def entypo (partial element Entypo))
;(defn ionicon [attrs] (.createElement js/React Ionicons attrs))


;; react-native-settings-list
(def SettingsList (js/require "react-native-settings-list"))
(def settings-list (partial element SettingsList))
(def settings-list-header (partial element (oget SettingsList "Header")))
(def settings-list-item (partial element (oget SettingsList "Item")))


(def picker (partial element (oget react-native "Picker")))
(def picker-item (partial element (oget react-native "Picker" "Item")))

(def ListView (oget react-native "ListView"))
(def list-view (partial element ListView))

(def SwipeableListView (js/require "SwipeableListView"))
(def swipeable-list-view (partial element SwipeableListView))

;; react-native-settings-list
(def native-base (js/require "native-base"))
(def my-theme (js/require "./themes/nativetheme"))

(def container (partial element (oget native-base "Container")))
(def content (partial element (oget native-base "Content")))
(def n-icon (partial element (oget native-base "Icon")))
(def card (partial element (oget native-base "Card")))
(def card-item (partial element (oget native-base "CardItem")))
(def txt (partial element (oget native-base "Text")))
(def n-list (partial element (oget native-base "List")))
(def n-list-item (partial element (oget native-base "ListItem")))
(def radio (partial element (oget native-base "Radio")))
(def right (partial element (oget native-base "Right")))
(def grid (partial element (oget native-base "Grid")))
(def row (partial element (oget native-base "Row")))
(def button (partial element (oget native-base "Button")))

;; Screen size dependencies
(def PixelRatio (oget react-native "PixelRatio"))
(def pixel-ratio (.get PixelRatio))
(def font-scale (.getFontScale PixelRatio))

(def Dimensions (oget react-native "Dimensions"))
(defn raw-dimensions [] (.get Dimensions "window"))
(defn get-dimensions* [raw-dims] (into {} (map (fn [[k, v]] [(keyword k), v]) (js->clj raw-dims))))
(defn get-dimensions [] (get-dimensions* (raw-dimensions)))

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

(defn text-field-font-size []
  (let [{:keys [width height scale]} (get-dimensions)]
    (Math.sqrt (/ (* width height) 1300))))

(def tffsz (text-field-font-size))

(defn view-flex-area [content-flex view-flex]
  (let [{:keys [width height]} (get-dimensions)
        area (* content-flex view-flex width height)]
    area))

(defn default-font-size [] 20)

;; Gesture Responders
(def pan-responder (oget react-native "PanResponder"))

;;;
;; mixin to update navbar title
;;;
(defn add-page-title [title]
  {:will-mount (fn [state]
                 (aset (:rum/react-component state) "props" "route" "config" "navigationBar" "title" title)
                 state)})




