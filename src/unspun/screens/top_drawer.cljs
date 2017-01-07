(ns unspun.screens.top-drawer
  (:require-macros [rum.core :refer [defc]])
  (:require [re-natal.support :as support]
            [rum.core :as rum]
            [cljs-exponent.components :refer [element text view image touchable-highlight style-sheet] :as rn]
            [unspun.db :refer [app-state brand-title palette-index]]
            [themes.palettes :refer [palettes get-palette]]
            [shared.ui :refer [ex-navigation create-router navigation-provider
                               stack-navigation drawer-navigation-layout drawer-navigation drawer-navigation-item
                               ionicon]]
            [unspun.navigation.router :refer [Router]]
            [unspun.screens.svg-test-page :refer [test-page]]
            [unspun.screens.rum-bars :as rum-bars]
            [unspun.screens.logo :as logo :refer [logo-page]]
            [unspun.common :refer [react-native ios?]]))

(defn styles [palette] (.create style-sheet

                                (clj->js {:header            {:flex       1
                                                              :height     180
                                                              :width      nil
                                                              :paddingTop 40}

                                          :buttonTitleText   {:color      (:text-icons palette)
                                                              :fontWeight "bold"
                                                              :marginLeft 18}

                                          :icon              {:color "#999"}

                                          :selectedText      {:color (:dark-primary palette)}

                                          :selectedItemStyle {:backgroundColor (:light-primary palette)}
                                          })))


;; statusbar
(def StatusBar (aget react-native "StatusBar"))
(defn hide-status-bar [] (.setHidden StatusBar true))
(defn show-status-bar [] (.setHidden StatusBar false))



(defn menu-icon [name palette isSelected]
  (let [st (styles palette)]
    (ionicon (clj->js {:name  name
                       :size  24
                       :style (js/Array. (aget st "buttonTitleText")
                                         (when isSelected (aget st "selectedText"))
                                         (when (= name "ios-podium") #js {:width 12}))}))))

(rum/defc header [palette]
  (let [st (styles palette)]
    (view {:style (aget st "header")})))

(rum/defc title [palette a-string isSelected]
  (let [st (styles palette)]
    (text {:style (js/Array. (aget st "buttonTitleText")
                             (if isSelected (aget st "selectedText")))}
          a-string)))

(rum/defc icon [name palette isSelected]
  (menu-icon name palette isSelected))

(defn navbar-height [] (if (ios?) 60 71))

(rum/defc drawer < rum/reactive []
  (let [palette (get-palette (rum/react palette-index))
        st (styles palette)]

    (drawer-navigation
      {:drawerPosition "right"
       :renderHeader   #(header palette)
       :drawerWidth    200
       :drawerStyle    {:backgroundColor (:accent palette)
                        :height          20}
       :initialItem    "startup"
       }

      (drawer-navigation-item
        {:id            "startup"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-arrow-up-outline" palette %)
         :renderTitle   (fn [isSelected] (title palette "Startup" isSelected))}
        (stack-navigation
          {:id                 "startup-stack"
           :defaultRouteConfig {:navigationBar {:height          (navbar-height)
                                                :backgroundColor (:accent palette)
                                                :tintColor       (:text-icons palette)
                                                :title           "Startup"}}
           :initialRoute       (.getRoute Router "startup")}))

      (drawer-navigation-item
        {:id            "icon-array"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-body" palette %)
         :renderTitle   (fn [isSelected] (title palette "Number needed" isSelected))}
        (stack-navigation
          {:id                 "icons-stack"
           :defaultRouteConfig {:navigationBar {:height          (navbar-height)
                                                :backgroundColor (:accent palette)
                                                :tintColor       (:text-icons palette)
                                                :title           "Number Needed"}}
           :initialRoute       (.getRoute Router "number-needed")}))

      (drawer-navigation-item
        {:id            "rum-bars"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-podium" palette %)
         :renderTitle   (fn [isSelected] (title palette "  Compare" isSelected))}
        (stack-navigation
          {:id                 "bars-stack"
           :defaultRouteConfig {:navigationBar {:height          (navbar-height)
                                                :backgroundColor (:accent palette)
                                                :tintColor       (:text-icons palette)
                                                :title           "Compare with and without"}}
           :initialRoute       (.getRoute Router "rum-bars")}))

      (drawer-navigation-item
        {:id            "settings"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-settings" palette %)
         :renderTitle   (fn [isSelected] (title palette "Settings" isSelected))}
        (stack-navigation
          {:id                 "settings-stack"
           :defaultRouteConfig {:navigationBar {:height          (navbar-height)
                                                :backgroundColor (:accent palette)
                                                :tintColor       (:text-icons palette)
                                                :title           "Settings"}}
           :initialRoute       (.getRoute Router "settings")}))


      (drawer-navigation-item
        {:id            "share"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-share-outline" palette %)
         :renderTitle   (fn [isSelected] (title palette "Share" isSelected))}
        (stack-navigation
          {:id                 "share-stack"
           :defaultRouteConfig {:navigationBar {:height          (navbar-height)
                                                :backgroundColor (:accent palette)
                                                :tintColor       (:text-icons palette)
                                                :title           "Share"}}
           :initialRoute       (.getRoute Router "not-yet")}))

      (drawer-navigation-item
        {:id            "native-base"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-share-outline" palette %)
         :renderTitle   (fn [isSelected] (title palette "Native Base" isSelected))}
        (stack-navigation
          {:id                 "n-base-stack"
           :defaultRouteConfig {:navigationBar {:height          (navbar-height)
                                                :backgroundColor (:accent palette)
                                                :tintColor       (:text-icons palette)
                                                :title           "Native Base"}}
           :initialRoute       (.getRoute Router "native-base")}))


      )))