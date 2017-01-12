(ns unspun.screens.logo
  (:require-macros [rum.core :refer [defc defcs]])
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight status-bar animated-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index brand-title app-banner to-pc clamp]]
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

(defcs logo-page < rum/reactive [state router]
  (view {:style (page-style)}
        (status-bar {:key      10
                     :hidden   false
                     :barStyle "light-content"})
        (text {:style (merge {:paddingTop 40 :flex 0.1} (brand-style))}
              (rum/react brand-title))
        (text {:style (merge {:paddingTop 0 :flex 0.1} (brand-style))}
              (rum/react app-banner))
        (view {:style {:flex           0.4
                       ;:backgroundColor "red"
                       :justifyContent "flex-start"
                       :alignItems     "center"}}
              (image {:source     logo-img
                      :resizeMode "contain"
                      :style      {:marginTop -170
                                   :transform [{:scale 0.3}]}}))
        (touchable-highlight {:style {:flex            0.075
                                      :margin          20
                                      ;:backgroundColor (:accent (get-palette (rum/react palette-index)))
                                      :borderColor "#fff"
                                      :borderWidth 2
                                      :borderRadius    30
                                      ;:shadowColor     "#000"
                                      ;:shadowOffset    {:width shadow-size :height shadow-size}
                                      ;:shadowRadius    shadow-size
                                      ;:shadowOpacity   0.5
                                      :alignItems      "center"
                                      :justifyContent  "center"
                                      }
                              :onPress #_#(.push
                                            (aget (:rum/react-component state) "props" "navigator")
                                            (.getRoute Router "stories")) ;#(alert "Hello!")
                                     #(.push (aget (:rum/react-component state) "props" "navigator") "intro") ;#(alert "Hello!")
                              }
                             (text {:style {:color (:text-icons (get-palette (rum/react palette-index))) :textAlign "center" :fontWeight "bold" :width 200}} "What's this about?"))
        (touchable-highlight {:style {:flex            0.075
                                      :margin          20
                                      :backgroundColor (:accent (get-palette (rum/react palette-index)))
                                      :borderRadius    30
                                      :shadowColor     "#000"
                                      :shadowOffset    {:width shadow-size :height shadow-size}
                                      :shadowRadius    shadow-size
                                      :shadowOpacity   0.5
                                      :alignItems      "center"
                                      :justifyContent  "center"
                                      }
                              :onPress #_#(.push
                                            (aget (:rum/react-component state) "props" "navigator")
                                            (.getRoute Router "stories")) ;#(alert "Hello!")
                                     #(.push (aget (:rum/react-component state) "props" "navigator") "stories") ;#(alert "Hello!")
                              }
                             (text {:style {:color (:text-icons (get-palette (rum/react palette-index))) :textAlign "center" :fontWeight "bold" :width 55}} "Start"))))