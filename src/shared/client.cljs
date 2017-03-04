(ns shared.client
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [unspun.db :refer [winton-csv app-state flash-error-time]]
            [clojure.data.csv :refer [read-csv]]
            [clojure.core.]
            [clojure.core.async :refer [timeout <!]]
            [shared.http-status-codes :refer [status-message]]
            ))

(def csv (atom nil))

(defn field-to-column
  "find the column index for a field of ids"
  ([csv-row key]
   (let [[_ keys [& cols]] csv-row]
     (.indexOf keys (str key)))))

(defn show-data [data]
  (reset! csv data)
  ;(prn "data")
  )                                                         ;; add encoding parameter?

(defn flash-error [error]
  (prn (str "flash-error: " error))
  (swap! app-state assoc :error error)
  (go
    (<! (timeout flash-error-time))
    (swap! app-state assoc :error nil)
    (prn "cleared flash-error")))

(defn fetch-csv-file [url success-handler error-handler]
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

(fetch-csv-file winton-csv show-data flash-error)

(comment
  (fetch-csv-file "foo.txt" show-data flash-error)

  (fetch-csv-file "https://wintoncentre.maths.cam.ac.uk/files/unspun-data/no-file.txt" show-data flash-error)

  (fetch-csv-file "https://wintoncentre.maths.cam.ac.uk/files/unspun-data/plain.txt" show-data flash-error)

  (-> @csv
      (read-csv)
      (field-to-column :scenario))
  ; => 0

  (field-to-column (read-csv @csv) :scenario)
  ; => 0

  (field-to-column (read-csv @csv) :tags)
  ; => 1

  (status-message 404)
  ; => "Not Found
  )
