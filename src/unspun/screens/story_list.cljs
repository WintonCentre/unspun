(ns unspun.screens.story-list
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar animated-view] :as rn]
            [shared.ui :refer [ionicon native-base my-theme container content n-icon txt n-list n-list-item radio card card-item button]]
            [unspun.db :refer [app-state palette-index stories story-index story-icon story]]
            [themes.palettes :refer [palettes get-palette]]
            [clojure.string :refer [capitalize]]
            ))

(def palette-titles
  (mapv name (keys palettes)))

(rum/defc icon-example [name]
  (content {:theme (aget my-theme "default")}
           (n-icon {:name  name
                    :style {:color (:dark-primary (get-palette @palette-index))}})))

(def add-icon
  (view {:style         {:backgroundColor (:accent (get-palette @palette-index))
                         :borderRadius    20
                         :paddingLeft     0
                         :width           30
                         :height          30
                         :alignItems      "center"
                         :justifyContent  "center"
                         :shadowColor     "black"
                         }
         :shadowOffset  #js [1 1]
         :shadowOpacity 0.3
         :shadowRadius  1}
        (n-icon {:name  "md-add"
                 :style {:fontSize 20
                         :color    "white"
                         }
                 })))

(def edit-icon
  (n-icon {:name  "ios-create"
           :key   2
           :style {:color (:dark-primary (get-palette @palette-index))}
           }))

(def nnt-icon
  (n-icon {:name  "md-keypad"
           :key   2
           :style {:color (:accent (get-palette @palette-index))}
           }))

(def bars-icon
  (n-icon {:name  "ios-stats"
           :key   2
           :style {:width 9
                   ;:paddingRight 5
                   :color (:accent (get-palette @palette-index))
                   }}))

(defn select-palette-item! [index]
  (n-list-item
    {:key index}
    (radio {:key      1
            :onPress  #(reset! palette-index index)
            :selected (= @palette-index index)})
    (txt {:key 2} (palette-titles index))))

(defn add-card! [palette]
  (card {:key   20
         :style {:flex   1
                 :margin 20}}
        (card-item {:header true
                    :key    1
                    :style  {:backgroundColor "white"
                             :justifyContent  "flex-start"
                             :flexDirection   "row"
                             :alignItems      "center"
                             }}
                   add-icon
                   (txt {:style {:flex  4
                                 :marginLeft 34
                                 :color (:secondary-text palette)}}
                        (capitalize "Pull down to update, or add your own story")))
        )

  )

(defn story-card! [palette index]
  (card {:key   index
         :style {:flex   1
                 :margin 20}}
        (card-item {:header true
                    :key    1
                    :style  {:backgroundColor "white"
                             :justifyContent  "flex-start"
                             :flexDirection   "row"
                             :alignItems      "center"
                             }}
                   (icon-example (story-icon (@stories index)))
                   (txt {:style {:flex  4
                                 :color (:secondary-text palette)}}
                        (capitalize (story (@stories index)))))
        (card-item {:cardBody true
                    :key      3
                    :style    {:backgroundColor (:text-icons palette)
                               :justifyContent  "space-around"
                               :flexDirection   "row"
                               :alignItems      "center"
                               :paddingBottom   10
                               :paddingTop      0
                               }}

                   (button {:key       1
                            :bordered  true
                            :small     true
                            :textStyle {:color (:dark-primary palette)}
                            :style     {:borderWidth 2
                                        :borderColor (:dark-primary palette)
                                        }}
                           "Edit"
                           edit-icon)
                   (button {:key       2
                            :bordered  true
                            :small     true
                            :textStyle {:color (:accent palette)}
                            :style     {:borderWidth 2
                                        :borderColor (:accent palette)
                                        }}
                           nnt-icon
                           "Show"
                           )
                   (button {:key       3
                            :bordered  true
                            :small     true
                            :textStyle {:color (:accent palette)}
                            :style     {:borderWidth 2
                                        :borderColor (:accent palette)
                                        }}
                           bars-icon
                           "Show"
                           )
                   )))

(rum/defcs page < rum/reactive
                  (rum/local 0 ::selection) [state]
  (let [palette (get-palette (rum/react palette-index))
        stories (rum/react stories)
        story-count (count stories)
        palette-count (count palettes)]
    (container
      {:style {:flex 1}}
      (content
        {:key   1
         :theme (aget my-theme "default")
         :style {:flex            1
                 :backgroundColor (:primary palette)}}
        (status-bar {:key      story-count
                     :hidden   false
                     :barStyle "light-content"})

        (apply n-list
               (concat [{:key   1
                         :style {:flex 1}}]
                       [(add-card! palette)]
                       [(map (partial story-card! palette) (range story-count))]))
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