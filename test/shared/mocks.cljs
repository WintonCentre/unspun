(ns shared.mocks
  (:require [shared.client :refer [column-ids
                                   make-scenarios
                                   get-scenario-data
                                   merge-new-scenarios]]
            [unspun.db :refer [app-state
                               scenarios-as-vec
                               scenarios-as-map]]
            [clojure.data.csv :refer [read-csv]]))

;;;
;; See dev profile version in shared.client
;;;

;;;
;;
;; Generate this string by:
;; 1) Saving the spreadsheet as a csv file (e.g. in incoming/risk-app.csv)
;; 2) Reading the file into EDN using
;; ```
;; lein run
;; (def raw-csv (slurp "incoming/risk-app-data.csv"))
;; (prn raw-csv)
;; ```
;; Copy/paste resulting EDN into mock-raw-csv def below
;;;
(def mock-raw-csv
  "﻿,\"Tags (comma separated, for search)\",\"Case (singular, brief & generic)\",Cases (plural & specific),icon* (see icons sheet),Exposure,Brief exposure label,Non-exposure label,Event (verb infinitive),Event (NP or AP for intransitive verb),Relative risk,Baseline risk,Causative?,Exposed risk,Number needed to be exposed,Source of relative risk ,\"List sources using a list of links written in markdown (see quick reference here).\rUse alt-enter to start a new line in the same cell.\",Comments\r:scenario,:tags,:subject,:subjects,:icon,:exposure,:with-label,:without-label,:outcome-verb,:outcome,:relative-risk,:baseline-risk,:causative,:exposed-risk,:nne,:sources-relative-risk,:sources-baseline-risk,:comments\r:bacon,\"food, bowel, cancer\",person,people,ios-man,eating a bacon sandwich every day,bacon every day,normal,develop,bowel cancer,1.18,6.0%,FALSE,7.1%,93,[Risk per 50g of daily processed meat. IARC Monograph](http://www.thelancet.com/journals/lanonc/article/PIIS1470-2045(15)00444-1/abstract),\"[Lifetime risk 7.3% for men, 5.5% for women](http://www.cancerresearchuk.org/health-professional/cancer-statistics/risk/lifetime-risk#heading-One)\",\r:hrt,\"preventative, breast, cancer, osteoporosis\",woman,women in their 50s,ios-woman,taking HRT for 5 years,taking HRT,No HRT,develop,breast cancer,1.05,10.0%,FALSE,10.5%,200,,,\r:wine,\"alcohol, breast, cancer\",woman,women,ios-wine,drinking half a bottle of wine a day,half a bottle a day,not drinking,develop,breast cancer,1.30,12.0%,FALSE,15.6%,28,,,\r:wdoc,\"gender, care\",person,US people over 65 admitted to hospital under Medicare,ios-person,being seen by a female doctor,female doctor,male doctor,die,within 30 days of admission,0.96,11.5%,FALSE,11.0%,217,,,\r:statins,\"preventative, medication\",person,people just within NICE guidelines for prescribing statins,ios-person,taking statins each day,taking statins,no statins,have,a heart attack or stroke within 10 years,0.70,10.0%,TRUE,7.0%,33,,,\rnew ones?????,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,\r,,,,,,,,,,,,,,,,,")


;; parse the raw csv. Result is a list of rows. Each row is a vector of cells. Each cell is a string, which may be empty.
;; Row 1: contains the first row of titles - to be ignored
(def mock-csv-data (read-csv mock-raw-csv))

(defn mockstore-csv [creator mock-data]
  (let [csv-data (make-scenarios ((juxt get-scenario-data column-ids) mock-data))]

    (swap! app-state
           update :stories
           #(merge-new-scenarios creator (scenarios-as-map %1) %2)
           csv-data)
    )
  )

