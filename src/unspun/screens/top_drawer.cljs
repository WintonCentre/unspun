(ns unspun.screens.top-drawer
  (:require-macros [rum.core :refer [defc]])
  (:require [re-natal.support :as support]
            [rum.core :as rum]
            [cljs-exponent.components :refer [element text view image touchable-highlight style-sheet] :as rn]
            [unspun.db :refer [app-state brand-title palette-index]]
            [themes.palettes :refer [palettes get-palette header-background header-foreground]]
            #_[shared.ui :refer [ex-navigation create-router navigation-provider
                               stack-navigation drawer-navigation-layout drawer-navigation drawer-navigation-item
                               ionicon]]
            [shared.ui :refer [ionicon]]
            #_[unspun.navigation.router :refer [Router]]
    ;[unspun.screens.svg-test-page :refer [test-page]]
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

(rum/defc header
  "this is hidden!"
  ([]
   (let [st (styles (get-palette @palette-index))]
     (view {:style (aget st "header")}
           (text {:style {:color "#fff"}} "Not visible!")))))

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

    #_(drawer-navigation
      {:drawerPosition "right"
       :navigatorUID   "top-drawer"
       :renderHeader   #(header)                            ;;todo - wrong and unsused?
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
           :navigatorUID       "startup"
           :defaultRouteConfig (defaultRouteConfig "Startup")
           :initialRoute       (.getRoute Router "startup")}))

      (drawer-navigation-item
        {:id            "intro"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-return-right" palette %)
         :renderTitle   (fn [isSelected] (do
                                           ;(.log js/console "Startup")
                                           (title palette "Introduction" isSelected)))}
        (stack-navigation
          {:id                 "intro-stack"
           :navigatorUID       "intro"
           :defaultRouteConfig (defaultRouteConfig "Introduction")
           :initialRoute       (.getRoute Router "intro")}))

      (drawer-navigation-item
        {:id            "scenarios"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-share-outline" palette %)
         :renderTitle   (fn [isSelected] (title palette "Scenarios" isSelected))}
        (stack-navigation
          {:id                 "scenarios-stack"
           :navigatorUID       "scenarios"
           :defaultRouteConfig (defaultRouteConfig "Scenarios")
           :initialRoute       (.getRoute Router "stories")}))

      (drawer-navigation-item
        {:id            "settings"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-settings" palette %)
         :renderTitle   (fn [isSelected] (title palette "Settings" isSelected))}
        (stack-navigation
          {:id                 "settings-stack"
           :navigatorUID       "settings"
           :defaultRouteConfig (defaultRouteConfig "Settings")
           :initialRoute       (.getRoute Router "settings")}))

      #_(drawer-navigation-item
        {:id            "share"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-share-outline" palette %)
         :renderTitle   (fn [isSelected] (title palette "Share" isSelected))}
        (stack-navigation
          {:id                 "share-stack"
           :navigatorUID       "share"
           :defaultRouteConfig (defaultRouteConfig "Share")
           :initialRoute       (.getRoute Router "not-yet")}))

      )))