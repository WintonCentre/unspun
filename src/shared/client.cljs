(ns shared.client
  (:require [unspun.db :refer [winton-csv]]))


(defn show-data [data]
  (prn "data")
  (.log js/console data))

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
                   (handler resp)))))

      (.catch (fn [error]
                (error-handler error)))))

(fetch-csv-file winton-csv show-data log-error)
