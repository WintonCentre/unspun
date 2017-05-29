(ns unspun.screens.select-palette
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar animated-view] :as rn]
            [shared.ui :refer [ionicon native-base my-theme container content n-icon txt n-list n-list-item radio right]]
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
    {:key      (gensym "radio-list")
     :selected (= @palette-index index)
     :onPress  #(reset! palette-index index)}
    (view {:style {:backgroundColor (:primary (get-palette index))
                   :borderRadius    30
                   :width           30
                   :height          30}}
          (view {:style {:backgroundColor (:accent (get-palette index))
                         :borderRadius    10
                         :width           10
                         :height          10}}))
    (txt {:key 2
          :style {:paddingLeft 10}} (palette-titles index))
    (right (radio {:key      (gensym "radio")
                   :selected (= @palette-index index)}))
    ))

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

        ))))