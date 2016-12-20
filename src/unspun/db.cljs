(ns unspun.db
  (:require [rum.core :as rum]))

(def max-nn 100)                                            ; maximum number needed to treat

(def presets {:icon-moods    #{:ok
                               :happy
                               :sad
                               :dead
                               }

              :people-styles #{:neurath
                               :head
                               :front
                               :side
                               :style1
                               :style2}

              :people-kinds  #{:women
                               :men
                               :adults
                               :girls
                               :boys
                               :children
                               :babies
                               :anybody}

              :icon-types    #{:people
                               :emoji
                               :geometric}

              :exposures     #{:bacon "eating a bacon sandwich"
                               :hrt5 "taking HRT for 5 years"
                               :wine "drinking half a bottle of wine per day"
                               :custom "enter exposure"}

              :future-events #{:none "no outcome"
                               :cardio10 "heart attack or stroke in 10 years"
                               :breast "breast cancer"
                               :custom "enter future event"}

              :graphics      #{:text-only
                               :bars
                               :stacked-icons
                               :scattered-icons}})

(defn get-icon-set [kind style] {})


;;;
;; APP-STATE
;;;
(defonce app-state (atom {:palette-index   0
                          :brand-title     "Winton Centre"
                          :icon-set        (get-icon-set :women :head)
                          :future-event    :cardio10
                          :exposure        :bacon
                          :relative-risk   1.18
                          :baseline-risk   0.06
                          :evidence-source "enter evidence source"
                          :graphic         :bars}))

(def palette-index (rum/cursor-in app-state [:palette-index]))
(def brand-title (rum/cursor-in app-state [:brand-title]))
(def icon-set (rum/cursor-in app-state [:icon-set]))
(def future-event (rum/cursor-in app-state [:future-event]))
(def exposure (rum/cursor-in app-state [:exposure]))
(def relative-risk (rum/cursor-in app-state [:relative-risk]))
(def baseline-risk (rum/cursor-in app-state [:baseline-risk]))

;;;
;; DERIVED STATE
;;;
(def *exposed-risk
  (rum/derived-atom [relative-risk baseline-risk] ::er
                    (fn [rr br] (* rr br))))

(def *number-needed
  (rum/derived-atom [relative-risk baseline-risk] ::nn
                    (fn [rr br]
                      (let [effect (- (* rr br) br)]
                        (if (zero? effect)
                          max-nn
                          (let [nn-val (Math.abs (/ 1 effect))]
                            (cond
                              (> nn-val max-nn) max-nn
                              (< nn-val 0) 0
                              :else (Math.round nn-val))))))))

(defn to-pc [x] (Math.round (* 100 x)))

(def *baseline-risk-percent
  (rum/derived-atom [baseline-risk] ::brpc
                    (fn [br] (to-pc br))))

(def *exposed-risk-percent
  (rum/derived-atom [*exposed-risk] ::erpc
                    (fn [er] (to-pc er))))

