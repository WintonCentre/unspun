(ns unspun.components.bars
  (:require-macros [rum.core :refer [defc]])
  (:require [rum.core :as rum]
            [cljs-exponent.components :refer [text view image touchable-highlight] :as rn]
            [themes.palettes :refer [get-palette]]
            [unspun.db :refer [app-state palette-index]]
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
                                     :fontSize   26}}
                            "Without treatment, the risk of heart damage is 70%, decreasing to 60% with treatment"))
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
                            (view {:style {:flex 0.3
                                           :position "relative"}}
                                  (text {:style {:color (:text-icons palette)
                                                 :position "absolute"
                                                 :bottom 0
                                                 :fontSize 30
                                                 :fontWeight "400"}
                                         } "70%"))
                            (view {:style {:flex 0.7
                                           :backgroundColor (:light-primary palette)}}))
                      (view {:style {:flex 0.04}})
                      (view {:style {:flex 0.2}}
                            (view {:style {:flex 0.4}}
                                  (text {:style {:color (:text-icons palette)
                                                 :position "absolute"
                                                 :bottom 0
                                                 :fontSize 30
                                                 :fontWeight "400"
                                                 :textAlign "center"}
                                         } "60%"))
                            (view {:style {:flex 0.6
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