(ns unspun.core
  (:require-macros [rum.core :refer [defc]])
  (:require [re-natal.support :as support]
            [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight] :as rn]
            [unspun.db :refer [app-state brand-title palette-index]]
            [unspun.navigation.router :refer [Router]]
            [unspun.screens.top-drawer :refer [drawer]]
            [themes.palettes :refer [palettes get-palette]]
            [unspun.navigation.router :refer [Router ex-navigation create-router navigation-provider
                                              stack-navigation drawer-navigation drawer-navigation-item]]
            [unspun.screens.top-drawer :refer [drawer]]
            ))


(defc AppRoot < rum/reactive [state]

  ;(startup-page)
  ;(bars/page)
  ;(logo-page)

  (navigation-provider {:router Router}
                       (drawer)
                       ;(stack-navigation {:initialRoute (.getRoute Router "home")})
                       ;(startup-page)
                       ;(bars/page)
                       ;(logo-page)
                       )
  )

(defonce root-component-factory (support/make-root-component-factory))

(defn mount-app [] (support/mount (AppRoot app-state)))

(defn init []
  (mount-app)
  (.registerComponent rn/app-registry "main" (fn [] root-component-factory)))
