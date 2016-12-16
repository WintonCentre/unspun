(ns unspun.core
  (:require-macros [rum.core :refer [defc]])
  (:require [re-natal.support :as support]
            [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight] :as rn]
            [unspun.shared.palette :refer [material-blue-orange]]))


;;;
;; design globals
;;;
(def logo-img (js/require "./assets/images/logo.png"))
(def app-palette material-blue-orange)


(defn alert [title]
  (.alert rn/alert title))

(defonce app-state (atom {:brand "Winton Centre"}))

(def page-style {:flex 1
                 :marginLeft      0
                 :paddingLeft 10
                 :alignItems      "center"
                 :justifyContent "space-around"
                 :backgroundColor (:primary app-palette)})

(def brand-style {:fontSize 30
                  :fontWeight "400"
                  :textAlign "center"
                  :color (:dark-primary app-palette)})


(defc AppRoot < rum/reactive [state]
  (view {:style page-style}
        (text {:style (merge {:paddingTop 80} brand-style)}
              (:brand @state))
        (view {:justifyContent "center" :style {:flexDirection "row" }}
              (view (:style {:flexDirection "column" :textAlign "center"})
                    (image {:source logo-img
                            :style  {:transform [{:scale 0.5}]
                                     }
                            })))
        (touchable-highlight {:style   {:marginBottom 40
                                        :backgroundColor (:accent app-palette)
                                        :padding         10
                                        :borderRadius    30 :height 60 :width 60
                                        :shadowColor     "#000"
                                        :shadowOffset    {:width 3 :height 3}
                                        :shadowRadius 3
                                        :shadowOpacity 0.5
                                        :justifyContent  "center"}
                              :onPress #(alert "HELLO!")}
                             (text {:style {:color "white" :textAlign "center" :fontWeight "bold"}} "Start"))))

(defonce root-component-factory (support/make-root-component-factory))

(defn mount-app [] (support/mount (AppRoot app-state)))

(defn init []
  (mount-app)
  (.registerComponent rn/app-registry "main" (fn [] root-component-factory)))
