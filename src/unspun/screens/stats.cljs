(ns unspun.screens.stats
  (:require-macros [rum.core :refer [defc defcs]])
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar list-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [shared.ui :refer [ionicon native-base my-theme container content n-icon txt n-list n-list-item ]]
            [unspun.db :refer [app-state palette-index stories story-index compare-text-vector nn-text-vector to-pc clamp parse-sources]]
            [unspun.navigation.bottom-nav :refer [bottom-button-bar]]
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

(empty? "a")

(defn show-sources [style sources]
  (prn sources)
  (let [small-link-style (merge style {:color "blue" :textDecoration "underline"})]
    (when sources
      (when-not (empty? (:prefix sources))
        (txt {:key   2
              :style style}
             (:prefix sources)))
      (when-not (empty? (:link sources))
        (txt {:key     3
              :style   small-link-style
              :onPress #(js/alert (str "Visit " (:url sources)))}
             (str " " (:link sources)))))))

(defc page < rum/reactive []
  (let [scenar ((rum/react stories) (rum/react story-index))
        db (rum/react app-state)
        br (:baseline-risk scenar)
        rr (:relative-risk scenar)
        brpc (to-pc br)
        rrpc (to-pc rr)
        er (* br rr)
        erpc (to-pc (clamp [0 1] er))
        palette (get-palette (rum/react palette-index))
        page-style {:flex            1
                    :backgroundColor (:primary palette)}]

    (let [palette (get-palette (rum/react palette-index))
          text-style {:color (:text-icons palette)}
          small-text-style (merge text-style {:fontSize 10})
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

          #_(status-bar {:key      "stats-status-bar"
                       :hidden   false
                       :barStyle "light-content"})
          (n-list
            {:key   "stats-list"
             :style {:flex 1}}
            (n-list-item
              {:key (gensym "item")}
              (txt {:key   1
                    :style text-style}
                   (str "background risk: " (percentage br) " (baseline risk: " br ")")
                   (show-sources small-text-style br-sources))
              )
            (n-list-item
              {:key (gensym "item")}
              (txt {:key   1
                    :style text-style}
                   (str (if (> rr 1) (str "increase: " (percentage (- rr 1)))
                                     (str "decrease: " (percentage (- 1 rr)))) " (relative risk: " rr ")")
                   (show-sources small-text-style rr-sources)))

            (n-list-item
              {:key (gensym "item")}
              (txt {:style text-style} (compare-text-vector scenar)))

            (n-list-item
              {:key (gensym "item")}
              (txt {:style text-style} (nn-text-vector scenar)))))))))