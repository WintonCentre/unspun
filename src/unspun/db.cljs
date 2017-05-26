(ns unspun.db
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [rum.core :as rum]
            [clojure.core.async :refer [timeout <!]]
            [clojure.pprint :refer [cl-format]]
            [clojure.string :refer [capitalize replace]]
            [shared.language :refer [present-participle]]
            ))

(def version "v0.0.11")

(def flash-error-time 5000)                                 ; duration (ms) of flash error messages

(def max-nn 1000)                                           ; maximum number needed to treat

(def max-id-length 100)                                     ; maximum length of a csv column id

(def app-src "https://github.com/wintoncentre/unspun")
(def winton-csv "https://wintoncentre.maths.cam.ac.uk/files/unspun-data/risk-app-data.csv")

;;;
;; APP-STATE
;;;

(def valid-field? #{:scenario
                    :tags
                    :subject
                    :subjects
                    :icon
                    :exposure
                    :with-label
                    :without-label
                    :outcome-verb
                    :outcome
                    :relative-risk
                    :baseline-risk
                    :causative
                    :exposed-risk
                    :nne
                    :sources-relative-risk
                    :sources-baseline-risk
                    :comments})

;;;
;; Built in data
;;;
(def scenarios {:bacon {:tags                  #{"food" "bowel" "cancer"}
                        :icon                  "ios-man"
                        :subject               "person"
                        :subjects              "people"
                        :exposure              "eating a bacon sandwich every day"
                        :baseline-risk         0.06
                        :relative-risk         1.18
                        :outcome-verb          "develop"
                        :outcome               "bowel cancer"
                        :with-label            "Bacon every day"
                        :without-label         "Normal"
                        :causative             false
                        :sources-relative-risk "https://wintoncentre.maths.cam.ac.uk/"
                        :sources-baseline-risk "https://wintoncentre.maths.cam.ac.uk/"
                        }})

(comment
  (def before
    :hrt5 {:tags            #{"preventitive" "breast" "cancer" "osteoporosis"}
           :icon            "ios-woman"
           :subjects        ["woman" "women in their 50s"]
           :exposure        "taking HRT for 5 years"
           :baseline-risk   0.1
           :relative-risk   1.05
           :outcome-verb    "develop"
           :outcome         "breast cancer"
           :evidence-source "https://wintoncentre.maths.cam.ac.uk/"
           :with-label      "Taking HRT"
           :without-label   "No HRT"
           :causative       false
           }
    :wine {:tags            #{"alcohol" "breast" "cancer"}
           :icon            "ios-wine"
           :subjects        ["woman" "women"]
           :exposure        "drinking half a bottle of wine a day"
           :baseline-risk   0.12
           :relative-risk   1.30
           :evidence-source "https://wintoncentre.maths.cam.ac.uk/"
           :outcome-verb    "develop"
           :outcome         "breast cancer"
           :with-label      "Half a bottle a day"
           :without-label   "Not drinking"
           :causative       false
           }
    :wdoc {:tags            #{"gender" "care"}
           :icon            "ios-person"
           :subjects        ["person" "US people over 65 admitted to hospital under Medicare"]
           :exposure        "being seen by a female doctor"
           :baseline-risk   0.115
           :relative-risk   0.96
           :evidence-source "https://wintoncentre.maths.cam.ac.uk/"
           :outcome-verb    "die"
           :outcome         "within 30 days of admission"
           :with-label      "Female doctor"
           :without-label   "Male doctor"
           :causative       false
           }
    :statins {:tags          #{"preventitive" "medication"}
              :icon          "ios-contact"
              :subjects      ["person" "people just within NICE guidelines for prescribing statins"]
              :exposure      "taking statins each day"
              :baseline-risk 0.1
              :relative-risk 0.7
              :outcome-verb  "have"
              :outcome       "a heart attack or stroke within 10 years"
              :sources       "https://wintoncentre.maths.cam.ac.uk/"
              :with-label    "Taking statins"
              :without-label "No statins"
              :causative     true
              }))


(defn scenarios-as-vec [m]
  (mapv (fn [[k v]] (merge v {:scenario k})) m))

