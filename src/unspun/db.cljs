(ns unspun.db
  (:require [rum.core :as rum]
            [clojure.pprint :refer [cl-format]]))

(def max-nn 1000)                                           ; maximum number needed to treat

#_(def women-doctors "Hormone Replacement Therapy
People: women in their 50s

Exposure: take HRT for 5 years

Event: breast cancer in their lifetime

relative risk: 1.05
baseline risk : 10%

People: US over-65s admitted to hospital under Medicare
Exposure: being seen by a female doctor
Event: Die within 30 days of admission
Relative risk: 0.96
Baseline risk 11.5%
")


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
                               :wdoc "being seen by a female doctor"
                               :custom "enter exposure"}

              :future-events #{:none "no outcome"
                               :cardio10 "heart attack or stroke in 10 years"
                               :breast "breast cancer"
                               :death-on-admission "die within 30 days of admission"
                               :custom "enter future event"}

              :graphics      #{:text-only
                               :bars
                               :stacked-icons
                               :scattered-icons}})

(defn get-icon-set [kind style] {})

(def bacon-bar "Without bacon sandwiches, the risk of ~a is ~d%, ~a to ~d% with ")
(def bacon-nn "On average ~r more ~:*~[people~;person~:;people~] ~a would mean one extra person experiences ~a.")
(def format (partial cl-format nil))

(defn increasing? [a b] [a (if (> a b) "increasing" "decreasing") b])
(format bacon-bar "a heart attack or stroke" 6 "increasing" 7)
(format bacon-nn 2400 "eating bacon sandwiches every day" "a heart attack or stroke")

(def presets2 {:bacon {:compare       (str "Without bacon sandwiches, the risk of heart attack or stroke is "
                                           ;brpc
                                           "%, "
                                           ;(if (< brpc erpc) "increasing" "decreasing")
                                           " to "
                                           ;erpc
                                           "% with bacon sandwiches")
                       :nnt           ""                    ;If [round(1/(P1 - P0))] people like you ate [A] we would have one extra person experiencing [B].
                       :number-needed "On average, number-needed more people must eat a bacon sandwich every day before we see one extra heart attack or stroke"}})


(defn summary-text [scenario presentation]
  (get-in presets2 scenario presentation))

;;;
;; APP-STATE
;;;
(defonce app-state (atom {:palette-index   0
                          :brand-title     "Winton Centre"
                          :app-banner      "Relative Risks Unspun"
                          :icon-set        (get-icon-set :women :head)
                          :future-event    :cardio10
                          :exposure        :bacon
                          :relative-risk   1.18
                          :baseline-risk   0.06
                          :evidence-source "enter evidence source"
                          :graphic         :bars}))

(def palette-index (rum/cursor-in app-state [:palette-index]))
(def brand-title (rum/cursor-in app-state [:brand-title]))
(def app-banner (rum/cursor-in app-state [:app-banner]))
(def icon-set (rum/cursor-in app-state [:icon-set]))
(def future-event (rum/cursor-in app-state [:future-event]))
(def exposure (rum/cursor-in app-state [:exposure]))
(def relative-risk (rum/cursor-in app-state [:relative-risk]))
(def baseline-risk (rum/cursor-in app-state [:baseline-risk]))

;;;
;; DERIVED STATE
;;;
(defn number-needed [rr br]
  (let [effect (- (* rr br) br)]
    (if (zero? effect)
      max-nn
      (let [nn-val (Math.abs (/ 1 effect))]
        (cond
          (> nn-val max-nn) max-nn
          (< nn-val 0) 0
          :else (Math.round nn-val))))))

(defn clamp
  "clamp v to the range [a,b]"
  [a b v]
  (if (<= a b)
    (Math.max a (Math.min b v))
    a))

(defn to-pc
  "convert x to a percentage"
  [x]
  (Math.round (* 100 x)))

