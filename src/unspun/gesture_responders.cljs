(ns unspun.gesture-responders
  (:require [shared.ui :refer [pan-responder]]
            [unspun.db :refer [clamp]]))

(defn g-prop [g-state name]
  (str name "=" (aget g-state name))
  )

(defn pan-logger [name]
  (fn [evt gesture-state]
    (if (= name "onTerminationRequest") false true)))

(defn onTerminationRequest []
  "Deny termination requests to avoid clash with sideways swipe"
  (fn [_ _] false))

(defn on-release [state]
  (fn [_ _]
    (reset! (:zooming state) false)
    ))

(defn grant-handler [state]
  (fn [_ _]
    (let [scale0 @(:scale state)]
      (reset! (:scale0 state) scale0)
      (reset! (:rescaler state) scale0)
      (reset! (:zooming state) true))
    true))

(def threshold 0.03)
(defn different [a b] (> (Math.abs (- a b)) threshold))

(defn move-handler [state origin]
  (fn [evt gesture-state]
    (let [y0 (aget gesture-state "y0")
          y1 (aget gesture-state "moveY")]
      ;(prn "y0 = " y0 " y1 = " y1 " factor = " (clamp [1 10] (inc (/ (- y0 y1) (- y0 700)))) " scale = " @(:scale state))
      (when-not @(:rescaler state) (reset! (:rescaler state) y0))
      (let [rescale (clamp [1 10] (* @(:scale0 state) (inc (/ (- y0 y1) (- origin y0)))))]
        (if (different rescale @(:scale state))
          (reset! (:scale state) rescale))))))

;;;
;; The pan responder mixin is a Rum mixin based on https://facebook.github.io/react-native/docs/panresponder.html.
;; The responder stat is stored in local component Rum state at `key`.
;;;
(defn pan-responder-mixin [key origin]
  {:will-mount (fn [state]
                 (let [pan-handlers
                       {:onStartShouldSetPanResponder        (pan-logger "onStartShouldSet")
                        :onStartShouldSetPanResponderCapture (pan-logger "onStartShouldSetCapture")
                        :onMoveShouldSetPanResponder         (pan-logger "onMoveShouldSet")
                        :onMoveShouldSetPanResponderCapture  (pan-logger "onStartShouldSetCapture")
                        :onPanResponderGrant                 (grant-handler state)
                        :onPanResponderMove                  (move-handler state origin)
                        :onPanResponderTerminationRequest    (onTerminationRequest)
                        :onPanResponderRelease               (on-release state)
                        :onPanResponderTerminate             (pan-logger "onTerminate")
                        :onShouldBlockNativeResponder        (pan-logger "onShouldBlock")
                        }]
                   (assoc state key (.create pan-responder (clj->js pan-handlers)))))})
