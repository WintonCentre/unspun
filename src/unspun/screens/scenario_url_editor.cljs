(ns unspun.screens.scenario-url-editor
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [element text text-input view image touchable-highlight status-bar animated-view] :as rn]
            [shared.ui :refer [ionicon native-base my-theme container content n-icon txt n-list n-list-item radio right card card-item button]]
            [unspun.db :refer [app-state palette-index scenario-url winton-csv]]
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
    (txt {:key 2} (palette-titles index))
    (right (radio {:key      (gensym "radio")
                   :selected (= @palette-index index)}))
    ))

(rum/defcs page < rum/reactive
                  (rum/local 0 ::selection) [state]
  (let [palette (get-palette (rum/react palette-index))
        palette-count (count palettes)]
    (container
      {:style {:flex 1}}
      (text {:style {:color           (:text-icons palette)
                     :backgroundColor (:dark-primary palette)
                     :padding         20
                     :fontSize        16
                     :flex            1}}
            "To use your own source for scenarios, download and edit the CSV file below. If you publish your version as CSV somewhere and provide its address in the box below, the app will use it on refresh.")
      (card {:key   2
             :style {:backgroundColor (:dark-primary palette)
                     :padding         0
                     :margin          0
                     :borderWidth     0
                     :flex            4}
             }
            (card-item {:header true
                        :key    1
                        :style  {:backgroundColor (:dark-primary palette)}
                        }
                       (txt {:style {:color "white"}} "risk scenario URL"))
            (card-item {:header false
                        :key    2
                        :style  {:margin          10
                                 :backgroundColor "#fff"
                                 ;:flex          1
                                 ;:flexDirection "row"
                                 :alignItems      "center"
                                 }}
                       (text-input {:style           {:flex            1
                                                      :height          70
                                                      :borderColor     (:accent palette)
                                                      :borderWidth     1
                                                      :backgroundColor "#fff"
                                                      :padding         20
                                                      :paddingTop      10
                                                      :paddingBottom   15
                                                      :margin          0
                                                      :fontSize        16}
                                    :multiline       true
                                    :number-of-lines 3
                                    :keyboardType    "url"
                                    :value           (rum/react scenario-url)
                                    :onChangeText    #(reset! scenario-url %)})

                       )
            (card-item {:footer true
                        :key    3
                        :style
                                {:backgroundColor (:dark-primary palette)
                                 :margin          10
                                 :flex            1
                                 :flexDirection   "row"
                                 :justifyContent  "flex-end"
                                 :alignItems      "flex-start"
                                 }}



                       (button {:style   {;:backgroundColor (:text-icons palette)
                                          :marginLeft      20
                                          :marginRight     20
                                          :height          50
                                          :backgroundColor (:dark-primary palette)
                                          :borderColor     (:accent palette)
                                          :borderWidth     2
                                          :borderRadius    30
                                          :shadowColor     "#000"
                                          :shadowOffset    {:width 1 :height 1}
                                          :shadowRadius    1
                                          :shadowOpacity   0.5
                                          :alignItems      "center"
                                          :justifyContent  "center"}
                                :onPress #(reset! scenario-url winton-csv)}
                               (text {:style {:color (:text-icons palette)}
                                      :block true} "Revert to default setting"))

                       )
            )

      )))


(comment

  (ns unspun.screens.scenario-url-editor
    (:require [rum.core :as rum]
              [cljs-exponent.components :refer [element text text-input view image touchable-highlight status-bar animated-view] :as rn]
              [shared.ui :refer [ionicon native-base my-theme container content n-icon txt n-list n-list-item radio right card card-item]]
              [unspun.db :refer [app-state palette-index scenario-url]]
              [themes.palettes :refer [palettes get-palette]]
              ))



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
      (txt {:key 2} (palette-titles index))
      (right (radio {:key      (gensym "radio")
                     :selected (= @palette-index index)}))
      ))

  (rum/defcs page < rum/reactive
                    (rum/local 0 ::selection) [state]
    (let [palette (get-palette (rum/react palette-index))]
      (container
        (card {:key   2
               :style {:backgroundColor (:primary palette)
                       :padding         0
                       :margin          0}
               }
              (card-item {:header true
                          :key    1
                          :style  {:backgroundColor (:primary palette)}
                          }
                         (txt {:style {:color "white"}} "risk scenario URL"))
              (card-item {:header false
                          :key    2
                          :style  {:margin          10
                                   :backgroundColor "#fff"
                                   ;:flex          1
                                   ;:flexDirection "row"
                                   :alignItems      "center"
                                   }}
                         (text-input {:style           {:flex            1
                                                        :height          60
                                                        ;:borderColor     'gray'
                                                        ;:borderWidth     1
                                                        :backgroundColor "#fff"
                                                        :padding         0
                                                        :margin          0
                                                        :fontSize        16}
                                      :multiline       true
                                      :number-of-lines 3
                                      :keyboardType    "url"
                                      :value           (rum/react scenario-url)
                                      :onChangeText    #(reset! scenario-url %)})

                         )

              (card-item {:header true
                          :key    3}
                         (text {:style {:color "blue"}} "Footer"))
              )))))