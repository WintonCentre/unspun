(ns unspun.screens.settings
  (:require [cljs-exponent.components :refer [element text view image touchable-highlight status-bar] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index scenario notifications]]
            [shared.ui :refer [settings-list settings-list-header settings-list-item
                               ionicon entypo]]
            [rum.core :as rum]))

(rum/defc notifications-icon [onoff]
  (view {:style {:height     30,
                 :marginLeft 10,
                 :alignSelf  "center"}}
        (entypo #js {:paddingTop  20
                     :paddingLeft 16
                     :name        (str "notification" (if onoff "" "s-off"))
                     :color       "red"
                     :size        30})))

(rum/defc palette-icon < rum/static [palette]
  (view {:style {:height     30,
                 :marginLeft 10,
                 :alignSelf  "center"}}
        (ionicon #js {:paddingTop  20
                      :paddingLeft 16
                      :name        "ios-color-palette"
                      :color       (:accent palette)
                      :size        30})))


(rum/defcs page < rum/reactive [state]
  (let [palette (get-palette (rum/react palette-index))
        notifications-on (rum/react notifications)]
    (this-as this-page
      (view {:style {:backgroundColor (:primary palette)
                     :flex            1}}
            (status-bar {:key      10
                         :hidden   false
                         :barStyle "light-content"})
            (view {:key   1
                   :style {:marginTop 20
                           :flex      1}}
                  (settings-list
                    (settings-list-header
                      #js {:headerText  "fooey"
                           :headerStyle #js {:marginTop 50 :color "#fff"}})
                    (settings-list-item
                      {:hasNavArrow         false
                       :switchState         notifications-on
                       :switchOnValueChange #(reset! notifications %)
                       :hasSwitch           true
                       :icon                (notifications-icon notifications-on)
                       :title               (str "Notifications " (if notifications-on "on" "off"))
                       :title-style         #js {:width 120}})
                    (settings-list-header
                      #js {:headerText  "theming"
                           :headerStyle #js {:marginTop 50
                                             :color     "#fff"}})
                    (settings-list-item
                      {:icon    (palette-icon palette)
                       :onPress #(.push (aget (:rum/react-component state) "props" "navigator") "select-palette")
                       :title   "App colour scheme" :hasNavArrow true})
                    )
                  )))))