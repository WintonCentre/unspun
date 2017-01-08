(ns unspun.screens.select-palette
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar animated-view] :as rn]
            [shared.ui :refer [ionicon]]
            [unspun.db :refer [app-state palette-index]]
            [themes.palettes :refer [palettes get-palette]]
            ))


;; react-native-settings-list
(def native-base (js/require "native-base"))
(def my-theme (js/require "./themes/nativetheme"))

(def container (partial element (aget native-base "Container")))
(def content (partial element (aget native-base "Content")))
(def n-icon (partial element (aget native-base "Icon")))
(def card (partial element (aget native-base "Card")))
(def card-item (partial element (aget native-base "CardItem")))
(def txt (partial element (aget native-base "Text")))
(def n-list (partial element (aget native-base "List")))
(def n-list-item (partial element (aget native-base "ListItem")))
(def radio (partial element (aget native-base "Radio")))
(def grid (partial element (aget native-base "Grid")))
(def row (partial element (aget native-base "Row")))


(comment
  (def settings-list (partial element SettingsList))
  (def settings-list-header (partial element (aget SettingsList "Header")))
  (def settings-list-item (partial element (aget SettingsList "Item"))))

(rum/defc icon-example [name]
  (content {:theme (aget my-theme "default")}
           (n-icon {:name "ios-menu"})))

(def palette-titles
  (mapv name (keys palettes)))

(defn select-palette-item! [index]
  (n-list-item
    {:key index}
    (radio {:key      1
            :onPress #(reset! palette-index index)
            :selected (= @palette-index index)})
    (txt {:key 2} (palette-titles index))))

(rum/defcs page < rum/reactive
                  (rum/local 0 ::selection) [state]
  (let [palette (get-palette (rum/react palette-index))
        palette-count (count palettes)]
    (container
      {:style {:flex 1}}
      (content
        {:key 2
         :theme (aget my-theme "default")
         :style {:flex 1}}
        (status-bar {:key 10
                     :hidden   false
                     :barStyle "light-content"})
        (apply n-list
               (concat [{:key   1
                         :style {:flex 1}}]
                       [(map select-palette-item! (range palette-count))]))

        )
      #_(card {:key 2
               ; :style {:flex    1
               }
              (card-item {:header true
                          :key    1
                          :style  {:backgroundColor "red"}
                          }
                         (txt {:style {:color "white"}} "Card Header"))
              (card-item {:header false
                          :key    2
                          :style  {;:flex          1
                                   ;:flexDirection "row"
                                   ;:alignItems    "center"
                                   }}
                         (icon-example)
                         #_(ionicon (clj->js {:name  "ios-home"
                                              :style {:color        "red"
                                                      :fontSize     20
                                                      :paddingRight 16}}))
                         (txt {:style {:color "black"}} "My text here"))

              (card-item {:header true
                          :key    3}
                         (text {:style {:color "blue"}} "Footer"))
              ))))