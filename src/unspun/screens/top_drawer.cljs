(ns unspun.screens.top-drawer
  (:require-macros [rum.core :refer [defc]])
  (:require [re-natal.support :as support]
            [rum.core :as rum]
            [cljs-exponent.components :refer [element text view image touchable-highlight style-sheet] :as rn]
            [unspun.db :refer [app-state brand-title palette-index]]
            [themes.palettes :refer [palettes get-palette]]
            [unspun.navigation.router :refer [Router ex-navigation create-router navigation-provider
                                              stack-navigation drawer-navigation drawer-navigation-item]]
            [unspun.screens.startup-page :refer [startup-page]]
            [unspun.screens.bars :as bars]
            [unspun.screens.rum-bars :as rum-bars]
            [unspun.screens.logo :as logo :refer [logo-page]]))


;; vector-icons
(def vector-icons (js/require "@exponent/vector-icons"))
(def Ionicons (aget vector-icons "Ionicons"))

(defn ionicon [attrs] (.createElement js/React Ionicons attrs))

(def ac-unit (ionicon (clj->js {:name  "md-checkmark-circle"
                                :size  30
                                :style {:transform [{:rotate "90deg"} {:scale 0.8}]}
                                :color "white"})))

(defn vector-icon [family attrs] (.createElement js/React family attrs))

(def bars-icon (vector-icon Ionicons (clj->js {:name  "ios-podium"
                                               :size  30
                                               :style {:width 14}
                                               :color "white"})))

(defn menu-icon [name] (ionicon (clj->js {:name  name
                                          :size  30
                                          :color "white"})))

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

(rum/defc header [palette]
  (let [st (styles palette)]
    (view {:style (aget st "header")})))

(rum/defc title [palette a-string isSelected]
  (let [st (styles palette)]
    (text {:style (js/Array. (aget st "buttonTitleText")
                             (if isSelected (aget st "selectedText")))}
          a-string)))

(rum/defc icon [name isSelected]
  (menu-icon name))

(rum/defc drawer < rum/reactive []
  (let [palette (get-palette (rum/react palette-index))
        st (styles palette)]

    (drawer-navigation
      {:drawerPosition "right"
       :renderHeader   #(header palette)
       :drawerWidth    200
       :drawerStyle    {:backgroundColor (:accent palette)}
       :initialItem    "startup"}

      (drawer-navigation-item
        {:id            "startup"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-arrow-up-outline")
         :renderTitle   (fn [isSelected] (title palette "Home" isSelected))
         }
        (stack-navigation
          {:id                 "startup-stack"
           :defaultRouteConfig {:navigationBar {:backgroundColor "#0084FF"
                                                :tintColor       "#fff"}}
           :initialRoute       (.getRoute Router "startup")}))

      (drawer-navigation-item
        {:id            "bars"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-body")
         :renderTitle   (fn [isSelected] (title palette "Icon Array" isSelected))}
        (stack-navigation
          {:id                 "bars-stack"
           :defaultRouteConfig {:navigationBar {:backgroundColor "#0084FF"
                                                :tintColor       "#fff"}}
           :initialRoute       (.getRoute Router "bars")}))

      (drawer-navigation-item
        {:id            "rum-bars"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    (fn [] bars-icon)
         :renderTitle   (fn [isSelected] (title palette "Bars" isSelected))}
        (stack-navigation
          {:id                 "rum-bars-stack"
           :defaultRouteConfig {:navigationBar {:backgroundColor "#0084FF"
                                                :tintColor       "#fff"}}
           :initialRoute       (.getRoute Router "rum-bars")}))

      (drawer-navigation-item
        {:id            "settings"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-settings")
         :renderTitle   (fn [isSelected] (title palette "Settings" isSelected))}
        (stack-navigation
          {:id                 "rum-bars-stack"
           :defaultRouteConfig {:navigationBar {:backgroundColor "#0084FF"
                                                :tintColor       "#fff"}}
           :initialRoute       (.getRoute Router "rum-bars")}))

      (drawer-navigation-item
        {:id            "share"
         :selectedStyle (aget st "selectedItemStyle")
         :renderIcon    #(menu-icon "ios-share-outline")
         :renderTitle   (fn [isSelected] (title palette "Share" isSelected))}
        (stack-navigation
          {:id                 "rum-bars-stack"
           :defaultRouteConfig {:navigationBar {:backgroundColor "#0084FF"
                                                :tintColor       "#fff"}}
           :initialRoute       (.getRoute Router "rum-bars")}))

      )))