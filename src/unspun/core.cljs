(ns unspun.core
  (:require-macros [rum.core :refer [defc]])
  (:require [re-natal.support :as support]
            [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight] :as rn]
            [unspun.db :refer [app-state brand-title palette-index]]
            [themes.palettes :refer [palettes get-palette]]
            [unspun.components.startup-page :refer [startup-page]]
            [unspun.components.bars :as bars]))


;;;
;; design globals
;;;
(def logo-img (js/require "./assets/images/logo.png"))

(defn alert [title]
  (.alert rn/alert title))

(defn canvas []
  {:position "absolute"
   :top 0
   :right 0
   :bottom 0
   :left 0})

(defn page-style []
  {:flex 1
   :backgroundColor (:primary (get-palette (rum/react palette-index)))})

(defn brand-style []
  {:fontSize   30
   :fontWeight "400"
   :textAlign  "center"
   :color      (:light-primary (get-palette (rum/react palette-index)))})

(def shadow-size 2)

(defc AppRoot < rum/reactive [state]

  ;(startup-page)
  (bars/page)

  #_(view {:style (page-style)}

        #_(text {:style (merge {:paddingTop 40} (brand-style))}
              (rum/react brand-title))
        (image {:source logo-img
                ;;:resizeMode "stretch"
                :style  {:transform [{:scale 0.3}]}})
        #_(touchable-highlight {:style   {:margin          40
                                        :backgroundColor (:accent (get-palette 0))
                                        :borderRadius    30
                                        :height          60
                                        :width           60
                                        :shadowColor     "#000"
                                        :shadowOffset    {:width shadowSize :height shadowSize}
                                        :shadowRadius    shadowSize
                                        :shadowOpacity   0.5
                                        :alignItems      "center"
                                        :justifyContent  "center"
                                        }
                              :onPress #(alert "Hello!")}
                             (text {:style {:color (:text-icons (get-palette 0)) :textAlign "center" :fontWeight "bold" :width 55}} "Start"))))

(defonce root-component-factory (support/make-root-component-factory))

(defn mount-app [] (support/mount (AppRoot app-state)))

(defn init []
  (mount-app)
  (.registerComponent rn/app-registry "main" (fn [] root-component-factory)))
