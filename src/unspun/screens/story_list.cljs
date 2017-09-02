(ns unspun.screens.story-list
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar animated-view refresh-control] :as rn]
            [shared.ui :refer [ionicon native-base my-theme container content n-icon txt n-list n-list-item radio card card-item button add-page-title tffsz]]
            [unspun.db :refer [app-state dimensions refreshing palette-index stories story-index story caps-tidy scenario-url flash-error]]
            [unspun.screens.scenario-title-view :refer [scenario-title]]
            [themes.palettes :refer [palettes get-palette]]
            [shared.non-phantom :refer [slurp-csv]]
            [shared.client :refer [store-csv]]
            ))

(def palette-titles
  (mapv name (keys palettes)))

#_(rum/defc icon-example [name]
    (content {:theme (aget my-theme "default")}
             (n-icon {:name  name
                      :style {:color (:dark-primary (get-palette @palette-index))}})))

(defn refresh-icon [palette]
  (n-icon {:name  "ios-cloud-download"
           :style {:color    "white"
                   :fontSize 30}}))

(defn iscale
  [w]
  (/ (* w tffsz) 16))

(def icon-offset (+ (iscale 30) (* 1.67 (iscale 25))))

(defn story-icon [palette name]
  (n-icon {:name  name
           :key   (gensym "story")
           :style {:flex 0
                   :width 30
                   :marginLeft 10
                   :borderWidth 1
                   :borderColor "red"
                   :color (:dark-primary palette)
                   :transform [{:scale (iscale 1)}]}
           }))

(defn edit-icon [palette]
  (n-icon {:name  "ios-create"
           :key   (gensym "edit")
           :style {:color (:dark-primary palette)}
           }))


(defn show-icon
  [palette]
  (n-icon {:name  "ios-arrow-dropright-outline"
           :key   (gensym "icon-foo")
           :style {:width           25
                   :flex            0
                   ;:flex 1
                   ;:justifyContent "flex-end"
                   :marginLeft      0
                   :paddingLeft     0
                   :paddingRight    0
                   :textAlign       "right"
                   :backgroundColor "rgba(0,0,0,0)"
                   :borderWidth     1
                   :borderColor     "red"
                   :color           (:accent palette)
                   :transform       [{:scale (iscale 1.67)}]}
           }))

(defn add-icon [palette]
  (n-icon {:name  "ios-add-circle-outline"
           :key   (gensym "add")
           :style {:color     (:accent palette)
                   :flex      1
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

(def card-item-style {                                      ;:backgroundColor "blue"
                      :justifyContent  "flex-start"
                      :flexDirection   "row"
                      :alignItems      "center"
                      :borderWidth     0
                      :borderColor     "rgba(0,0,0,0)"
                      :width           "100%"
                      :padding         10})

(defn refresh-list []
  (do
    (swap! app-state assoc :refreshing true)
    (slurp-csv @scenario-url
               (fn [csv]
                 (store-csv {:creator @scenario-url} csv)
                 (swap! app-state assoc :refreshing false))
               flash-error))
  )

(rum/defc add-card! [navigator palette]
  (card {:style card-style}
        (card-item {:header  false
                    :key     1
                    :onPress refresh-list
                    :style   (merge card-item-style {:flexDirection   "row"
                                                     :justifyContent  "space-between"
                                                     :height          40
                                                     :width           "100%"
                                                     :marginLeft      -1
                                                     ;:marginTop -1
                                                     :marginRight     -1
                                                     ;:marginBottom -1
                                                     :padding         0
                                                     :borderWidth     5
                                                     :borderColor     (:accent palette) ;"rgba(0,0,0,0)"
                                                     :backgroundColor (:accent palette)})}
                   (txt {:key   1
                         :style {:fontWeight "bold"
                                 :marginLeft 35
                                 :color      "white"}}
                        "Refresh List")
                   (button {:key      2
                            :bordered true
                            :style    {;:flex 1
                                       :height      20
                                       :borderWidth 0
                                       :borderColor "rgba(0,0,0,0)"}
                            :onPress  refresh-list}
                           (refresh-icon palette)))))

(rum/defc story-card! < rum/reactive [navigator palette index]
  (let [scenar ((rum/react stories) index)
        w (:width (rum/react dimensions))
        text-field (fn [palette-key weight content]
                     (text {:key   (gensym "text-field")
                            :style {:color      (palette-key palette)
                                    :fontWeight weight
                                    }} content))]

    (card {:style card-style}
          (card-item {:header  true
                      :key     1
                      :style   card-item-style
                      :onPress #(do (reset! story-index index)
                                    (.push navigator "tabs"))
                      }
                     (story-icon palette (:icon scenar))
                     (view {:style {:width (- w icon-offset 20)
                                    :padding 10
                                    :borderWidth 1
                                    :borderColor "red"}}
                           (txt {:key   1
                                 :style {;:flex       0.5
                                         :fontWeight "bold"
                                         :fontSize   tffsz
                                         :color      (:secondary-text palette)}}
                                (:title scenar))
                           (txt {:key   2
                                 :style {;:flex       4
                                         :fontWeight "normal"
                                         :fontSize   tffsz
                                         :color      (:secondary-text palette)}}
                                (caps-tidy (story index))))
                     (show-icon palette)
                     #_(button {:bordered true
                                :style    {:borderWidth 0
                                           :borderColor "white"}
                                :onPress  #(do (reset! story-index index)
                                               (.push navigator "tabs"))}
                               (show-icon palette))))))

(rum/defcs page < rum/reactive
                  (rum/local 0 ::selection)
                  (add-page-title "Scenarios")
  [state]
  (let [palette (get-palette (rum/react palette-index))
        stories (rum/react stories)
        story-count (count stories)
        palette-count (count palettes)
        navigator (aget (:rum/react-component state) "props" "navigator")]
    (println "new stories")
    (container
      {:style {:flex 1}}
      (content
        {:key            1
         :theme          (aget my-theme "default")
         :style          {:flex            1
                          :backgroundColor (:primary palette)}

         :refreshControl (refresh-control {:refreshing (rum/react refreshing)
                                           :onRefresh  refresh-list})
         }
        (status-bar {:key      (gensym "stories")
                     :hidden   false
                     :barStyle "light-content"})


        (apply n-list
               (->> [(map-indexed #(rum/with-key (story-card! navigator palette %2) %1) (range story-count))]
                    (cons (rum/with-key (add-card! navigator palette) "refresher"))
                    (cons {:key 1 :style {:flex 1}}))


               #_(concat [{:key   1
                           :style {:flex 1}
                           }]
                         [(rum/with-key (add-card! navigator palette) "refresher")]
                         ;[(story-card! navigator palette 0)]
                         [(map-indexed #(rum/with-key (story-card! navigator palette %2) %1) (range story-count))]))))))