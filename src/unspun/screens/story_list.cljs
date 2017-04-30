(ns unspun.screens.story-list
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar animated-view refresh-control] :as rn]
            [shared.ui :refer [ionicon native-base my-theme container content n-icon txt n-list n-list-item radio card card-item button add-page-title]]
            [unspun.db :refer [app-state palette-index stories story-index story caps-tidy]]
            [themes.palettes :refer [palettes get-palette]]
            ))

(def palette-titles
  (mapv name (keys palettes)))

#_(rum/defc icon-example [name]
  (content {:theme (aget my-theme "default")}
           (n-icon {:name  name
                    :style {:color (:dark-primary (get-palette @palette-index))}})))

(defn story-icon [palette name]
  (n-icon {:name  name
           :key   (gensym "story")
           :style {:color (:dark-primary palette)}
           }))

(defn edit-icon [palette]
  (n-icon {:name  "ios-create"
           :key   (gensym "edit")
           :style {:color (:dark-primary palette)}
           }))

(defn show-icon [palette]
  (n-icon {:name  "ios-arrow-dropright-outline"
           :key   (gensym "icon-foo")
           :style {:color     (:accent palette)
                   :transform [{:scale 1.67}]}
           }))

(defn add-icon [palette]
  (n-icon {:name  "ios-add-circle-outline"
           :key   (gensym "add")
           :style {:color     (:accent palette)
                   :transform [{:scale 1.67}]}}))

(defn select-palette-item! [index]
  (n-list-item
    {:key index}
    (radio {:key      1
            :onPress  #(reset! palette-index index)
            :selected (= @palette-index index)})
    (txt {:key 2} (palette-titles index))))

(def card-style {:flex         1
                 :marginLeft   15
                 :marginRight  15
                 :marginTop    5
                 :marginBottom 0})

(def card-item-style {:backgroundColor "white"
                      :justifyContent  "flex-start"
                      :flexDirection   "row"
                      :alignItems      "center"
                      :padding         10})

(defn add-card! [navigator palette]
  (card {:key   (gensym "add-card")
         :style card-style}
        (card-item {:header true
                    :key    1
                    :style  card-item-style}
                   (txt {:key   1
                         :style {:flex       4
                                 :marginLeft 34
                                 :fontWeight "normal"
                                 :color      (:secondary-text palette)}}
                        "Add your own scenario")
                   (button {:key       2
                            :bordered  true
                            :style     {:borderWidth 0
                                        :borderColor "white"}
                            :onPress   #(.push navigator "not-yet")}
                           (add-icon palette)))))

(defn story-card! [navigator palette index]
  (card {:key   (gensym "story-card")
         :style card-style}
        (card-item {:header true
                    :key    1
                    :style  card-item-style}
                   (story-icon palette (:icon (@stories index)))
                   (txt {:key   1
                         :style {:flex       4
                                 :fontWeight "normal"
                                 :color      (:secondary-text palette)}}
                        (caps-tidy (story index)))
                   (button {:key       2
                            :bordered  true
                            :style     {:borderWidth 0
                                        :borderColor "white"}
                            :onPress   #(do (reset! story-index index)
                                            (.push navigator "tabs"))}
                           (show-icon palette)))))

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
        {:key             1
         :theme           (aget my-theme "default")
         :style           {:flex            1
                           :backgroundColor (:primary palette)}
         :refreshControl (refresh-control {
                                            :onRefresh #(.log js/console "Refreshing")})
         }
        (status-bar {:key      (gensym "stories")
                     :hidden   false
                     :barStyle "light-content"})
        (apply n-list
               (concat [{:key   1
                         :style {:flex 1}
                         }]
                       [(add-card! navigator palette)]
                       ;[(story-card! navigator palette 0)]
                       [(map (partial story-card! navigator palette) (range story-count))]))))))