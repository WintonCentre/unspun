(ns shared.non-phantom
  (:require [shared.http-status-codes :refer [status-message]]
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
                (.log js/console error)
                (error-handler "Network connection error")))))

(slurp-csv winton-csv #(store-csv {:creator winton-csv} %) flash-error)