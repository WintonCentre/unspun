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

(rum/defc select-palette-item! < rum/reactive [navigation index]
  (n-list-item
    {:key      (str "palette" index)
     :selected (= (rum/react palette-index) index)
     :onPress  #(do
                  (reset! palette-index index)
                  (.pop navigation)
                  )}
    (view {:key   30
           :style {:backgroundColor (:dark-primary (get-palette index))
                   ;:borderRadius    30
                   :width           30
                   :height          30}}
          (view {:key 20
                 :style {:backgroundColor (:primary (get-palette index))
                         :borderRadius    25
                         :width           25
                         :height          25}}
                (view {:key   1
                       :style {:backgroundColor (:accent (get-palette index))
                               :borderRadius    10
                               :width           10
                               :height          10}})
                (text {:key   2
                       :style {:fontSize 16
                               :color    (:text-icons (get-palette index))}} "A")))
    (txt {:key   40
          :style {:paddingLeft 10}} (palette-titles index))
    (right {:key 50}
           (radio {:key      (gensym "radio")
                   :selected (= (rum/react palette-index) index)}))
    ))

(rum/defcs page < rum/reactive
                  (rum/local 0 ::selection) [state]
  (let [navigation (aget (:rum/react-component state) "props" "navigator")
        palette (get-palette (rum/react palette-index))
        palette-count (count palettes)]
    ;(.log js/console navigation)
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
               {:key 70
                :style {:flex 1}}
               (mapv #(select-palette-item! navigation %) (range palette-count)))

        ))))