(ns unspun.screens.logo
  (:require-macros [rum.core :refer [defc defcs]])
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight status-bar animated-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [version app-state palette-index brand-title app-banner to-pc clamp winton-csv scenario-url]]
            [graphics.svg :refer [svg circle rect]]))

;(def logo-img (js/require "./assets/images/logo.png"))
(def brand (js/require "./assets/images/brand.png"))

(def shadow-size 1)

(defn alert [title]
  (.alert rn/alert title))

(defn page-style []
  {:flex            1
   :flexDirection   "column"
   :justifyContent  "space-around"
   :backgroundColor (:primary (get-palette @palette-index))})

(defn brand-style []
  {:fontSize   30
   :lineHeight 60
   :fontWeight "normal"
   :textAlign  "center"
   :color      (:light-primary (get-palette @palette-index))})

(defn version-style []
  {:fontSize   14
   ;:lineHeight        10
   :fontWeight "normal"
   :textAlign  "center"
   :color      (:light-primary (get-palette @palette-index))})

(defn winton-brand? [url]
  (= url winton-csv))


(defcs logo-page < rum/reactive [state router]

  (letfn [(jumpToDrawer [drawerId route]
            (let [navigation (aget (:rum/react-component state) "props" "navigation")]
              (.performAction navigation (fn [stateUtils]
                                           (.jumpToItem ((aget stateUtils "drawer") "top-drawer") drawerId)))
              ))]

    (view {:style (page-style)}

          (status-bar {:key      10
                       :hidden   false
                       :barStyle "light-content"})

          (text {:style (brand-style)}
                (if (winton-brand? (rum/react scenario-url))
                  (rum/react brand-title)
                  "Customised"))

          (when (winton-brand? (rum/react scenario-url))
            (view {}
                  (text {:style (brand-style)}
                        (rum/react app-banner))

                  (text {:style (version-style)}
                        version)))

          (when (winton-brand? (rum/react scenario-url))
            (view {:style {:flexDirection  "column"
                           :justifyContent "flex-start"
                           :alignItems     "center"
                           }}
                  (image {:source     brand
                          :resizeMode "contain"
                          :style      {:width  250
                                       :height 250}})))

          (touchable-highlight {:style   {;:flex           0.1
                                          :marginLeft     20
                                          :marginRight    20
                                          :height         50
                                          :borderColor    "#fff"
                                          :borderWidth    2
                                          :borderRadius   30
                                          :alignItems     "center"
                                          :justifyContent "center"
                                          }
                                :onPress #(jumpToDrawer "intro" "intro")}
                               (text {:style {:color      (:text-icons (get-palette (rum/react palette-index)))
                                              :textAlign  "center"
                                              :fontWeight "bold"}}
                                     "What's this about?"))

          (touchable-highlight {:style   {;:flex            0.1
                                          :marginLeft      20
                                          :marginRight     20
                                          :height          50
                                          ;:backgroundColor (:accent (get-palette (rum/react palette-index)))
                                          :borderColor    (:accent (get-palette (rum/react palette-index)))
                                          :borderWidth    2
                                          :borderRadius    30
                                          :shadowColor     "#000"
                                          :shadowOffset    {:width shadow-size :height shadow-size}
                                          :shadowRadius    shadow-size
                                          :shadowOpacity   0.5
                                          :alignItems      "center"
                                          :justifyContent  "center"
                                          }
                                :onPress #(jumpToDrawer "scenarios" "stories")}

                               (text {:style {:color      (:text-icons (get-palette (rum/react palette-index)))
                                              :textAlign  "center"
                                              :fontWeight "bold"}}
                                     "Start")))))


