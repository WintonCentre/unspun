(ns unspun.core
  (:require-macros [rum.core :refer [defc]])
  (:require [re-natal.support :as support]
            [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight] :as rn]
            [unspun.shared.palettes :refer [palettes]]))


;;;
;; design globals
;;;
(def logo-img (js/require "./assets/images/logo.png"))

#_(defn palette [] (:deep-purple-pink palettes))
(defn palette [] (:cyan-deep-orange palettes))


(defn alert [title]
  (.alert rn/alert title))

(defonce app-state (atom {:brand "Winton Centre"}))

(defn page-style []
  {:marginLeft      0
   :padding         0
   :alignItems      "center"
   :justifyContent  "space-around"
   :backgroundColor (:primary (palette))})

(defn brand-style []
  {:fontSize   30
   :fontWeight "400"
   :textAlign  "center"
   :color      (:light-primary (palette))})

(def shadow-size)

(defc AppRoot < rum/reactive [state]

  (view {:fill 1 :style (page-style)}

        (text {:style (merge {:paddingTop 40} (brand-style))}
              (:brand @state))
        (image {:source logo-img
                :style  {:transform [{:scale 0.5}]}})
        (touchable-highlight {:style   {:margin          40
                                        :backgroundColor (:accent (palette))
                                        :borderRadius    30
                                        :height          60
                                        :width           60
                                        :shadowColor     "#000"
                                        :shadowOffset    {:width 2 :height 2}
                                        :shadowRadius    2
                                        :shadowOpacity   0.5
                                        :alignItems      "center"
                                        :justifyContent  "center"
                                        }
                              :onPress #(alert "Hello!")}
                             (text {:style {:color (:text-icons (palette)) :textAlign "center" :fontWeight "bold" :width 55}} "Start"))))

(defonce root-component-factory (support/make-root-component-factory))

(defn mount-app [] (support/mount (AppRoot app-state)))

(defn init []
  (mount-app)
  (.registerComponent rn/app-registry "main" (fn [] root-component-factory)))
