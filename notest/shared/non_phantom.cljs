(ns shared.non-phantom
  (:require [shared.http-status-codes :refer [status-message]]
            [shared.client :refer [flash-error store-csv]]
            [unspun.db :refer [winton-csv]]
            ))

;;
;; This is production code that cannot be tested in phantom because phantom does not support js/fetch
;;

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

(comment
  ;(def winton-csv "https://wintoncentre.maths.cam.ac.uk/files/unspun-data/risk-app-data.csv")
  (slurp-csv winton-csv #(println %) #(println "error: " %))

  ;; This is where we want to end up
  (slurp-csv winton-csv #(store-csv {:creator winton-csv} %) flash-error))