(ns unspun.screens.stats
  (:require-macros [rum.core :refer [defc defcs]])
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar list-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [shared.ui :refer [ionicon native-base my-theme container content n-icon txt n-list n-list-item linking]]
            [unspun.db :refer [app-state palette-index stories story-index compare-text-vector nn-text-vector to-pc clamp parse-sources]]
            [unspun.navigation.bottom-nav :refer [story-links]]
            [unspun.screens.mixins :refer [monitor]]
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

(defn show-sources [style sources]

  (let [small-link-style (merge style {:textDecorationLine "underline"})]
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

(defc page < rum/reactive []
  (let [scenar ((rum/react stories) (rum/react story-index))
        db (rum/react app-state) ; unused?
        br (:baseline-risk scenar)
        rr (:relative-risk scenar)
        brpc (to-pc br)
        rrpc (to-pc rr)
        er (* br rr)
        erpc (to-pc (clamp [0 1] er))
        palette (get-palette (rum/react palette-index))
        page-style {:flex            1
                    :backgroundColor (:primary palette)}]

    (let [text-style {:color (:text-icons palette)}
          small-text-style (merge text-style {:fontSize 14})
          outcome-type (:outcome-type scenar)
          br-sources (parse-sources (:sources-baseline-risk scenar))
          rr-sources (parse-sources (:sources-relative-risk scenar))]
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
             :style {:flex 1}}
            (n-list-item
              {:key (gensym "item")}
              (txt {:key   1
                    :style text-style}
                   (str "Background " outcome-type ": " (percentage br) " (baseline " outcome-type ": " (sigfigs 2 br) ")\n\n")
                   (show-sources small-text-style br-sources))
              )
            (n-list-item
              {:key (gensym "item")}
              (txt {:key   1
                    :style text-style}
                   (str (if (> rr 1) (str "Increased " outcome-type ": " (percentage (- rr 1)))
                                     (str "Decreased " outcome-type ": " (percentage (- 1 rr)))) " (relative " outcome-type ": " (sigfigs 2 rr) ")\n\n")
                   (show-sources small-text-style rr-sources)))

            (n-list-item
              {:key (gensym "item")}
              (txt {:style text-style} (compare-text-vector scenar)))

            (n-list-item
              {:key (gensym "item")}
              (txt {:style text-style} (nn-text-vector scenar)))))))))