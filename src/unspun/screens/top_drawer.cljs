(ns unspun.screens.top-drawer
  (:require-macros [rum.core :refer [defc]])
  (:require [re-natal.support :as support]
            [rum.core :as rum]
            [cljs-exponent.components :refer [element text view image touchable-highlight style-sheet] :as rn]
            [unspun.db :refer [app-state brand-title palette-index]]
            [themes.palettes :refer [palettes get-palette header-background header-foreground]]
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
                                                              ;:height     64
                                                              :width      180
                                                              ;:backgroundColor "red"
                                                              :paddingTop 64
                                                              }

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

(rum/defc header []
  (let [st (styles (get-palette @palette-index))]
    (view {:style (aget st "header")}
          (text {:style {:color "#fff"}} "Hi there"))))

(rum/defc title [palette a-string isSelected]
  (let [st (styles palette)]
    (text {:style (js/Array. (aget st "buttonTitleText")
                             (if isSelected (aget st "selectedText")))}
          a-string)))

(rum/defc icon [name palette isSelected]
  (menu-icon name palette isSelected))

(defn navbar-height [] (if (ios?) nil 71))

(defn defaultRouteConfig [name]
  {:navigationBar {:height          (navbar-height)
                   ;:translucent     true
                   :backgroundColor header-background
                   :tintColor       header-foreground
                   :title           name}})

(rum/defc drawer < rum/reactive []
  (let [palette (get-palette (rum/react palette-index))
        st (styles palette)]

    (drawer-navigation
      {:drawerPosition "right"
       :renderHeader   #(header)
       :drawerWidth    200
       :drawerStyle    {:backgroundColor (:dark-primary (get-palette (rum/react palette-index)))
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
           :defaultRouteConfig (defaultRouteConfig "Startup")
           :initialRoute       (.getRoute Router "startup")}))

      (drawer-navigation-item
        {:id            "native-base"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-share-outline" palette %)
         :renderTitle   (fn [isSelected] (title palette "Stories" isSelected))}
        (stack-navigation
          {:id                 "n-base-stack"
           :defaultRouteConfig (defaultRouteConfig "Stories")
           :initialRoute       (.getRoute Router "stories")}))

      (drawer-navigation-item
        {:id            "icon-array"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-body" palette %)
         :renderTitle   (fn [isSelected] (title palette "Number needed" isSelected))}
        (stack-navigation
          {:id                 "icons-stack"
           :defaultRouteConfig (defaultRouteConfig "Number Needed")
           :initialRoute       (.getRoute Router "number-needed")}))

      (drawer-navigation-item
        {:id            "rum-bars"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-podium" palette %)
         :renderTitle   (fn [isSelected] (title palette "  Compare" isSelected))}
        (stack-navigation
          {:id                 "bars-stack"
           :defaultRouteConfig (defaultRouteConfig "Compare with and without")
           :initialRoute       (.getRoute Router "rum-bars")}))

      (drawer-navigation-item
        {:id            "settings"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-settings" palette %)
         :renderTitle   (fn [isSelected] (title palette "Settings" isSelected))}
        (stack-navigation
          {:id                 "settings-stack"
           :defaultRouteConfig (defaultRouteConfig "Settings")
           :initialRoute       (.getRoute Router "settings")}))


      (drawer-navigation-item
        {:id            "share"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-share-outline" palette %)
         :renderTitle   (fn [isSelected] (title palette "Share" isSelected))}
        (stack-navigation
          {:id                 "share-stack"
           :defaultRouteConfig (defaultRouteConfig "Share")
           :initialRoute       (.getRoute Router "not-yet")}))

      (drawer-navigation-item
        {:id            "tabs"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-share-outline" palette %)
         :renderTitle   (fn [isSelected] (title palette "Tabs" isSelected))}
        (stack-navigation
          {:id                 "tabs-stack"
           :defaultRouteConfig (defaultRouteConfig "Tabs")
           :initialRoute       (.getRoute Router "tabs")}))

      )))