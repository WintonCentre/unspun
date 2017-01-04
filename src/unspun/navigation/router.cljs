(ns unspun.navigation.router
  (:require [cljs-exponent.core :refer [exponent]]
            [cljs-exponent.components :refer [element]]
            [clojure.string :refer [lower-case]]
            [unspun.screens.svg-test-page :refer [test-page]]
            [unspun.screens.number-needed :as number-needed]
            [unspun.screens.bars :as bars]
            [unspun.screens.rum-bars :as rum-bars]
            [unspun.screens.logo :as logo :refer [logo-page]]
            [unspun.screens.not-yet :as not-yet]
            ))

;; ex-navigation
(def ex-navigation (js/require "@exponent/ex-navigation"))


(def navigation-provider (partial element (aget ex-navigation "NavigationProvider")))
(def stack-navigation (partial element (aget ex-navigation "StackNavigation")))
(def drawer-navigation-layout (partial element (aget ex-navigation "DrawerNavigationLayout")))
(def drawer-navigation (partial element (aget ex-navigation "DrawerNavigation")))
(def drawer-navigation-item (partial element (aget ex-navigation "DrawerNavigationItem")))

(def create-router (aget ex-navigation "createRouter"))

#_(defn wrap-route
    [component route-opts]
    (let [c (r/create-class {:component-will-mount set-nav
                             :reagent-render       (fn [] component)})]
      (aset c "route" (clj->js route-opts))
      c))

(def Router (create-router (fn []
                             #js {:home       (fn [] (:rum/class (meta test-page)))
                                  :icon-array (fn [] (:rum/class (meta number-needed/page)))
                                  :bars       (fn [] (:rum/class (meta bars/page)))
                                  :rum-bars   (fn [] (:rum/class (meta rum-bars/page)))
                                  :startup    (fn [] (:rum/class (meta logo-page)))
                                  :not-yet    (fn [] (:rum/class (meta not-yet/page)))})))


#_(comment
    (def StackNavigation (aget ExNavigation "StackNavigation"))
    (def stack-navigation (r/adapt-react-class StackNavigation))
    (def TabNavigation (aget ExNavigation "TabNavigation"))
    (def tab-navigation (r/adapt-react-class TabNavigation))
    (def TabNavigationItem (aget ExNavigation "TabNavigationItem"))
    (def tab-navigation-item (r/adapt-react-class TabNavigationItem)))