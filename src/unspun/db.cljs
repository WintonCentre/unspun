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


(def compare1 "~@(~a~) ~a the risk of ~b from ~d% to ~d%")
(def nn1 "On average ~r more ~:*~[people~;person~:;people~] ~a would mean one extra person experiences ~a.")
(def nn2 "On average, for one extra person to experience ~a, woulr require ~r more ~:*~[people~;person~:;people~] ~a.")


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


;;;;;


(def format (partial cl-format nil))

(defn increases? [a b] (if (> a b) "decreases" "increases"))

(def scenarios {:bacon {:exposure      "eating bacon sandwiches every day"
                        :baseline-risk 0.06
                        :relative-risk 1.18
                        :increases?    increases?
                        :outcome       "a heart attack or stroke"
                        :cmp-form      [compare1 :exposure :increases? :outcome]
                        :nn-form       [nn1 :number-needed :outcome]}
                :hrt5  {:exposure      "taking HRT for 5 years"
                        :baseline-risk 0.1
                        :relative-risk 0.93
                        :increases?    increases?
                        :outcome       "ostioporosis"
                        :cmp-form      [compare1 :exposure :increases? :outcome]
                        :nn-form       [nn2 :outcome :number-needed]}
                :wine  {:exposure      "drinking half a bottle of wine a day"
                        :baseline-risk 0.06
                        :relative-risk 0.07
                        :increases?    increases?
                        :outcome       "breast cancer"
                        :cmp-form      [compare1 :exposure :increases? :outcome]
                        :nn-form       [nn1 :number-needed :outcome]}
                :wdoc  {:exposure      "being seen by a woman doctor"
                        :baseline-risk 0.1
                        :relative-risk 0.9
                        :increases?    increases?
                        :outcome       "death within 30 days of admission"
                        :cmp-form      [compare1 :exposure :increases? :outcome]
                        :nn-form       [nn1 :number-needed :outcome]}
                })

(defn text-generator [presentation {:keys [exposure baseline-risk relative-risk increases? outcome]}]
  (let [brpc (to-pc baseline-risk)
        erpc (to-pc (* baseline-risk relative-risk))]
    (cond
      (= presentation compare1)
      (format compare1 exposure (increases? brpc erpc) outcome brpc erpc)

      (= presentation nn1)
      (format nn1 (number-needed relative-risk baseline-risk) exposure outcome)

      :else
      nil)))

(format compare1 "eating bacon sandwiches every day" (increases? 6 7) "a heart attack or stroke" 6 7)
(format nn1 2400 "eating bacon sandwiches every day" "a heart attack or stroke")


(prn (text-generator nn1 (:hrt5 scenarios)))
(prn (text-generator compare1 (:hrt5 scenarios)))
(prn (text-generator nn1 (:bacon scenarios)))
(prn (text-generator compare1 (:bacon scenarios)))
(prn (text-generator nn1 (:wine scenarios)))
(prn (text-generator compare1 (:wine scenarios)))
(prn (text-generator nn1 (:wdoc scenarios)))
(prn (text-generator compare1 (:wdoc scenarios)))


