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
            [unspun.screens.native-base :as n-base]
            ))

(def Router (create-router (fn []
                             #js {:home          (fn [] (:rum/class (meta test-page)))
                                  :icon-array    (fn [] (:rum/class (meta number-needed/page)))
                                  :rum-bars      (fn [] (:rum/class (meta rum-bars/page)))
                                  :number-needed (fn [] (:rum/class (meta number-needed/page)))
                                  :startup       (fn [] (:rum/class (meta logo-page)))
                                  :theming       (fn [] (:rum/class (meta palette/page)))
                                  :settings      (fn [] (:rum/class (meta settings/page)))
                                  :not-yet       (fn [] (:rum/class (meta not-yet/page)))
                                  :native-base   (fn [] (:rum/class (meta n-base/page)))})))

(def r-help "May be my question is just how to pass props when we use the Navigator to route my app?

When you use <Navigator />, you're able to specify a renderScene function which will decide how
to render your scene.

renderScene accepts two arguments: route and navigator. In your particular case you're interested in a
route. Every time you push a new route, you can pass any extra params to it and then use it to render your component.
You can take a look on example I have here. So, in this example I use Navigator's initialRoute property, which specifies
the initial route in your application. It may contain any fields you want. In my case it's a component I render inside
the scene. But at the same moment you can pass foo: 'bar' and use it thru the route.foo property to inject it into your
component.")
