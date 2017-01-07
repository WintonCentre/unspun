(ns unspun.screens.native-base
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar animated-view] :as rn]
            [shared.ui :refer [ionicon]]
            ))


;; react-native-settings-list
(def native-base (js/require "native-base"))
(def container (partial element (aget native-base "Container")))
(def content (partial element (aget native-base "Content")))
(def n-icon (partial element (aget native-base "Icon")))
(def card (partial element (aget native-base "Card")))
(def card-item (partial element (aget native-base "CardItem")))
(def txt (partial element (aget native-base "Text")))


(comment
  (def settings-list (partial element SettingsList))
  (def settings-list-header (partial element (aget SettingsList "Header")))
  (def settings-list-item (partial element (aget SettingsList "Item"))))

(rum/defc icon-example []
  (content {:style {:paddingTop 50}}

           #_(n-icon {:name  "ios-menu"
                    :style {:fontSize 20
                            :color    "red"}})))



(rum/defc page []

  (container {:flex 1
              :padding 16}
             (content {:flex 1}
                      (card {:style {:flex    1}}
                            (card-item {:header true
                                        :style  {:backgroundColor "red"}}
                                       (txt {:style {:color "white"}} "Card Header"))
                            (card-item {:header false
                                        :style  {:flex          1
                                                 :flexDirection "row"
                                                 :alignItems "center"}}
                                       (ionicon (clj->js {:name "ios-home"
                                                          :style {:color "red"
                                                                  :fontSize 20
                                                                  :paddingRight 16}}))
                                       (txt {:style {:color    "black"}} "My text here"))

                            (card-item {:footer true}
                                       (text {:style {:color    "black"}} "Card Footer"))
                            )))
  )