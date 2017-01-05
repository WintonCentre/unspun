(ns unspun.navigation.router
  (:require [cljs-exponent.core :refer [exponent]]
            [cljs-exponent.components :refer [element]]
            [clojure.string :refer [lower-case]]
            [shared.ui :refer [create-router]]
            [unspun.screens.svg-test-page :refer [test-page]]
            [unspun.screens.number-needed :as number-needed]
            [unspun.screens.rum-bars :as rum-bars]
            [unspun.screens.logo :as logo :refer [logo-page]]
            [unspun.screens.palette :as palette]
            [unspun.screens.settings :as settings]
            [unspun.screens.not-yet :as not-yet]
            ))

(def Router (create-router (fn []
                             #js {:home          (fn [] (:rum/class (meta test-page)))
                                  :icon-array    (fn [] (:rum/class (meta number-needed/page)))
                                  :rum-bars      (fn [] (:rum/class (meta rum-bars/page)))
                                  :number-needed (fn [] (:rum/class (meta number-needed/page)))
                                  :startup       (fn [] (:rum/class (meta logo-page)))
                                  :theming       (fn [] (:rum/class (meta palette/page)))
                                  :settings      (fn [] (:rum/class (meta settings/page)))
                                  :not-yet       (fn [] (:rum/class (meta not-yet/page)))})))

#_(comment
    (def StackNavigation (aget ExNavigation "StackNavigation"))
    (def stack-navigation (r/adapt-react-class StackNavigation))
    (def TabNavigation (aget ExNavigation "TabNavigation"))
    (def tab-navigation (r/adapt-react-class TabNavigation))
    (def TabNavigationItem (aget ExNavigation "TabNavigationItem"))
    (def tab-navigation-item (r/adapt-react-class TabNavigationItem)))