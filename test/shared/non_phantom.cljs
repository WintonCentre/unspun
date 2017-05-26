(ns shared.non-phantom
  (:require [shared.http-status-codes :refer [status-message]]
            [shared.client :refer [store-csv]]
            [unspun.db :refer [scenario-url flash-error ]]
            ))

;;
;; This is production code that cannot be tested in phantom because phantom does not support js/fetch
;;

(defn slurp-csv [url success-handler error-handler]
  (-> (js/fetch url)

      (.then (fn [resp]
               (when-not (nil? resp)
                 (let [handler (if (.-ok resp)
                                 success-handler
                                 #(error-handler (str url "\n" (status-message (.-status resp)))))]
                   (-> (.text resp)
                       (.then handler))))))

      (.catch (fn [error]
                (flash-error error)
                (error-handler "Network connection error")))))

(comment

  (slurp-csv @scenario-url #(println %) #(println "error: " %))

  ;; This is where we want to end up
  (slurp-csv @scenario-url #(store-csv {:creator @scenario-url} %) flash-error))