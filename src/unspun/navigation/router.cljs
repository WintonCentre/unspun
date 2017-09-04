(ns unspun.navigation.router
  (:require [cljs-exponent.components :refer [element]]
            [clojure.string :refer [lower-case]]
            [shared.ui :refer [create-router]]
            [unspun.screens.logo :as logo :refer [logo-page]]
            [unspun.screens.introduction :as intro]
            [unspun.screens.number-needed :as number-needed]
            [unspun.screens.rum-bars :as rum-bars]
            [unspun.screens.settings :as settings]
            [unspun.screens.not-yet :as not-yet]
            [unspun.screens.story-list :as story-list]
            [unspun.screens.select-palette :as select-palette]
            [unspun.screens.scenario-url-editor :as edit-url]
            [unspun.screens.tabs :as tabs]
            [themes.palettes :refer [get-palette header-background]]
            [unspun.db :refer [palette-index]]
            ))

(defn wrap-route [cp-class route-options]
  (aset cp-class "route" (clj->js route-options))
  cp-class)

(defn wrap [screen]
  (let [cp-class (:rum/class (meta screen))]
    #(wrap-route cp-class {:navigationBar {:backgroundColor "black"
                                           }})))


(def Router (create-router (fn []
                             #js {:startup        (wrap logo-page)
                                  :intro          (wrap intro/page)
                                  :icon-array     (wrap number-needed/page)
                                  :rum-bars       (wrap rum-bars/page)
                                  :number-needed  (wrap number-needed/page)

                                  ;:theming        (wrap palette/page)
                                  :settings       (wrap settings/page)
                                  :not-yet        (wrap not-yet/page)
                                  :select-palette (wrap select-palette/page)
                                  :edit-url       (wrap edit-url/page)
                                  :stories        (wrap story-list/page)
                                  :tabs           (wrap tabs/page)
                                  })))

#_(def r-help "May be my question is just how to pass props when we use the Navigator to route my app?

When you use <Navigator />, you're able to specify a renderScene function which will decide how
to render your scene.

renderScene accepts two arguments: route and navigator. In your particular case you're interested in a
route. Every time you push a new route, you can pass any extra params to it and then use it to render your component.
You can take a look on example I have here. So, in this example I use Navigator's initialRoute property, which specifies
the initial route in your application. It may contain any fields you want. In my case it's a component I render inside
the scene. But at the same moment you can pass foo: 'bar' and use it thru the route.foo property to inject it into your
component.")
