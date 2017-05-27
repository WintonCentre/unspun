(ns unspun.screens.introduction
  (:require [cljs-exponent.components :refer [element text view image touchable-highlight status-bar scroll-view] :as rn]
            [themes.palettes :refer [get-palette]]
            [shared.ui :refer [n-icon]]
            [unspun.db :refer [app-state palette-index to-pc number-needed stories story-index]]
            [rum.core :as rum]))

(def shadow-size 1)

(rum/defcs page < rum/reactive [state]
  (let [scenar ((rum/react stories) (rum/react story-index))
        br (:baseline-risk scenar)
        rr (:relative-risk scenar)
        nn (number-needed rr br)
        palette (get-palette (rum/react palette-index))
        page-style {:flex            0.8
                    :padding         20
                    :backgroundColor (:primary palette)}
        header-style {:fontSize 24
                      :color    (:text-icons palette)
                      :marginBottom 20}
        text-style {:fontSize 18
                    :color    (:text-icons palette)
                    :marginBottom 15}
        quote-style {:fontSize 20
                     :fontStyle "italic"
                    :color    (:text-icons palette)
                    :marginBottom 15}]
    (letfn [(jumpToDrawer [drawerId route]
              (let [navigation (aget (:rum/react-component state) "props" "navigation")]
                (.performAction navigation (fn [stateUtils]
                                             (.jumpToItem ((aget stateUtils "drawer") "top-drawer") drawerId)))
                ))]

      (view {:style {:flex 1}}


            (scroll-view {:style page-style
                          :key   1}
                         (text {:style header-style}
                               "What are the numbers really telling you?")
                         (text {:style text-style}
                               "Headlines often try to persuade you with numbers - but looking at them another way can reveal a different story.")
                         (text {:style text-style}
                               "This app helps you to see the evidence for what it really is.")
                         (text {:style text-style}
                               "Take the famous statement:\n"
                               (text {:style quote-style}
                                     "“Eating bacon sandwiches increases your risk of cancer by 18%”"))
                         (text {:style text-style}
                               "If you don’t eat bacon the risk is about 6%, so if you eat bacon every day it rises to 7%, and the app plots this in a graph.")
                         (text {:style text-style}
                               "In pictures the app reveals that 93 people would have to give up eating daily bacon before 1 person would be saved from cancer.")
                         (text {:style text-style}
                               "It looks quite different when you put it like that!\n\nRefresh the list of scenarios to see our latest.\n\n See Settings to hook up your own data source.")
                         #_(text {:style text-style}
                               "Turn on notifications if you want to know when we add more examples.")
                         )
            (view {:style {:flex            0.18
                           :backgroundColor (:dark-primary palette)
                           }}
                  (touchable-highlight {:style   {:flex            1
                                                  :margin      20
                                                  :height          50
                                                  :borderColor    (:accent (get-palette (rum/react palette-index)))
                                                  :borderWidth    2
                                                  :borderRadius    30
                                                  :shadowColor     "#000"
                                                  :shadowOffset    {:width shadow-size :height shadow-size}
                                                  :shadowRadius    shadow-size
                                                  :shadowOpacity   0.5
                                                  :alignItems      "center"
                                                  :justifyContent  "center"
                                                  }
                                        :onPress #(jumpToDrawer "scenarios" "stories")}

                                       (text {:style {:color      (:text-icons (get-palette (rum/react palette-index)))
                                                      :textAlign  "center"
                                                      :fontWeight "bold"}}
                                             "Start"))

                  #_(touchable-highlight {:style {:flex            1
                                                :margin          20
                                                :backgroundColor (:accent (get-palette (rum/react palette-index)))
                                                :borderRadius    30
                                                :shadowColor     "#000"
                                                :shadowOffset    {:width shadow-size :height shadow-size}
                                                :shadowRadius    shadow-size
                                                :shadowOpacity   0.5
                                                :alignItems      "center"
                                                :justifyContent  "center"
                                                }
                                        :onPress #(jumpToDrawer "scenarios" "stories")
                                        ;#(.push (aget (:rum/react-component state) "props" "navigator") "stories")
                                        }
                                       (text {:style {:color (:text-icons (get-palette (rum/react palette-index))) :textAlign "center" :fontWeight "bold" :width 55}} "Start")))))))


