(ns unspun.core
  (:require-macros [rum.core :refer [defc]]
                   [cljs.core.async.macros :refer [go]])
  (:require [re-natal.support :as support]
            [rum.core :as rum]
            [cljs-exponent.components :as rn]
            [unspun.db :refer [app-state dimensions]]
            [shared.async-storage :refer [reload-app-state!]]
            [unspun.navigation.router :refer [Router]]
            [unspun.screens.top-drawer :refer [drawer]]
            [shared.ui :refer [navigation-provider get-dimensions]]
            [unspun.screens.top-drawer :refer [drawer]]
            [unspun.screens.mixins :refer [resize-mixin]]
            ))



(enable-console-print!)

(defn resizer
  "Resize handler which responds to screen orientation changes"
  [event]
  ;(println "core " (get-dimensions))
  (reset! dimensions (get-dimensions)))

(defc AppRoot < rum/reactive (resize-mixin resizer) [state]
  "Root of app is a sliding drawer component wrapped in an ex-navigation provider.
  We also ensure that app-state contains the correct screen dimensions at all times"
  (navigation-provider {:router Router}
                       (drawer)))

(defonce root-component-factory (support/make-root-component-factory))

(defn mount-app [] (support/mount (AppRoot app-state)))

(defn init []
  (mount-app)
  (.registerComponent rn/app-registry "main" (fn [] root-component-factory))
  (reload-app-state!)
  )


