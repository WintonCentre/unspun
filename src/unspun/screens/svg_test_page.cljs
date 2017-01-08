(ns unspun.screens.svg-test-page
  (:require-macros [rum.core :refer [defc]])
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index]]
            [graphics.svg :refer [svg circle rect]]
            ))

(defn title-style [palette]
  {:color          (:light-primary palette)
   :marginVertical 6
   :fontSize       50
   })

(defn test-page-style [palette]
  {:flex            1
   :backgroundColor (:primary palette)})

(defc startup-title < rum/reactive [& {:keys [title style]
                                       :or   {title "Relative Risks Unspun"
                                              style (title-style (get-palette (rum/react palette-index)))}}]
  (text {
         :style style} title))

(defc test-page < rum/reactive [& {:keys [title logo launcher style]
                                      :or   {title startup-title
                                             style (test-page-style (get-palette (rum/react palette-index)))}}]
  (let [palette (get-palette (rum/react palette-index))]
    (view {:style style}
          (status-bar {:key 10
                       :hidden   false
                       :barStyle "light-content"})
          (title)
          (svg {:flex 1}
               (rect {:x 50 :y 50 :width 200 :height 200 :fill (:accent palette)})
               (circle {:cx 150 :cy 150 :r 50 :fill (:dark-primary palette)})))))

