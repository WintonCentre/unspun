(ns unspun.core
  (:require-macros [rum.core :refer [defc]])
  (:require [re-natal.support :as support]
            [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight] :as rn]
            [unspun.shared.palette :refer [material-blue-orange]]))


;;;
;; design globals
;;;
(def logo-img (js/require "./assets/images/logo.png"))
(def app-palette material-blue-orange)


(defn alert [title]
  (.alert rn/alert title))

(defonce app-state (atom {:greeting "Hello Clojure in iOS and Android!"}))



(def page-style {:flexDirection   "column"
                 :flex            1
                 :marginLeft      0
                 :paddingLeft 10
                 :alignItems      "flex-start"
                 :backgroundColor (:primary app-palette)})


(defc AppRoot < rum/reactive [state]
  (view {:style page-style}
        (text {:style {:fontSize 30 :fontWeight "100" :marginBottom 20 :textAlign "center"}} (str @state))
        (image {:source logo-img})
        (touchable-highlight {:style   {:backgroundColor "#999" :padding 10 :borderRadius 5}
                              :onPress #(alert "HELLO!")}
                             (text {:style {:color "white" :textAlign "center" :fontWeight "bold"}} "press me"))))

(defonce root-component-factory (support/make-root-component-factory))

(defn mount-app [] (support/mount (AppRoot app-state)))

(defn init []
  (mount-app)
  (.registerComponent rn/app-registry "main" (fn [] root-component-factory)))
