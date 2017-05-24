(ns unspun.core
  (:require-macros [rum.core :refer [defc]]
                   [cljs.core.async.macros :refer [go]])
  (:require [re-natal.support :as support]
            [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight] :as rn]
            [unspun.db :refer [app-state brand-title palette-index]]
            [unspun.navigation.router :refer [Router]]
            [unspun.screens.top-drawer :refer [drawer]]
            [themes.palettes :refer [palettes get-palette]]
            [shared.ui :refer [ex-navigation create-router navigation-provider
                               stack-navigation drawer-navigation drawer-navigation-item]]
            [unspun.screens.top-drawer :refer [drawer]]

            [glittershark.core-async-storage :refer [get-item set-item multi-get multi-set]]
            [cljs.core.async :refer [<!]]
            ))



(enable-console-print!)

(defc AppRoot < rum/reactive [state]

  ;(test-page)
  ;(bars/page)
  ;(logo-page)
  (navigation-provider {:router Router}
                       (drawer)
                       )
  )

(defonce root-component-factory (support/make-root-component-factory))

(defn mount-app [] (support/mount (AppRoot app-state)))

(defn init []
  (mount-app)
  (.registerComponent rn/app-registry "main" (fn [] root-component-factory)))


