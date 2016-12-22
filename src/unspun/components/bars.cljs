(ns unspun.components.bars
  (:require-macros [rum.core :refer [defc]])
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index baseline-risk relative-risk to-pc clamp]]
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
  (let [br (rum/react baseline-risk)
        rr (rum/react relative-risk)
        brpc (to-pc br)
        er (* br rr)
        erpc (to-pc (clamp 0 1 er))
        palette (get-palette (rum/react palette-index))
        page-style {:flex             1
                    :backgroundColor (:primary palette)}]
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
                                 brpc
                                 "%, "
                                 (if (< brpc erpc) "increasing" "decreasing")
                                 " to "
                                 erpc
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
                            (view {:style {:flex     (- 1 br)
                                           :position "relative"}})
                            (view {:style {:flex            br
                                           :backgroundColor (:light-primary palette)}}
                                  (view {:style {:flex          1
                                                 :flexDirection "row"
                                                 :justifyContent "center"}}
                                        (text {:style {:color      (:dark-primary palette)
                                                       :fontSize   26
                                                       :flex 1
                                                       :textAlign "center"
                                                       :fontWeight "400"}
                                               } (str brpc "%")))))
                      (view {:style {:flex 0.04}})
                      (view {:style {:flex 0.2}}
                            (view {:style {:flex (- 1 er)}}
                                  (text {:style {:color (:text-icons palette)
                                                 :position "absolute"
                                                 :bottom 0
                                                 :paddingLeft 2
                                                 :fontSize 26
                                                 :fontWeight "400"
                                                 :textAlign "center"}
                                         } (str erpc "%")))
                            (view {:style {:flex            er
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