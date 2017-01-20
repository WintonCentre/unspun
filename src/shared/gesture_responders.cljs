(ns shared.gesture-responders
  (:require [shared.ui :refer [pan-responder]]))

(defn g-prop [g-state name]
  (str name "=" (aget g-state name))
  )

(defn pan-logger [name]
  (fn [evt gestureState]
    #_(when gestureState
      (doseq [prop ["moveX" "moveY" "x0" "y0" "dx" "dy" "vx" "vy" "numberActiveTouches"]]
        (.log js/console (g-prop gestureState prop)))
      (.log js/console name)
      )
    true))


(defn move-y-handler [state]
  (fn
    [evt gestureState]
    (let [y0 (aget gestureState "y0")
          dy (aget gestureState "dy")]
      (swap! (:scale state) #(* % (/ (- y0 dy) y0))))))

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
                        :onPanResponderGrant                 (pan-logger "onGrant")
                        :onPanResponderMove                  (move-y-handler state)
                        :onPanResponderTerminationRequest    (pan-logger "onTerminationRequest")
                        :onPanResponderRelease               (pan-logger "onRelease")
                        :onPanResponderTerminate             (pan-logger "onTerminate")
                        :onShouldBlockNativeResponder        (pan-logger "onShouldBlock")
                        }]
                   (assoc state key (.create pan-responder (clj->js pan-handlers)))))})
