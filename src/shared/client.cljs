(ns shared.client
  (:require [unspun.db :refer [winton-csv]]
            [clojure.data.csv :refer [read-csv]]
            ))

(def csv (atom nil))




(defn field-to-column
"find the column index for a field of ids"
  ([csv-row key]
   (let [[_ keys [& cols]] csv-row]
     (.indexOf keys (str key)))))

(defn show-data [data]
  (reset! csv data)
  (prn "data")

  (.log js/console data)
  )                                                         ;; add encoding parameter?


(defn log-error [error]
  (prn "error")
  (.log js/console error))

(defn fetch-csv-file [url success-handler error-handler]
  (-> (js/fetch url)

      (.then (fn [resp]
               (when-not (nil? resp)
                 (.log js/console resp)
                 (let [handler (if (.-ok resp)
                                 success-handler
                                 error-handler)]
                   (-> (.text resp)
                       (.then handler))))))

      (.catch (fn [error]
                (error-handler error)))))

(fetch-csv-file winton-csv show-data log-error)

(comment
  (field-to-column @csv :tags)
  ; => 1

  )
