(ns unspun.screens.stats
  (:require-macros [rum.core :refer [defc defcs]])
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [element text view image touchable-highlight status-bar list-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [shared.ui :refer [ionicon native-base my-theme container content n-icon txt n-list n-list-item radio]]
            [unspun.db :refer [app-state palette-index stories story-index compare-text-vector nn-text-vector to-pc clamp]]
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

    (let [palette (get-palette (rum/react palette-index))]
      (container
        {:key   "stats-container"
         :style {:flex            1
                 :backgroundColor "white"}}
        (content
          {:key   "stats-content"
           :theme (aget my-theme "default")
           :style {:flex 1}}
          (status-bar {:key      "stats-status-bar"
                       :hidden   false
                       :barStyle "light-content"})
          (n-list
            {:key   "stats-list"
             :style {:flex 1}}
            (n-list-item
              {:key (gensym "item")}
              (txt {:key 1} (str "background risk: " (percentage br) " (baseline risk: " br ")")))
            (n-list-item
              {:key (gensym "item")}
              (txt {:key 1} (str (if (> rr 1) (str "increase: " (percentage (- rr 1)))
                                              (str "decrease: " (percentage (- 1 rr)))) " (relative risk: " rr ")")))

            (n-list-item
              {:key (gensym "item")}
              (txt {} (compare-text-vector scenar)))

            (n-list-item
              {:key (gensym "item")}
              (txt {} (nn-text-vector scenar)))))))))