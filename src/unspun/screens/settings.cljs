(ns unspun.screens.settings
  (:require [cljs-exponent.components :refer [element text text-input view image touchable-highlight status-bar] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index scenario scenario-url notifications use-cache]]
            [shared.ui :refer [settings-list settings-list-header settings-list-item
                               ionicon entypo]]
            [rum.core :as rum]))

(rum/defc notifications-icon [palette onoff]
  (view {:style {:height     30,
                 :marginLeft 10,
                 :alignSelf  "center"}}
        (entypo #js {:paddingTop  20
                     :paddingLeft 16
                     :name        (str "notification" (if onoff "" "s-off"))
                     :color       (:accent palette)
                     :size        30})))

(rum/defc cache-icon [palette onoff]
  (view {:style {:height     30,
                 :marginLeft 10,
                 :alignSelf  "center"}}
        (ionicon #js {:paddingTop  20
                      :paddingLeft 16
                      :name        (if onoff "ios-briefcase" "ios-briefcase-outline")
                      :color       (:accent palette)
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

(rum/defc download-icon < rum/static [palette]
  (view {:style {:height     30,
                 :marginLeft 10,
                 :alignSelf  "center"}}
        (ionicon #js {:paddingTop  20
                      :paddingLeft 16
                      :name        "ios-download"
                      :color       (:accent palette)
                      :size        30})))


(rum/defcs page < rum/reactive [state]
  (let [palette (get-palette (rum/react palette-index))
        notifications-on (rum/react notifications)
        cache-on (rum/react use-cache)]
    (this-as this-page
      (view {:style {:backgroundColor (:primary palette)
                     :flex            1}}
            (status-bar {:key      1
                         :hidden   false
                         :barStyle "light-content"})
            (view {:key   2
                   :style {:marginTop 0
                           :flex      3}}
                  (settings-list
                    {:flex   1
                     :height nil
                     :width  nil}
                    (settings-list-header
                      {:key         1
                       :headerText  "offline use"
                       :headerStyle {:marginTop 30
                                     :color     "rgba(255,255,255,0.8)"}})


                    #_(settings-list-item
                        {:key                 2
                         :style               {:backgroundColor "#888"
                                               :opacity         0.3}
                         :hasNavArrow         false
                         :switchState         notifications-on
                         :switchOnValueChange #(reset! notifications %)
                         :hasSwitch           true
                         :icon                (notifications-icon palette notifications-on)
                         :title               (str "Notifications " (if notifications-on "on" "off"))
                         :title-style         {:width   120
                                               :opacity 0.5
                                               }})

                    (settings-list-item
                      {:key                 2
                       :style               {:backgroundColor "#888"
                                             :opacity         0.3}
                       :hasNavArrow         false
                       :switchState         cache-on
                       :switchOnValueChange #(reset! use-cache %)
                       :hasSwitch           true
                       :icon                (cache-icon palette cache-on)
                       :title               (if cache-on "Save app state on device.\nSwitch off to clear." "Local storage cleared.\nSwitch on to use offline.")
                       :title-style         {:width   120
                                             :opacity 0.5
                                             }})
                    (settings-list-header
                      {:key         3
                       :headerText  "theming"
                       :headerStyle {:marginTop 30
                                     :color     "rgba(255,255,255,0.8)"}})
                    (settings-list-item
                      {:key     4
                       :icon    (palette-icon palette)
                       :onPress #(.push (aget (:rum/react-component state) "props" "navigator") "select-palette")
                       :title   "Choose a colour scheme" :hasNavArrow true})

                    (settings-list-header
                      {:key         5
                       :headerText  "updates"
                       :headerStyle {:marginTop 30
                                     :color     "rgba(255,255,255,0.8)"}})

                    (settings-list-item
                      {:key     6
                       :icon    (download-icon palette)
                       :onPress #(.push (aget (:rum/react-component state) "props" "navigator") "edit-url")
                       :title   "Set the URL for scenario updates" :hasNavArrow true}
                      )
                    ))
            (view {:style {:flex 1}}
                  )
            ))))