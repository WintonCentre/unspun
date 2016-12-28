(ns unspun.screens.logo
  (:require-macros [rum.core :refer [defc defcs]])
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight status-bar animated-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index baseline-risk relative-risk brand-title to-pc clamp]]
            [graphics.svg :refer [svg circle rect]]))

(def logo-img (js/require "./assets/images/logo.png"))

(def shadow-size 2)

(defn alert [title]
  (.alert rn/alert title))

(defn page-style []
  {:flex            1
   :backgroundColor (:primary (get-palette @palette-index))})

(defn brand-style []
  {:fontSize   30
   :fontWeight "400"
   :textAlign  "center"
   :color      (:light-primary (get-palette (rum/react palette-index)))})

(defc logo-page < rum/reactive []
  (view {:style (page-style)}
        (text {:style (merge {:paddingTop 40 :flex 0.1} (brand-style))}
                (rum/react brand-title))
        (view {:style {:flex 0.8
                       :alignItems "center"}}
              (image {:source     logo-img
                      :resizeMode "stretch"
                      :style      {:transform [{:scale 0.3}]}}))
        (touchable-highlight {:style   {:flex 0.1
                                        :margin          20
                                        :backgroundColor (:accent (get-palette 0))
                                        :borderRadius    30
                                        :shadowColor     "#000"
                                        :shadowOffset    {:width shadow-size :height shadow-size}
                                        :shadowRadius    shadow-size
                                        :shadowOpacity   0.5
                                        :alignItems      "center"
                                        :justifyContent  "center"
                                        }
                              :onPress #(alert "Hello!")}
                             (text {:style {:color (:text-icons (get-palette 0)) :textAlign "center" :fontWeight "bold" :width 55}} "Start"))))