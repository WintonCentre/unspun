(ns shared.client
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [unspun.db :refer [winton-csv app-state flash-error-time]]
            [clojure.data.csv :refer [read-csv]]
            [clojure.core.]
            [clojure.core.async :refer [timeout <!]]
            [clojure.string :refer [starts-with? triml split]]
            [shared.http-status-codes :refer [status-message]]
            ))

(def csv (atom nil))

(defn first-word [s]
  (first (split (triml s) #"\s+")))

(defn colon-str-to-id [cstr]
  (let [tstr (first-word cstr)]
    (if (and (< (count tstr) 100)
             (starts-with? tstr ":"))
      (keyword (.substr tstr 1))
      nil)))

(defn column-ids [rcsv]
  (reduce conj {}
          (filter (comp not nil? first)
                  (map-indexed (fn [idx itm] [(colon-str-to-id itm) idx])
                               (first (for [v rcsv :when (= ":scenarios" (first v))] v))))))

(defn column-index
  "find the column index for a given column-id"
  ([csv-row id]
   (let [[_ keys [& cols]] csv-row]
     (.indexOf keys (str key)))))

(defn store-csv [data]
  (reset! csv (read-csv data))
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
  (first-word ":aaa bbb")
  ; => ":aaa"

  (slurp-csv "foo.txt" store-csv flash-error)

  (slurp-csv "https://wintoncentre.maths.cam.ac.uk/files/unspun-data/no-file.txt" store-csv flash-error)

  (slurp-csv "https://wintoncentre.maths.cam.ac.uk/files/unspun-data/plain.txt" store-csv flash-error)

  (column-ids @csv)
  ;=>
  ;{:baseline-risk 11,
  ; :tags 1,
  ; :exposed 14,
  ; :source-uri 13,
  ; :scenarios 0,
  ; :nn 15,
  ; :outcome 9,
  ; :icon 4,
  ; :without 7,
  ; :with 6,
  ; :causative 12,
  ; :subjects 3,
  ; :exposure 5,
  ; :relative-risk 10,
  ; :outcome-verb 8,
  ; :subject 2}

  (-> @csv
      (read-csv)
      (field-to-column :scenarios))
  ; => 0

  (field-to-column (read-csv @csv) :scenarios)
  ; => 0

  (field-to-column (read-csv @csv) :tags)
  ; => 1

  (status-message 404)
  ; => "Not Found
  )
