(ns unspun.core
  (:require-macros [rum.core :refer [defc]])
  (:require [re-natal.support :as support]
            [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight] :as rn]
            [unspun.db :refer [app-state brand-title palette-index]]
            [themes.palettes :refer [palettes get-palette]]
            [unspun.screens.startup-page :refer [startup-page]]
            [unspun.screens.bars :as bars]
            [unspun.screens.logo :as logo :refer [logo-page]]))



(defc AppRoot < rum/reactive [state]

  ;(startup-page)
  ;(bars/page)
  (logo-page)
  )

(defonce root-component-factory (support/make-root-component-factory))

(defn mount-app [] (support/mount (AppRoot app-state)))

(defn init []
  (mount-app)
  (.registerComponent rn/app-registry "main" (fn [] root-component-factory)))
