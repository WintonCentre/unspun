(ns unspun.screens.top-drawer
  (:require-macros [rum.core :refer [defc]])
  (:require [re-natal.support :as support]
            [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight style-sheet] :as rn]
            [unspun.db :refer [app-state brand-title palette-index]]
            [themes.palettes :refer [palettes get-palette]]
            [unspun.navigation.router :refer [Router ex-navigation create-router navigation-provider
                                              stack-navigation drawer-navigation drawer-navigation-item]]
            [unspun.screens.startup-page :refer [startup-page]]
            [unspun.screens.bars :as bars]
            [unspun.screens.logo :as logo :refer [logo-page]]))

(def ionicons (js/require "@exponent/vector-icons"))

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
    (view {:style      (aget st "header")})))

(rum/defc title [palette a-string isSelected]
  (let [st (styles palette)]
    (text {:style (js/Array. (aget st "buttonTitleText")
                             (if isSelected (aget st "selectedText")))}
          a-string)))

(rum/defc drawer < rum/reactive []
  (let [palette (get-palette (rum/react palette-index))
        st (styles palette)]
    (drawer-navigation
      {:drawerPosition "right"
       :renderHeader   #(header palette)                            ;(fn [] (:rum/class (meta header)))
       :drawerWidth    200
       :drawerStyle    {:backgroundColor (:accent palette)}
       :initialItem    "startup"}

      (drawer-navigation-item
        {:id            "startup"
         :selectedStyle (aget st "selectedItemStyle")
         :renderTitle   (fn [isSelected] (title palette "Home" isSelected))}
        (stack-navigation
          {:id                 "startup-stack"
           :defaultRouteConfig {:navigationBar {:backgroundColor "#0084FF"
                                                :tintColor       "#fff"}}
           :initialRoute       (.getRoute Router "startup")}))

      (drawer-navigation-item
        {:id            "bars"
         :selectedStyle (aget st "selectedItemStyle")
         :renderTitle   (fn [isSelected] (title palette "Bars" isSelected))}
        (stack-navigation
          {:id                 "bars-stack"
           :defaultRouteConfig {:navigationBar {:backgroundColor "#0084FF"
                                                :tintColor       "#fff"}}
           :initialRoute       (.getRoute Router "bars")}))

      )))