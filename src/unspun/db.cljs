(ns unspun.db
  (:require [rum.core :as rum]
            [clojure.pprint :refer [cl-format]]))

(def max-nn 1000)                                           ; maximum number needed to treat



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

              })

;;;
;; APP-STATE
;;;
(defonce app-state (atom {:palette-index   0
                          :brand-title     "Winton Centre"
                          :app-banner      "Relative Risks Unspun"
                          :scenario        :hrt5
                          ;:exposure        :bacon
                          ;:relative-risk   1.18
                          ;:baseline-risk   0.06
                          ;:evidence-source "enter evidence source"
                          }))

(def palette-index (rum/cursor-in app-state [:palette-index]))
(def brand-title (rum/cursor-in app-state [:brand-title]))
(def app-banner (rum/cursor-in app-state [:app-banner]))
(def scenario (rum/cursor-in app-state [:scenario]))


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

(defn increases? [a b] (if (> a b) "decrease" "increase"))

(def scenarios {:bacon {:subjects      ["person" "people"]
                        :risk          "their risk"
                        :exposure      "eating bacon sandwiches every day"
                        :baseline-risk 0.06
                        :relative-risk 1.18
                        :increases?    increases?
                        :outcome       "a heart attack or stroke"
                        }
                :hrt5  {:subjects      ["woman" "women in their 50s"]
                        :risk          "their lifetime risk"
                        :exposure      "taking HRT for 5 years"
                        :baseline-risk 0.1
                        :relative-risk 1.05
                        :increases?    increases?
                        :outcome       "breast cancer"
                        }
                :wine  {:subjects      ["woman" "women"]
                        :risk          "their lifetime risk"
                        :exposure      "drinking half a bottle of wine a day"
                        :baseline-risk 0.12
                        :relative-risk 1.30
                        :increases?    increases?
                        :outcome       "breast cancer"
                        }
                :wdoc  {:subjects      ["person" "US people over 65 admitted to hospital under Medicare"]
                        :risk          "their risk"
                        :exposure      "being seen by a female doctor"
                        :baseline-risk 0.115
                        :relative-risk 0.96
                        :increases?    increases?
                        :outcome       "death within 30 days of admission"
                        }})





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

(defn singular-form [[singular _]] singular)

(defn n-plural-form [[singular plural]]
  (str "~[" singular "~;" plural "~:;" plural "~]"))

(defn compare1 [subjects]
  "~@(~a~) ~a ~a ~a of ~b from ~d% to ~d%")
(defn nn1 [subjects]
  (str "On average, ~d more ~:*" (n-plural-form subjects) " ~a would mean one extra " (singular-form subjects) " experiences ~a."))
(defn nn2 [subjects]
  (str "On average, for one extra " (singular-form subjects) " to experience ~a, ~d more ~:*" (n-plural-form subjects) " would need to be ~a."))


(defn text-generator [presentation {:keys [subjects risk exposure baseline-risk relative-risk outcome]}]
  (let [brpc (to-pc baseline-risk)
        erpc (to-pc (* baseline-risk relative-risk))]
    (cond
      (= presentation compare1)
      (format (compare1 subjects) (second subjects) exposure (increases? brpc erpc) risk outcome brpc erpc)

      (= presentation nn1)
      (format (nn1 subjects) (number-needed relative-risk baseline-risk) exposure outcome)

      (= presentation nn2)
      (format (nn2 subjects) outcome (number-needed relative-risk baseline-risk) exposure)

      :else
      nil)))


(text-generator nn1 (:hrt5 scenarios))
(text-generator nn2 (:hrt5 scenarios))
(text-generator compare1 (:hrt5 scenarios))
(text-generator nn1 (:bacon scenarios))
(text-generator nn2 (:bacon scenarios))
(text-generator compare1 (:bacon scenarios))
(text-generator nn1 (:wine scenarios))
(text-generator nn2 (:wine scenarios))
(text-generator compare1 (:wine scenarios))
(text-generator nn1 (:wdoc scenarios))
(text-generator nn2 (:wdoc scenarios))
(text-generator compare1 (:wdoc scenarios))


