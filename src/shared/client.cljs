(ns shared.client
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [unspun.db :refer [winton-csv app-state flash-error-time max-id-length valid-field?]]
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

(def csv (atom nil))

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
             (first (for [v rcsv :when (= ":scenarios" (first v))] v)))))

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
  (let [sval ((if max-length (partial truncate max-length) identity) (trim value))]
    (if (>= (count sval) min-length) sval nil)))

(defn make-valid-boolean [value]
  (if (#{"true" "t" "yes" "y"} (lower-case (trim value))) true false))

;;
;;
;;
(defn make-valid-markdown
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
  (let [f (field {:tags          make-valid-tags
                  :icon          make-valid-icon
                  :subject       #(make-valid-string % 1 64)
                  :subjects      #(make-valid-string % 1 128)
                  :exposure      #(make-valid-string % 1 228)
                  :baseline-risk #(in-range (make-valid-float %) 0 1)
                  :relative-risk #(in-range (make-valid-float %) 0)
                  :outcome-verb  #(make-valid-string % 1 64)
                  :outcome       #(make-valid-string % 1 128)
                  :with          #(make-valid-string % 1 32)
                  :without       #(make-valid-string % 1 32)
                  :causative     make-valid-boolean
                  :sources       make-valid-markdown
                  })]
    (f value)))

(defn make-scenario [sd col-ids]
  (reduce conj {}
          (for [i (range 1 (min (count (rest sd)) (count col-ids)))
                :let [field (col-ids i)]
                :when (valid-field? field)
                :let [value ((vec (rest sd)) (dec i))
                      valid-value (valid-value? field value)]
                :when (not (nil? valid-value))]
            [field valid-value])))

(defn make-scenarios [sdata col-ids]
  (reduce conj {} (for [sd sdata]
                    [(colon-str-to-id (first sd))
                     (make-scenario sd col-ids)])))
;; networking

(defn merge-new-stories [old-stories new-stories]
  )

(defn store-csv [data]
  ;(reset! csv (read-csv data))
  (let [csv-data (apply make-scenarios ((juxt get-scenario-data column-ids) (read-csv data)))]
    (swap! app-state update :stories merge-new-stories (mapv second csv-data))
    )
  ;(prn "data")
  )                                                         ;; add encoding parameter?

(defn flash-error [error]
  (prn (str "flash-error: " error))
  (swap! app-state assoc :error error)
  (go
    (<! (timeout flash-error-time))
    (swap! app-state assoc :error nil)
    (prn "cleared flash-error")
    ))

(defn slurp-csv [url success-handler error-handler]
  (-> (js/fetch url)

      (.then (fn [resp]
               (when-not (nil? resp)
                 ;(.log js/console resp)
                 (let [handler (if (.-ok resp)
                                 success-handler
                                 #(error-handler (str url "\n" (status-message (.-status resp)))))]
                   (-> (.text resp)
                       (.then handler))))))

      (.catch (fn [error]
                (error-handler "Network connection error")))))

(slurp-csv winton-csv store-csv flash-error)

(comment
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
  ; => nil

  (make-valid-boolean "T")
  ; => "t"

  (valid-field? :nn)
  ; => nil

  (valid-field? :relative-risk)
  ; => :relative-risk

  (first-word ":aaa bbb")
  ; => ":aaa"

  (slurp-csv "foo.txt" store-csv flash-error)

  (slurp-csv "https://wintoncentre.maths.cam.ac.uk/files/unspun-data/no-file.txt" store-csv flash-error)

  (slurp-csv "https://wintoncentre.maths.cam.ac.uk/files/unspun-data/plain.txt" store-csv flash-error)

  (column-ids @csv)
  ;=>
  ;[:scenarios
  ; :tags
  ; :subject
  ; :subjects
  ; :icon
  ; :exposure
  ; :with
  ; :without
  ; :outcome-verb
  ; :outcome
  ; :relative-risk
  ; :baseline-risk
  ; :causative
  ; nil
  ; nil
  ; :sources
  ; nil]

  (-> @csv
      (read-csv)
      (field-to-column :scenarios))
  ; => 0

  (colon-str-to-id ":scenarios")
  ; => :scenarios

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

  (get-scenario-data @csv)
  ; => ([":bacon" ...] [":hrt" ...] ...)

  (make-scenarios (get-scenario-data @csv) (column-ids @csv))

  (apply make-scenarios ((juxt get-scenario-data column-ids) @csv))
  )
