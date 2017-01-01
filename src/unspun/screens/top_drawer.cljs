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
(def material-icons (aget vector-icons "MaterialIcons"))


;(def FontAwesome (js/require "@exponent/vector-icons/FontAwesome"))

;(def icon (r/adapt-react-class (aget FontAwesome "default")))
;(def FontAwesomeButton (aget FontAwesome "default" "Button"))
;(def icon-button (r/adapt-react-class FontAwesomeButton))

;(def MaterialIcons (js/require "@exponent/vector-icons/MaterialIcons"))


(defn material-icon [name]
  (partial element (aget material-icons name)))
;(def material-icon (r/adapt-react-class (aget MaterialIcons "default")))

#_(def MaterialIconButton (aget MaterialIcons "default" "Button"))
;(def material-icon-button (r/adapt-react-ass MaterialIconButton))

#_(comment
    (def wrap-material (partial aget MaterialIcons))

    (defn wrap-svg-component [name]
      (partial element (wrap-svg name)))

    (def svg (partial element (wrap-svg)))
    (def circle (wrap-svg-component "Circle")))


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

(rum/defc icon [icon-name isSelected]
  (view {:style {:alignItems     "center"
                 :justifyContent "center"}}
        ))

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
         :renderTitle   (fn [isSelected] (title palette "Bars" isSelected))}
        (stack-navigation
          {:id                 "bars-stack"
           :defaultRouteConfig {:navigationBar {:backgroundColor "#0084FF"
                                                :tintColor       "#fff"}}
           :initialRoute       (.getRoute Router "bars")}))

      (drawer-navigation-item
        {:id            "rum-bars"
         :selectedStyle (aget st "selectedItemStyle")
         :renderTitle   (fn [isSelected] (title palette "Rum-Bars" isSelected))}
        (stack-navigation
          {:id                 "rum-bars-stack"
           :defaultRouteConfig {:navigationBar {:backgroundColor "#0084FF"
                                                :tintColor       "#fff"}}
           :initialRoute       (.getRoute Router "rum-bars")}))

      )))