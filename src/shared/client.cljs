(ns shared.client
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [unspun.db :refer [winton-csv app-state scenarios-as-map scenarios-as-vec with-additions flash-error-time max-id-length valid-field?]]
            [shared.schema :refer [db-schema scenario-keys]]
            [clojure.data.csv :refer [read-csv]]
            [clojure.core.]
            [clojure.core.async :refer [timeout <!]]
            [clojure.string :refer [starts-with? ends-with? trim triml split lower-case]]
            [shared.http-status-codes :refer [status-message]]
            [graphics.icons :refer [icon-name?]]
            ))

;;;
;; Client-side support for reading scenarios from csv files
;;;

(defn first-word [s]
  (first (split (triml s) #"\s+")))

(defn colon-str-to-id [cstr]
  (let [tstr (first-word cstr)]
    (if (and (< (count tstr) (+ 2 max-id-length))
             (starts-with? tstr ":"))
      (keyword (.substr tstr 1))
      nil)))

(defn column-ids [rcsv]
  (into []
        (map colon-str-to-id
             (first (for [v rcsv
                          :when (= ":scenario-id" (first v))] v)))))

(defn get-scenario-data [rcsv]
  (filter (comp #(and (starts-with? % ":")
                      (not (starts-with? % ":scenario")))
                first-word
                first) rcsv))

(defn truncate [n s]
  (.substr s 0 n))

(def trunc128 (partial truncate 128))
(def trunc64 (partial truncate 64))
(def trunc32 (partial truncate 32))

(defn make-valid-tags [value]
  (into #{} (map (comp trunc128 lower-case trim) (first (read-csv value)))))

(defn make-valid-icon [value]
  (icon-name? (trunc64 (trim value))))

(defn make-valid-float [value]
  (let [sval (trunc32 (trim value))
        float (if (ends-with? sval "%")
                (/ (js/parseFloat sval) 100)
                (js/parseFloat sval))]
    (if (js/isNaN float) nil float)))

(defn make-valid-string [value min-length & [max-length]]
  (let [sval ((if max-length
                #(if (> (count %) max-length) nil %)
                identity) (trim value))]
    (if (>= (count sval) min-length) sval nil)))

(defn make-valid-boolean [value]
  (if (#{"true" "t" "yes" "y"} (lower-case (trim value))) true false))

;;
;;
;;
(defn make-valid-source
  "Links in this markdown should only be rendered when the CSV source is trusted or when the user has typed in the markdown herself."
  [value]
  (make-valid-string value 0 1024))

(defn in-range [val min-val & [max-val]]
  (if (and (>= val min-val)
           (or (nil? max-val)
               (<= val max-val)))
    val
    nil))

(defn valid-value? [field value]
  (let [f (field {:scenario              identity
                  :tags                  make-valid-tags
                  :icon                  make-valid-icon
                  :subject               #(make-valid-string % 1 64)
                  :subjects              #(make-valid-string % 1 128)
                  :exposure              #(make-valid-string % 1 228)
                  :baseline-risk         #(in-range (make-valid-float %) 0 1)
                  :relative-risk         #(in-range (make-valid-float %) 0)
                  :outcome-verb          #(make-valid-string % 1 64)
                  :outcome               #(make-valid-string % 1 128)
                  :with-label            #(make-valid-string % 1 32)
                  :without-label         #(make-valid-string % 1 32)
                  :causative             make-valid-boolean
                  :sources-baseline-risk make-valid-source
                  :sources-relative-risk make-valid-source
                  })]
    (f value)))

(defn make-scenario [[sc-key & sc-val] col-ids]
  (reduce conj {:scenario sc-key}
          (for [i (range 1 (min (count sc-val) (count col-ids)))
                :let [field (col-ids i)]
                :when (valid-field? field)
                :let [value ((vec sc-val) (dec i))
                      valid-value (valid-value? field value)]
                :when (not (nil? valid-value))]
            [field valid-value])))

(defn make-scenarios [[sdata col-ids]]
  (reduce conj {}
          (for [sd sdata]
            [(colon-str-to-id (first sd))
             (make-scenario sd col-ids)])))
;; networking

(defn merge-new-scenarios [creator old-scenarios new-scenarios]
  (with-additions creator (scenarios-as-vec (merge old-scenarios new-scenarios))))

(defn store-csv [creator data]
  ;(reset! csv (read-csv data))
  (let [csv-data (make-scenarios ((juxt get-scenario-data column-ids) (read-csv data)))]
    (swap! app-state
           update :stories
           #(merge-new-scenarios creator (scenarios-as-map %1) %2)
           csv-data)
    )
  )

(defn flash-error [error]
  (prn (str "flash-error: " error))
  (swap! app-state assoc :error error)
  (go
    (<! (timeout flash-error-time))
    (swap! app-state assoc :error nil)
    (prn "cleared flash-error")
    ))



(comment
  (def mock-csv-data '([""
                        "Tags (comma separated, for search)"
                        "Case (singular, brief & generic)"
                        "Cases (plural & specific)"
                        "icon* (see icons sheet)"
                        "Exposure"
                        "Brief exposure label"
                        "Non-exposure label"
                        "Event (verb infinitive)"
                        "Event (NP or AP for intransitive verb)"
                        "Relative risk"
                        "Baseline risk"
                        "Causative?"
                        "Exposed risk"
                        "Number needed to be exposed"
                        "List sources using a list of links written in markdown (see quick reference here).\rUse alt-enter to start a new line in the same cell."
                        "Comments"]
                        [":scenario-id"
                         ":tags"
                         ":subject"
                         ":subjects"
                         ":icon"
                         ":exposure"
                         ":with-label"
                         ":without-label"
                         ":outcome-verb"
                         ":outcome"
                         ":relative-risk"
                         ":baseline-risk"
                         ":causative"
                         ""
                         ""
                         ":sources"
                         ""]
                        [":bacon"
                         "food, bowel, cancer"
                         "person"
                         "people"
                         "ios-man"
                         "eating a bacon sandwich every day"
                         "bacon every day"
                         "normal"
                         "develop"
                         "bowel cancer"
                         "1.18"
                         "6.0%"
                         "FALSE"
                         "7.1%"
                         "93"
                         "* For relative risk, see: [Risk per 50g of daily processed meat. IARC Monograph](http://www.thelancet.com/journals/lanonc/article/PIIS1470-2045(15)00444-1/abstract)\r* For baseline risk, see: [Lifetime risk 7.3% for men, 5.5% for women](http://www.cancerresearchuk.org/health-professional/cancer-statistics/risk/lifetime-risk#heading-One)"
                         ""]
                        [":hrt"
                         "preventitive, breast, cancer, osteoporosis"
                         "woman"
                         "women in their 50s"
                         "ios-woman"
                         "taking HRT for 5 years"
                         "taking HRT"
                         "No HRT"
                         "develop"
                         "breast cancer"
                         "1.05"
                         "10.0%"
                         "FALSE"
                         "10.5%"
                         "200"
                         ""
                         ""]
                        [":wine"
                         "alcohol, breast, cancer"
                         "woman"
                         "women"
                         "ios-wine"
                         "drinking half a bottle of wine a day"
                         "half a bottle a day"
                         "not drinking"
                         "develop"
                         "breast cancer"
                         "1.30"
                         "12.0%"
                         "FALSE"
                         "15.6%"
                         "28"
                         ""
                         ""]
                        [":wdoc"
                         "gender,care"
                         "person"
                         "US people over 65 admitted to hospital under Medicare"
                         "ios-person"
                         "being seen by a female doctor"
                         "female doctor"
                         "male doctor"
                         "die"
                         "within 30 days of admission"
                         "0.96"
                         "11.5%"
                         "FALSE"
                         "11.0%"
                         "217"
                         ""
                         ""]
                        [":statins"
                         "preventitive, medication"
                         "person"
                         "people just within NICE guidelines for prescribing statins"
                         "ios-person"
                         "taking statins each day"
                         "taking statins"
                         "no statins"
                         "have"
                         "a heart attack or stroke within 10 years"
                         "0.70"
                         "10.0%"
                         "TRUE"
                         "7.0%"
                         "33"
                         ""
                         ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""]
                        ["," "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""])
    )

  (= (trunc128 (clojure.string/join "" (repeat 128 ".")))
     (trunc128 (clojure.string/join "" (repeat 129 "."))))
  ; => true

  (make-valid-tags "food, bowel, cancer")
  ; => #{"food" "bowel" "cancer"}

  (make-valid-icon "   ios-woman  ")
  ; => "ios-woman"

  (make-valid-float "  10.00% ")
  ; => 0.1

  (make-valid-float "  % ")
  ; => nil

  (make-valid-string "" 1 10)
  ; => nil

  (make-valid-string "." 1 10)
  ; => "."

  (make-valid-string "..........." 1 10)
  ; => ".........."

  (make-valid-string "12345678901" 1 10)
  ; => nil

  (make-valid-boolean "foo")
  ; => false

  (make-valid-boolean "T")
  ; => "t"

  (valid-field? :scenario-id)
  ; => nil

  (valid-field? :relative-risk)
  ; => :relative-risk

  (first-word ":aaa bbb")
  ; => ":aaa"

  (slurp-csv "foo.txt" store-csv flash-error)

  (slurp-csv "https://wintoncentre.maths.cam.ac.uk/files/unspun-data/no-file.txt" store-csv flash-error)

  (slurp-csv "https://wintoncentre.maths.cam.ac.uk/files/unspun-data/plain.txt" store-csv flash-error)

  (column-ids mock-csv-data)
  ;=>
  ;[:scenario-id
  ; :tags
  ; :subject
  ; :subjects
  ; :icon
  ; :exposure
  ; :with-label
  ; :without-label
  ; :outcome-verb
  ; :outcome
  ; :relative-risk
  ; :baseline-risk
  ; :causative
  ; nil
  ; nil
  ; :sources
  ; nil]

  (colon-str-to-id ":scenario-id")
  ; => :scenario-id

  (colon-str-to-id ":wom bat")
  ; => :wom

  (colon-str-to-id (str ":" (clojure.string/join (repeat 100 "a"))))
  ; => :aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa

  (colon-str-to-id (str ":" (clojure.string/join (repeat 101 "a"))))
  ; => nil

  (status-message 404)
  ; => "Not Found

  (status-message 500)
  ; => "Internal Server Error"

  (get-scenario-data mock-csv-data)
  ; => ([":bacon" ...] [":hrt" ...] ...)

  (make-scenarios [(get-scenario-data mock-csv-data) (column-ids mock-csv-data)])

  (make-scenarios ((juxt get-scenario-data column-ids) mock-csv-data))

  (defn mockstore-csv [creator mock-data]
    (let [csv-data (make-scenarios ((juxt get-scenario-data column-ids) mock-data))]

      (swap! app-state
             update :stories
             #(merge-new-scenarios creator (scenarios-as-map %1) %2)
             csv-data)
      )
    )

  (mockstore-csv {:creator winton-csv} mock-csv-data)
  )
