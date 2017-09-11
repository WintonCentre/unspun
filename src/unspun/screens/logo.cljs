(ns unspun.screens.logo
  (:require-macros [rum.core :refer [defc defcs]])
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight status-bar animated-view] :as rn]
            [themes.palettes :refer [get-palette palettes]]
            [unspun.db :refer [version app-state palette-index brand-title app-banner to-pc clamp winton-csv scenario-url dimensions]]
            [shared.ui :refer [tffsz]]
            ))

;(def logo-img (js/require "./assets/images/logo.png"))
(def brand (js/require "./assets/images/brand.png"))
(def light-brand (js/require "./assets/images/light-brand.png"))

(def shadow-size 1)

(defn alert [title]
  (.alert rn/alert title))

(defn page-style []
  {:flex            1
   :flexDirection   "column"
   :justifyContent  "space-around"
   :backgroundColor (if (= (:primary (get-palette @palette-index))
                           (:primary (get-palette @palette-index)))
                      (:primary (get-palette @palette-index))
                      (:primary (get-palette @palette-index)))})

(defn brand-style []
  {:fontSize   (* 1.4 tffsz)
   :lineHeight 60
   :fontWeight "normal"
   :textAlign  "center"
   :color      (:light-primary (get-palette @palette-index))})

(defn version-style []
  {:fontSize   (* 0.8 tffsz)
   ;:lineHeight        10
   :fontWeight "normal"
   :textAlign  "center"
   :color      (:light-primary (get-palette @palette-index))})

(defn winton-brand? [url]
  (= url winton-csv))


(defcs logo-page < rum/reactive [state router]
  (let [pal-index (rum/react palette-index)
        palette (get-palette pal-index)
        {w :width h :height} (rum/react dimensions)
        portrait (> h w)]
    (letfn [(jumpToDrawer [drawerId route]
              (let [navigation (aget (:rum/react-component state) "props" "navigation")]
                (.performAction navigation (fn [stateUtils]
                                             (.jumpToItem ((aget stateUtils "drawer") "top-drawer") drawerId)))))]

      (view {:style (page-style)}

            (status-bar {:key      10
                         :hidden   false
                         :barStyle "light-content"
                         :flex 1
                         :justifyContent "flex-start"})

            (view {:style {:flex 1
                           :flexDirection (if portrait "column" "row")
                           :justifyContent (if portrait "space-around" "space-around")}}

                  (view {:style {:flex           (if portrait 0.7 0.5)
                                 :justifyContent (if portrait "space-around" "space-around")}}

                        (view {:style {:flex 1
                                       :justifyContent "space-around"}}
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
                                      (image {:source     (if (= (:dark-on-light palettes) palette) light-brand brand)
                                              :resizeMode "contain"
                                              :style      {:width     250
                                                           :height    250
                                                           :transform [{:scale (min 1 (/ tffsz 16))
                                                                        }]}})))))
                  (view {:style {:flex           (if portrait 0.3 0.5)
                                 :paddingBottom  tffsz
                                 :justifyContent (if portrait "flex-end" "space-around")}}
                        (touchable-highlight {:style   {;:flex           0.1
                                                        :marginLeft     20
                                                        :marginRight    20
                                                        :marginBottom   20
                                                        :height         (* tffsz 3)
                                                        :borderColor    "#fff"
                                                        :borderWidth    (* tffsz 0.2)
                                                        :borderRadius   (* tffsz 1.5)
                                                        :alignItems     "center"
                                                        :justifyContent "center"
                                                        }
                                              :onPress #(jumpToDrawer "intro" "intro")}
                                             (text {:style {:color      (:text-icons palette)
                                                            :fontSize   tffsz
                                                            :textAlign  "center"
                                                            :fontWeight "bold"}}
                                                   "What's this about?"))

                        (touchable-highlight {:style   {;:flex            0.1
                                                        :marginLeft     20
                                                        :marginRight    20
                                                        :height         (* tffsz 3)
                                                        :borderColor    (:accent palette)
                                                        :borderWidth    (* tffsz 0.2)
                                                        :borderRadius   (* tffsz 1.5)
                                                        :shadowColor    "#000"
                                                        :shadowOffset   {:width shadow-size :height shadow-size}
                                                        :shadowRadius   shadow-size
                                                        :shadowOpacity  0.5
                                                        :alignItems     "center"
                                                        :justifyContent "center"
                                                        }
                                              :onPress #(jumpToDrawer "scenarios" "stories")}

                                             (text {:style {:color      (:text-icons palette)
                                                            :textAlign  "center"
                                                            :fontSize   tffsz
                                                            :fontWeight "bold"}}
                                                   "Start"))))))))


