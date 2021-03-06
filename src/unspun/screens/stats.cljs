(ns unspun.screens.stats
  (:require-macros [rum.core :refer [defc defcs]])
  (:require [rum.core :as rum]
            [clojure.string :refer [capitalize]]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar list-view scroll-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [shared.ui :refer [native-base my-theme container content n-icon txt n-list n-list-item linking tffsz get-dimensions]]
            [shared.icons :refer [ionicon]]
            [unspun.db :refer [app-state palette-index stories story-index compare-text-vector nn-text-vector to-pc clamp parse-sources dimensions caps-tidy story]]
            [unspun.navigation.bottom-nav :refer [story-links]]
            [unspun.screens.mixins :refer [monitor]]
            [unspun.screens.scenario-title-view :refer [scenario-title]]
            ))

(defn edit-icon [palette]
  (n-icon {:name  "ios-arrow-dropright-outline"
           :key   2
           :style {:color     (:accent palette)
                   :transform [{:scale 2}]}
           }))

(defc header < rum/reactive []
  (let [palette (get-palette (rum/react palette-index))
        header-style {:height          23
                      :backgroundColor (:dark-primary palette)}]
    (view {:style header-style})))

(defn percentage [value]
  (str (to-pc value) "%"))

(defn sigfigs [precision value]
  (.toPrecision (js/Number. value) precision))

(empty? "a")

;; todo - refactor
(defn iscale
  [w]
  (/ (* w tffsz) 16))

(def icon-offset (iscale 70))

(defn story-icon [palette name]
  (n-icon {:name  name
           :key   (gensym "story")
           :style {:flex  0
                   :width 30
                   ;:borderWidth 1
                   ;:borderColor "red"
                   :color (:text-icons palette)
                   ;:transform  [{:scale (iscale 1.5)}]
                   }
           }))

(defn show-icon
  [palette]
  (n-icon {:name  "ios-open-outline"
           :key   (gensym "icon-foo")
           :style {:width           30
                   :flex            0
                   ;:flex 1
                   ;:justifyContent "flex-end"
                   :marginLeft      0
                   :paddingLeft     0
                   :paddingRight    0
                   :textAlign       "left"
                   :backgroundColor "rgba(0,0,0,0)"
                   ;:borderWidth     1
                   ;:borderColor     "red"
                   :color           (:text-icons palette)
                   :transform       [{:scale 1}]}
           }))

;; end duplicate

(defn show-sources [style sources palette]
  (when (and sources (pos? (count sources)))
    (view {:style {:flexDirection  "row"
                   :justifyContent "space-between"
                   :alignItems     "center"
                   :padding        tffsz
                   :borderRadius   tffsz
                   :borderWidth    1
                   :borderColor    (:color style)}}
          (view {:style {:paddingRight tffsz
                         :flex         0.8
                         :left         (* 0.5 tffsz)}}
                (let [small-link-style style]
                  #_(txt {:key   "first"
                          :style style} "Source")
                  (map (fn [source] (when source
                                      (when-not (empty? (:prefix source))
                                        (txt {:key   2
                                              :style style}
                                             (:prefix source)))
                                      (when-not (empty? (:link source))
                                        (txt {:key     3
                                              :style   small-link-style
                                              :onPress #(.openURL linking (:url source))}
                                             (str " " (:link source))))))
                       sources)))
          (view {:style {:flex 0.2}} (show-icon palette)))))


(defc page < rum/reactive []
  (let [index (rum/react story-index)
        scenar ((rum/react stories) index)
        db (rum/react app-state)                            ; unused?
        br (:baseline-risk scenar)
        rr (:relative-risk scenar)
        brpc (to-pc br)
        rrpc (to-pc rr)
        er (* br rr)
        erpc (to-pc (clamp [0 1] er))
        palette (get-palette (rum/react palette-index))
        {w :width h :height} (rum/react dimensions)
        portrait (> h w)
        page-style {:flex            1
                    :backgroundColor (:primary palette)}

        text-field (fn [palette-key weight content]
                     (text {:key   (gensym "text-field")
                            :style {:color      (palette-key palette)
                                    :fontWeight weight
                                    }} content))

        text-style {:color    (:text-icons palette)
                    :fontSize tffsz}
        small-text-style (merge text-style {:fontSize           tffsz
                                            ;:fontWeight "bold"
                                            :textDecorationLine "underline"})
        outcome-type (:outcome-type scenar)
        br-sources (parse-sources (:sources-baseline-risk scenar))
        rr-sources (parse-sources (:sources-relative-risk scenar))]
    (view {:style {:flex          1
                   :flexDirection (if portrait "column" "row")}}


          (view {:key   1
                 :style {:flex            (if portrait 0.3 0.5)
                         :flexDirection   "column"
                         :justifyContent  "space-around"
                         :alignItems      "stretch"
                         :backgroundColor (:dark-primary palette)
                         }}
                (story-links palette)
                (scroll-view {:style {:backgroundColor (:dark-primary palette)
                                      :padding         0
                                      :flex            0.6}}
                             (scenario-title (:title scenar) text-field (:qoe scenar))
                             (view {:style {:flex           1
                                            :flexDirection  "row"
                                            :justifyContent "flex-start"
                                            :alignItems     "center"
                                            :padding        0}}
                                   #_(view {:style {:flex       0
                                                    :marginLeft tffsz}}
                                           (story-icon palette (:icon scenar)))
                                   (view {:style {:flex         1
                                                  :width        w ;(- w icon-offset 60)
                                                  :paddingLeft  20
                                                  :paddingRight 20
                                                  ;:borderWidth 1
                                                  ;:borderColor "red"
                                                  }}
                                         (txt {:key   2
                                               :style {:fontWeight "normal"
                                                       :fontSize   tffsz
                                                       :color      (:text-icons palette)}}
                                              (caps-tidy (story index)))))
                             )
                )





          (view {:key   2
                 :style {:flex (if portrait 0.7 0.5)}}
                (container
                  {:key   "stats-container"
                   :style {:flex            1
                           :backgroundColor (:primary palette)
                           }}
                  (content
                    {:key   "stats-content"
                     :theme (aget my-theme "default")
                     :style {:flex 1}}



                    (n-list
                      {:key   "stats-list"
                       :style {:flex 1
                               ;:paddingLeft  (* 3 tffsz)
                               ;:paddingRight (* 3 tffsz)
                               }}

                      (n-list-item
                        {:key (gensym "item")}
                        (view {:flex 1}
                              (txt {:key   1
                                    :style (merge text-style {:fontWeight "bold"})}
                                   (str "Background or baseline " outcome-type ": " (percentage br) "\n")
                                   )
                              (show-sources small-text-style br-sources palette))
                        )
                      (n-list-item
                        {:key (gensym "item")}
                        (view {:flex 1}
                              (txt {:key   1
                                    :style (merge text-style {:fontWeight "bold"})}
                                   (str "Relative " outcome-type ": " (sigfigs 3 rr) "\n"
                                        "(" (capitalize outcome-type)
                                        (if (> rr 1) (str " increased by " (percentage (- rr 1)) ")")
                                                     (str " decreased by " (percentage (- 1 rr)) ")"))
                                        )
                                   )
                              (show-sources small-text-style rr-sources palette))
                        )

                      (n-list-item
                        {:key (gensym "item")}
                        (txt {:style text-style} (compare-text-vector scenar)))

                      (n-list-item
                        {:key (gensym "item")}
                        (txt {:style text-style} (nn-text-vector scenar))))))))))