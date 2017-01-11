(ns unspun.db
  (:require [rum.core :as rum]
            [clojure.pprint :refer [cl-format]]
            [clojure.string :refer [capitalize replace]]
            ))

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
(def scenarios {:bacon   {:icon            "ios-man"
                          :subjects        ["person" "people"]
                          :risk            "their risk"
                          :exposure        "eating bacon sandwiches every day"
                          :baseline-risk   0.06
                          :relative-risk   1.18
                          :outcome         "a heart attack or stroke"
                          :evidence-source "https://wintoncentre.maths.cam.ac.uk/"
                          :fontSize        18
                          :with            "Bacon every day"
                          :without         "Normal"
                          }
                :hrt5    {:icon            "ios-woman"
                          :subjects        ["woman" "women in their 50s"]
                          :risk            "their lifetime risk"
                          :exposure        "taking HRT for 5 years"
                          :baseline-risk   0.1
                          :relative-risk   1.05
                          :outcome         "breast cancer"
                          :evidence-source "https://wintoncentre.maths.cam.ac.uk/"
                          :fontSize        18
                          :with            "Taking HRT"
                          :without         "No HRT"
                          }
                :wine    {:icon            "ios-wine"
                          :subjects        ["woman" "women"]
                          :risk            "their lifetime risk"
                          :exposure        "drinking half a bottle of wine a day"
                          :baseline-risk   0.12
                          :relative-risk   1.30
                          :outcome         "breast cancer"
                          :evidence-source "https://wintoncentre.maths.cam.ac.uk/"
                          :fontSize        18
                          :with            "Half a bottle a day"
                          :without         "Not drinking"
                          }
                :wdoc    {:icon            "ios-person"
                          :subjects        ["person" "US people over 65 admitted to hospital under Medicare"]
                          :risk            "their risk"
                          :exposure        "seen by a female doctor"
                          :baseline-risk   0.115
                          :relative-risk   0.96
                          :outcome         "death within 30 days of admission"
                          :evidence-source "https://wintoncentre.maths.cam.ac.uk/"
                          :fontSize        16
                          :with            "Female doctor"
                          :without         "Male doctor"
                          }
                :statins {:icon            "ios-contact"
                          :subjects        ["person" "people"]
                          :risk            "their risk"
                          :exposure        "taking statins"
                          :baseline-risk   0.1
                          :relative-risk   0.9
                          :outcome         "heart attack or stroke in 10 years"
                          :evidence-source "https://wintoncentre.maths.cam.ac.uk/"
                          :fontSize        18
                          :with            "Taking statins"
                          :without         "No statins"
                          }})

(defn initial-stories []
  (mapv second scenarios))

(defn story [scenario]
  (str (second (:subjects scenario)) " " (:exposure scenario)))

(defn story-icon [scenario]
  (:icon scenario))

(def app-state (atom {:palette-index 0
                      :brand-title   "Winton Centre"
                      :app-banner    "Relative Risks Unspun"
                      :scenario      :hrt5
                      :stories       (initial-stories)
                      :story-index   0
                      :notifications true
                      :screen        :home
                      }))

(def palette-index (rum/cursor-in app-state [:palette-index]))
(def brand-title (rum/cursor-in app-state [:brand-title]))
(def app-banner (rum/cursor-in app-state [:app-banner]))
(def scenario (rum/cursor-in app-state [:scenario]))
(def story-index (rum/cursor-in app-state [:story-index]))
(def stories (rum/cursor-in app-state [:stories]))
(def notifications (rum/cursor-in app-state [:notifications]))


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

(defn caps-tidy [s]
  (-> s
      (capitalize)
      (replace #"Us " "US ")
      (replace #" us " " US ")
      (replace #"hrt" "HRT")))

(def format (partial cl-format nil))

(defn increase? [a b] (if (> a b) "decrease" "increase"))

(defn extra [rr] (if (> rr 1) "extra" "fewer"))

(defn singular-form [[singular _]] singular)

(defn n-plural-form [[singular plural]]
  (str "~[" singular "~;" plural "~:;" plural "~]"))

(defn compare1 [subjects]
  "~@(~a~) ~a ~a ~a of ~b from ~d% to ~d%")
(defn nn1 [subjects]
  (str "On average, ~d more ~:*" (n-plural-form subjects) " ~a would mean one ~a " (singular-form subjects) " experiences ~a."))
(defn nn2 [subjects]
  (str "On average, for one ~a " (singular-form subjects) " to experience ~a, ~d more ~:*" (n-plural-form subjects) " would need to be ~a."))

(defn text-generator [presentation {:keys [subjects risk exposure baseline-risk relative-risk outcome]}]
  (let [brpc (to-pc baseline-risk)
        erpc (to-pc (* baseline-risk relative-risk))]
    (caps-tidy (cond
                 (= presentation compare1)
                 (format (compare1 subjects) (second subjects) exposure (increase? brpc erpc) risk outcome brpc erpc)

                 (= presentation nn1)
                 (format (nn1 subjects) (number-needed relative-risk baseline-risk) exposure (extra relative-risk) outcome)

                 (= presentation nn2)
                 (format (nn2 subjects) (extra relative-risk) outcome (number-needed relative-risk baseline-risk) exposure)

                 :else
                 nil))))

;; todo: Cache these, or just generate as needed?

(comment
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

  (text-generator nn1 (:statins scenarios))
  (text-generator nn2 (:statins scenarios))
  (text-generator compare1 (:statins scenarios)))


