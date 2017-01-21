(ns shared.gesture-responders
  (:require [shared.ui :refer [pan-responder]]
            [unspun.db :refer [clamp]]))

(defn g-prop [g-state name]
  (str name "=" (aget g-state name))
  )

(defn pan-logger [name]
  (fn [evt gesture-state]
    #_(when gesture-state
        (doseq [prop ["moveX" "moveY" "x0" "y0" "dx" "dy" "vx" "vy" "numberActiveTouches"]]
          (.log js/console (g-prop gesture-state prop)))
        (.log js/console name)
        )
    true))

(defn grant-handler [state]
  (fn [evt gesture-state]
    (reset! (:scale0 state) @(:scale state))))



(defn move-handler [state]
  (fn [evt gesture-state]
    (let [y0 (aget gesture-state "y0")
          y1 (aget gesture-state "moveY")
          ]
      ;(prn "y0 = " y0 " y1 = " y1 " factor = " (clamp [1 10] (inc (/ (- y0 y1) (- y0 700)))) " scale = " @(:scale state))
      (reset! (:scale state) (clamp [1 10] (* @(:scale0 state) (inc (/ (- y0 y1) (- 700 y0)))))))))

;;;
;; The pan responder mixin is a Rum mixin based on https://facebook.github.io/react-native/docs/panresponder.html.
;; The responder stat is stored in local component Rum state at `key`.
;;;
(defn pan-responder-mixin [key]
  {:will-mount (fn [state]
                 (let [pan-handlers
                       {:onStartShouldSetPanResponder        (pan-logger "onStartShouldSet")
                        :onStartShouldSetPanResponderCapture (pan-logger "onStartShouldSetCapture")
                        :onMoveShouldSetPanResponder         (pan-logger "onMoveShouldSet")
                        :onMoveShouldSetPanResponderCapture  (pan-logger "onStartShouldSetCapture")
                        :onPanResponderGrant                 (grant-handler state)
                        :onPanResponderMove                  (move-handler state)
                        :onPanResponderTerminationRequest    (pan-logger "onTerminationRequest")
                        :onPanResponderRelease               (pan-logger "onRelease")
                        :onPanResponderTerminate             (pan-logger "onTerminate")
                        :onShouldBlockNativeResponder        (pan-logger "onShouldBlock")
                        }]
                   (assoc state key (.create pan-responder (clj->js pan-handlers)))))})