(defn scenarios-as-map [v]
  (reduce conj {} (map (fn [s] [(:scenario s) s]) v)))

(defn with-additions [addition-map scenario-vec]
  (mapv #(merge addition-map %) scenario-vec))

(defn initial-stories [] (with-additions {:creator app-src} (scenarios-as-vec scenarios)))

(def app-state (atom {:palette-index 0
                      :brand-title   "Winton Centre"
                      :app-banner    "Risk Checker"
                      :scenario      :hrt5
                      :stories       (initial-stories)
                      :story-index   0
                      :notifications true
                      :use-cache     true
                      :screen        :home
                      :error         nil
                      :refreshing    false
                      :scenario-url  winton-csv
                      }))

(def refreshing (rum/cursor app-state :refreshing))
(def palette-index (rum/cursor-in app-state [:palette-index]))
(def brand-title (rum/cursor-in app-state [:brand-title]))
(def app-banner (rum/cursor-in app-state [:app-banner]))
(def scenario (rum/cursor-in app-state [:scenario]))
(def story-index (rum/cursor-in app-state [:story-index]))
(def stories (rum/cursor-in app-state [:stories]))
(def notifications (rum/cursor-in app-state [:notifications]))
(def use-cache (rum/cursor-in app-state [:use-cache]))
(def scenario-url (rum/cursor-in app-state [:scenario-url]))
(def error-status (rum/cursor-in app-state [:error]))


(defn story [index]
  (let [scenario (@stories index)]
    (str "How much does " (:exposure scenario) " "
         (if (> (:relative-risk scenario) 1) "increase" "decrease")
         " the risk of "
         (present-participle (:outcome-verb scenario))
         " "
         (:outcome scenario)
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

(defn n-plural-form [[singular plural]]
  (str "~[" singular "~;" plural "~:;" plural "~]"))

(def compare1
  "The risk of ~a for ~a is ~d%. ")
(def compare2 "The risk for those ~a is ~a to ~d%.")

(defn compare-text-vector
  "Generate a vector of texts for compare screens. We return a vector rather than a single string to make
  it easy to apply different formatting to each element."
  ([{:keys [subjects risk exposure baseline-risk relative-risk outcome-verb outcome causative]}]
   (let [brpc (to-pc baseline-risk)
         erpc (to-pc (* baseline-risk relative-risk))]
     [(str "The risk of " (present-participle outcome-verb) " " outcome " for " subjects " is ")
      (str brpc "%. ")
      (str "The risk for those " exposure " is ")
      (str (increased? brpc erpc))
      " to "
      (str erpc "%.")])))

(defn nn-text-vector
  "Generate a vector of texts for number needed screens. We return a vector rather than a single string to make
  it easy to apply different formatting to each element."
  ([{:keys [subject subjects risk exposure baseline-risk relative-risk outcome-verb outcome causative]}]
   (let [brpc (to-pc baseline-risk)
         erpc (to-pc (* baseline-risk relative-risk))
         nn (number-needed relative-risk baseline-risk)
         anyway-count ((if (> relative-risk 1) identity dec)
                        (anyway relative-risk baseline-risk))
         outcome-inf (str outcome-verb " " outcome)
         outcome-pp (str (present-participle outcome-verb) " " outcome)
         outcome-text (if (> relative-risk 1)
                        (str "would " outcome-inf " anyway. ")
                        (str "would still " outcome-inf ". ")
                        )]
     (if causative
       ["On average, for "                                  ; head
        (str "one " (extra relative-risk) " " subject " ")  ; mark-one
        (format "to ~a, " outcome-inf)                      ; one-to-group
        (format "~d more " nn)                              ; group
        (format (str (n-plural-form [subject subjects]) " would need to be ~a. Of these, ") nn exposure) ; group-to-anyway
        (format "~d " anyway-count)                         ;anyway
        outcome-text
        ]
       ["On average, for "                                  ; head
        (str "one " (extra relative-risk) " " subject " ")  ; mark-one
        (format "to ~a, we would need " outcome-inf)        ; one-to-group
        (format "a group of ~d more " nn)                   ; group
        (format (str (n-plural-form [subject subjects]) " ~a. Of these, ") nn exposure) ; group-to-anyway
        (format "~d " anyway-count)                         ;anyway
        outcome-text
        ]))))

(defn flash-error
  "flash an error state in the database"
  [error]
  (js/alert error)
  (swap! app-state assoc :error error)
  (go
    (<! (timeout flash-error-time))
    (reset! error-status nil)
    (reset! refreshing nil)
    ))

;; todo: copy these to test suite

(comment

  (nn-text-vector (:hrt5 scenarios))
  ;=>
  ;["On average, for "
  ; "one extra woman "
  ; "to develop breast cancer, we would need "
  ; "a group of 200 more "
  ; "women in their 50s taking HRT for 5 years. Of these, "
  ; "20 "
  ; "would develop breast cancer anyway. "]

  (compare-text-vector (:hrt5 scenarios))
  ;=>
  ;["The risk of developing breast cancer for women in their 50s is "
  ; "10%. "
  ; "The risk for those taking HRT for 5 years is "
  ; "increased"
  ; " to "
  ; "11%."]

  (nn-text-vector (:bacon scenarios))
  ;=>
  ;["On average, for "
  ; "one extra person "
  ; "to develop bowel cancer, we would need "
  ; "a group of 93 more "
  ; "people eating a bacon sandwich every day. Of these, "
  ; "6 "
  ; "would develop bowel cancer anyway. "]

  (compare-text-vector (:bacon scenarios))
  ;=>
  ;["The risk of developing bowel cancer for people is "
  ; "6%. "
  ; "The risk for those eating a bacon sandwich every day is "
  ; "increased"
  ; " to "
  ; "7%."]

  (nn-text-vector (:wine scenarios))
  ;=>
  ;["On average, for "
  ; "one extra woman "
  ; "to develop breast cancer, we would need "
  ; "a group of 28 more "
  ; "women drinking half a bottle of wine a day. Of these, "
  ; "3 "
  ; "would develop breast cancer anyway. "]

  (compare-text-vector (:wine scenarios))
  ;=>
  ;["The risk of developing breast cancer for women is "
  ; "12%. "
  ; "The risk for those drinking half a bottle of wine a day is "
  ; "increased"
  ; " to "
  ; "16%."]


  (nn-text-vector (:wdoc scenarios))
  ;=>
  ;["On average, for "
  ; "one fewer person "
  ; "to die within 30 days of admission, we would need "
  ; "a group of 217 more "
  ; "US people over 65 admitted to hospital under Medicare being seen by a female doctor. Of these, "
  ; "24 "
  ; "would still die within 30 days of admission. "]

  (compare-text-vector (:wdoc scenarios))
  ;=>
  ;["The risk of dying within 30 days of admission for US people over 65 admitted to hospital under Medicare is "
  ; "12%. "
  ; "The risk for those being seen by a female doctor is "
  ; "decreased"
  ; " to "
  ; "11%."]


  (nn-text-vector (:statins scenarios))
  ;=>
  ;["On average, for "
  ; "one fewer person "
  ; "to have a heart attack or stroke within 10 years, "
  ; "33 more "
  ; "people just within NICE guidelines for prescribing statins would need to be taking statins each day. Of these, "
  ; "2 "
  ; "would still have a heart attack or stroke within 10 years. "]

  (compare-text-vector (:statins scenarios))
  ;=>
  ;["The risk of having a heart attack or stroke within 10 years for people just within NICE guidelines for prescribing statins is "
  ; "10%. "
  ; "The risk for those taking statins each day is "
  ; "decreased"
  ; " to "
  ; "7%."]

  (scenarios-as-vec scenarios)

  (scenarios-as-map (scenarios-as-vec scenarios))

  (= (scenarios-as-vec (scenarios-as-map (scenarios-as-vec scenarios)))
     (scenarios-as-vec scenarios))
  ; => true

  (with-additions {:creator app-src
                   :sources "test source"} (scenarios-as-vec scenarios))

  (initial-stories)

  )




