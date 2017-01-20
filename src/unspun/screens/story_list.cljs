(ns unspun.screens.story-list
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar animated-view] :as rn]
            [shared.ui :refer [ionicon native-base my-theme container content n-icon txt n-list n-list-item radio card card-item button add-page-title]]
            [unspun.db :refer [app-state palette-index stories story-index story caps-tidy]]
            [themes.palettes :refer [palettes get-palette]]
            ))

(def palette-titles
  (mapv name (keys palettes)))

(rum/defc icon-example [name]
  (content {:theme (aget my-theme "default")}
           (n-icon {:name  name
                    :style {:color (:dark-primary (get-palette @palette-index))}})))

(defn story-icon [palette name]
  (n-icon {:name  name
           :key   2
           :style {:color (:dark-primary palette)}
           }))

(defn edit-icon [palette]
  (n-icon {:name  "ios-create"
           :key   2
           :style {:color (:dark-primary palette)}
           }))

(defn nnt-icon [palette]
  (n-icon {:name  "md-keypad"
           :key   2
           :style {:color (:accent palette)}
           }))

(defn show-icon [palette]
  (n-icon {:name  "ios-arrow-dropright-outline"
           :key   2
           :style {:color     (:accent palette)
                   :transform [{:scale 1.67}]}
           }))

(defn add-icon [palette]
  (n-icon {:name  "ios-add-circle-outline"
           :key   2
           :style {:color     (:accent palette)
                   :transform [{:scale 1.67}]}
           }))


(defn bars-icon [palette]
  (n-icon {:name  "ios-stats"
           :key   2
           :style {:width 9
                   ;:paddingRight 5
                   :color (:accent palette)
                   }}))

(defn select-palette-item! [index]
  (n-list-item
    {:key index}
    (radio {:key      1
            :onPress  #(reset! palette-index index)
            :selected (= @palette-index index)})
    (txt {:key 2} (palette-titles index))))

(defn add-card! [navigator palette]
  (card {:key   20
         :style {:flex   1
                 :margin 15}}
        (card-item {:header true
                    :key    1
                    :style  {:backgroundColor "white"
                             :justifyContent  "flex-start"
                             :flexDirection   "row"
                             :alignItems      "center"
                             }}
                   (txt {:style {:flex       4
                                 :marginLeft 34
                                 :fontWeight "normal"
                                 :color      (:secondary-text palette)}}
                        "Add your own scenario")
                   (button {:key       2
                            :bordered  true
                            ;:small     true
                            :textStyle {:color (:accent palette)}
                            :style     {:borderWidth 0}
                            :onPress   #(do (.push navigator "not-yet"))}
                           (add-icon palette)
                           ""))
        )

  )

(defn story-card! [navigator palette index]
  (card {:key   index
         :style {:flex   1
                 :margin 15}}
        (card-item {:header true
                    :key    1
                    :style  {:backgroundColor "white"
                             :justifyContent  "flex-start"
                             :flexDirection   "row"
                             :alignItems      "center"
                             }}
                   (story-icon palette (:icon (@stories index)))
                   (txt {:style {:flex  4
                                 :fontWeight "normal"
                                 :color (:secondary-text palette)}}
                        (caps-tidy (story (@stories index))))
                   (button {:key       2
                            :bordered  true
                            ;:small true
                            :textStyle {:color (:accent palette)}
                            :style     {:borderWidth 0}
                            :onPress   #(do (reset! story-index index)
                                            (.push navigator "tabs"))}
                           (show-icon palette)
                           ""))
        ))



(rum/defcs page < rum/reactive
                  (rum/local 0 ::selection) (add-page-title "Scenarios")
  [state]
  (let [palette (get-palette (rum/react palette-index))
        stories (rum/react stories)
        story-count (count stories)
        palette-count (count palettes)
        navigator (aget (:rum/react-component state) "props" "navigator")]
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
                       [(add-card! navigator palette)]
                       [(map (partial story-card! navigator palette) (range story-count))]))
        )
      )))