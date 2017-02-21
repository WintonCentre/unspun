(ns unspun.db
  (:require [rum.core :as rum]
            [clojure.pprint :refer [cl-format]]
            [clojure.string :refer [capitalize replace]]
            ))

(def version "v0.0.9")

(def max-nn 1000)                                           ; maximum number needed to treat

;;;
;; APP-STATE
;;;
(def scenarios {:bacon   {:icon            "ios-man"
                          :subjects        ["person" "people"]
                          :exposure        "eating a bacon sandwich every day"
                          :baseline-risk   0.06
                          :relative-risk   1.18
                          :outcome         ["developing bowel cancer" "develop bowel cancer"] ; " during their lifetime"
                          :evidence-source "https://wintoncentre.maths.cam.ac.uk/"
                          :with            "Bacon every day"
                          :without         "Normal"
                          :causative       false
                          }
                :hrt5    {:icon            "ios-woman"
                          :subjects        ["woman" "women in their 50s"]
                          :exposure        "taking HRT for 5 years"
                          :baseline-risk   0.1
                          :relative-risk   1.05
                          :outcome         ["developing breast cancer" "develop breast cancer"]  ; " during their lifetime"
                          :evidence-source "https://wintoncentre.maths.cam.ac.uk/"
                          :with            "Taking HRT"
                          :without         "No HRT"
                          :causative       false
                          }
                :wine    {:icon            "ios-wine"
                          :subjects        ["woman" "women"]
                          :exposure        "drinking half a bottle of wine a day"
                          :baseline-risk   0.12
                          :relative-risk   1.30
                          :evidence-source "https://wintoncentre.maths.cam.ac.uk/"
                          :outcome         ["developing breast cancer" "develop breast cancer"]
                          :with            "Half a bottle a day"
                          :without         "Not drinking"
                          :causative       false
                          }
                :wdoc    {:icon            "ios-person"
                          :subjects        ["person" "US people over 65 admitted to hospital under Medicare"]
                          :exposure        "being seen by a female doctor"
                          :baseline-risk   0.115
                          :relative-risk   0.96
                          :evidence-source "https://wintoncentre.maths.cam.ac.uk/"
                          :outcome         ["dying within 30 days of admission" "die within 30 days of admission"]
                          :with            "Female doctor"
                          :without         "Male doctor"
                          :causative       false
                          }
                :statins {:icon            "ios-contact"
                          :subjects        ["person" "people just within NICE guidelines for prescribing statins"]
                          :exposure        "taking statins each day"
                          :baseline-risk   0.1
                          :relative-risk   0.7
                          :outcome         ["having a heart attack or stroke within 10 years" "have a heart attack or stroke within 10 years"]
                          :evidence-source "https://wintoncentre.maths.cam.ac.uk/"
                          :with            "Taking statins"
                          :without         "No statins"
                          :causative       true
                          }})


(defn initial-stories []
  (mapv second scenarios))

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


(defn story [index]
  (let [scenario (@stories index)]
    (str "How much does " (:exposure scenario) " "
         (if (> (:relative-risk scenario) 1) "increase" "decrease")
         " the risk of "
         (first (:outcome scenario))
         "?")))

;;;
;; DERIVED STATE
;;;
(defn number-needed* [rr br]
  (let [effect (- (* rr br) br)]
    (if (zero? effect)
      max-nn
      (let [nn-val (Math.abs (/ 1 effect))]
        (cond
          (> nn-val max-nn) max-nn
          (< nn-val 0) 0
          :else nn-val)))))

(defn number-needed [rr br]
  (Math.round (number-needed* rr br)))

(defn anyway [rr br]
  (Math.round (* br (number-needed* rr br))))

(defn clamp
  "clamp v to the range [a,b]"
  [[a b] v]
  (if (<= a b)
    (Math.max a (Math.min b v))
    a))

(defn to-pc
  "convert x to a percentage"
  [x]
  (Math.round (* 100 x)))

(def caps-tidy capitalize)
#_(defn caps-tidy [s]
    (-> s
        (capitalize)
        (replace #"Us " "US ")
        (replace #" us " " US ")
        (replace #"hrt" "HRT")
        (replace #"nice" "NICE")))

(def format (partial cl-format nil))

(defn increased? [a b] (if (> a b) "decreased" "increased"))

(defn extra [rr] (if (> rr 1) "extra" "fewer"))

(defn singular-form [[singular _]] singular)

(defn n-plural-form [[singular plural]]
  (str "~[" singular "~;" plural "~:;" plural "~]"))

(def compare1
  "The risk of ~a for ~a is ~d%. ")
(def compare2 "The risk for those ~a is ~a to ~d%.")

(defn compare-text-vector
  "Generate a vector of texts for compare screens. We return a vector rather than a single string to make
  it easy to apply different formatting to each element."
  ([{:keys [subjects risk exposure baseline-risk relative-risk outcome causative]}]
   (let [brpc (to-pc baseline-risk)
         erpc (to-pc (* baseline-risk relative-risk))]
     [(str "The risk of " (first outcome) " for " (second subjects) " is ")
      (str brpc "%. ")
      (str "The risk for those " exposure " is ")
      (str (increased? brpc erpc))
      " to "
      (str erpc "%.")])))

(defn nn-text-vector
  "Generate a vector of texts for number needed screens. We return a vector rather than a single string to make
  it easy to apply different formatting to each element."
  ([{:keys [subjects risk exposure baseline-risk relative-risk outcome causative]}]
   (let [brpc (to-pc baseline-risk)
         erpc (to-pc (* baseline-risk relative-risk))
         nn (number-needed relative-risk baseline-risk)
         anyway-count ((if (> relative-risk 1) identity dec)
                        (anyway relative-risk baseline-risk))
         outcome-text (if true                                  ;(> relative-risk 1)
                        (str "would " (second outcome) " anyway. ")
                        (str "would otherwise " (second outcome) ". ")
                        )]
     (if causative
       ["On average, for "                              ; head
        (str "one " (extra relative-risk) " " (singular-form subjects) " ") ; mark-one
        (format "to ~a, " (second outcome))                 ; one-to-group
        (format "~d more " nn)                              ; group
        (format (str (n-plural-form subjects) " would need to be ~a. Of these, ") nn exposure) ; group-to-anyway
        (format "~d " anyway-count) ;anyway
        outcome-text
        ]
       ["On average, for "                                  ; head
        (str "one " (extra relative-risk) " " (singular-form subjects) " ") ; mark-one
        (format "to ~a, we would need " (second outcome))    ; one-to-group
        (format "a group of ~d more " nn)                   ; group
        (format (str (n-plural-form subjects) " ~a. Of these, ") nn exposure) ; group-to-anyway
        (format "~d " anyway-count) ;anyway
        outcome-text
        ]))))


;; todo: Cache these, or just generate as needed?

(comment

  (nn-text-vector (:hrt5 scenarios))
  (compare-text-vector (:hrt5 scenarios))


  (nn-text-vector (:bacon scenarios))
  (compare-text-vector (:bacon scenarios))


  (nn-text-vector (:wine scenarios))
  (compare-text-vector (:wine scenarios))


  (nn-text-vector (:wdoc scenarios))
  (compare-text-vector (:wdoc scenarios))


  (nn-text-vector (:statins scenarios))
  (compare-text-vector (:statins scenarios))
  )


