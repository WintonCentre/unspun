(ns unspun.components.bars
  (:require-macros [rum.core :refer [defc]])
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index baseline-risk relative-risk *baseline-risk-percent *exposed-risk *exposed-risk-percent]]
            [graphics.svg :refer [svg circle rect]]
            ))

(def header-height 23)

(defc header < rum/reactive []
  (let [palette (get-palette (rum/react palette-index))
        header-style {:height           header-height
                      :backgroundColor (:dark-primary palette)}]
    (view {:style header-style})
    ))

(defc page < rum/reactive []
  (let [palette (get-palette (rum/react palette-index))
        page-style {:flex             1
                    :backgroundColor (:primary palette)

                    }]
    (view {:style {:flex 1}}
          (header)
          (view {:style page-style}
                (view {:style {:flex 0.3
                               :backgroundColor (:dark-primary palette)}}
                      (text {:style {:color      (:light-primary palette)
                                     :fontWeight "400"
                                     :padding 20
                                     :fontSize   24}}
                            (str "Without bacon sandwiches, the risk of heart attack or stroke is "
                                 (rum/react *baseline-risk-percent)
                                 "%, "
                                 (if (< (rum/react *baseline-risk-percent) (rum/react *exposed-risk-percent)) "increasing" "decreasing")
                                 " to "
                                 (rum/react *exposed-risk-percent)
                                 "% with bacon sandwiches"
                                 )))
                (view {:style {:flex          0.6
                               :flexDirection "row"
                               ;:backgroundColor "pink"
                               }}
                      (view {:style {:flex 0.3
                                     :justifyContent "center"}}
                            (text {:style {:color (:text-icons palette)
                                           :fontSize 24
                                           :textAlign "right"
                                           :paddingRight 10}}
                                  "Without"))
                      (view {:style {:flex 0.2}}
                            (view {:style {:flex (- 1 (rum/react baseline-risk)) ;; 0.3
                                           :position "relative"}}
                                  (text {:style {:color (:text-icons palette)
                                                 :position "absolute"
                                                 :bottom 0
                                                 :fontSize 30
                                                 :fontWeight "400"}
                                         } (str (rum/react *baseline-risk-percent) "%")))
                            (view {:style {:flex (rum/react baseline-risk)
                                           :backgroundColor (:light-primary palette)}}))
                      (view {:style {:flex 0.04}})
                      (view {:style {:flex 0.2}}
                            (view {:style {:flex (- 1 (rum/react *exposed-risk))}}
                                  (text {:style {:color (:text-icons palette)
                                                 :position "absolute"
                                                 :bottom 0
                                                 :fontSize 30
                                                 :fontWeight "400"
                                                 :textAlign "center"}
                                         } (str (rum/react *exposed-risk-percent) "%")))
                            (view {:style {:flex (rum/react *exposed-risk)
                                           :backgroundColor (:light-primary palette)}}))
                      (view {:style {:flex 0.3
                                     :justifyContent "center"}}
                            (text {:style {:color (:text-icons palette)
                                           :fontSize 24
                                           :padding 10
                                           :textAlign "left"}}
                                  "With")))
                (view {:style {:flex 0.1
                               :backgroundColor (:dark-primary palette)}})))))