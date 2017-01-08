(ns unspun.screens.select-palette
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar animated-view] :as rn]
            [shared.ui :refer [ionicon native-base my-theme container content n-icon txt n-list n-list-item radio]]
            [unspun.db :refer [app-state palette-index]]
            [themes.palettes :refer [palettes get-palette]]
            ))

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
            :onPress  #(reset! palette-index index)
            :selected (= @palette-index index)})
    (txt {:key 2} (palette-titles index))))

(rum/defcs page < rum/reactive
                  (rum/local 0 ::selection) [state]
  (let [palette (get-palette (rum/react palette-index))
        palette-count (count palettes)]
    (container
      {:style {:flex 1}}
      (content
        {:key   2
         :theme (aget my-theme "default")
         :style {:flex 1}}
        (status-bar {:key      10
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