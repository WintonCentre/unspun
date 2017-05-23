(ns shared.client-test
  (:require [cljs.test :refer-macros [deftest is testing async]]
            [unspun.db :refer [valid-field?
                               winton-csv]]
            [shared.client :refer [trunc128
                                   make-valid-icon
                                   make-valid-tags
                                   make-valid-float
                                   make-valid-string
                                   make-valid-boolean
                                   first-word
                                   column-ids
                                   colon-str-to-id
                                   get-scenario-data
                                   make-scenarios]]
            [shared.mocks :refer [mock-csv-data
                                  ; mock-scenario-data ; todo
                                  ; mock-scenarios ;todo
                                  mockstore-csv
                                  mock-app-state]]
            [shared.http-status-codes :refer [status-message]]
            ))

(deftest validation-utils
  (testing "validation utilities"
    (= (trunc128 (clojure.string/join "" (repeat 128 ".")))
       (trunc128 (clojure.string/join "" (repeat 129 "."))))
    ; => true

    (is (= (make-valid-tags "food, bowel, cancer")
           #{"food" "bowel" "cancer"}))

    (is (= (make-valid-icon "   ios-woman  "))
        "ios-woman")

    (is (= (make-valid-float "  10.00% ")
           0.1))

    (is (= (make-valid-float "  % ")
           nil))

    (is (= (make-valid-string "" 1 10)
           nil))

    (is (= (make-valid-string "." 1 10)
           "."))

    (is (= (make-valid-string ".........." 1 10)
           ".........."))

    (is (= (make-valid-string "12345678901" 1 10)
           nil))

    (is (= (make-valid-boolean "foo")
           false))

    (is (= (make-valid-boolean "T")
           true))

    (is (not (valid-field? :nn)))

    (is (valid-field? :relative-risk))

    (is (= (first-word ":aaa bbb")
           ":aaa"))

    ))

(deftest status-messages
  (testing "status message lookup"

    (is (= (status-message 404)
           "Not Found"))

    (is (= (status-message 500)
           "Internal Server Error"))))

(comment
  (column-ids mock-csv-data)
  )

(deftest csv-parsing
  (testing "csv parsing utilities"

    (is (= (column-ids mock-csv-data)
           [:scenario
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
            :comments]))

    (is (= (colon-str-to-id ":scenario-id")
           :scenario-id))

    (is (= (colon-str-to-id ":wom bat")
           :wom))

    (is (= (colon-str-to-id (str ":" (clojure.string/join (repeat 100 "a"))))
           :aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa))

    (is (= (colon-str-to-id (str ":" (clojure.string/join (repeat 101 "a"))))
           nil))

    (is (= (first (get-scenario-data mock-csv-data))
           (let [[_ _ bacon] mock-csv-data]
             bacon)
           ))


    (comment
      ;failing tests

      (is (= (first (make-scenarios ((juxt get-scenario-data column-ids) mock-csv-data)))
             [:bacon
              {:without-label "normal",
               :baseline-risk 0.06,
               :tags #{"food" "bowel" "cancer"},
               :outcome "bowel cancer",
               :icon "ios-man",
               :causative false,
               :with-label "bacon every day",
               :subjects "people",
               :exposure "eating a bacon sandwich every day",
               :relative-risk 1.18,
               :scenario ":bacon",
               :outcome-verb "develop",
               :subject "person"}]))



      (is (= (mockstore-csv {:creator winton-csv} mock-csv-data)
             mock-app-state)))
    ))

(comment
  mock-csv-data

  (mockstore-csv {:creator winton-csv} mock-csv-data)

  )