(ns unspun.screens.mixins
  (:require [shared.ui :refer [Dimensions]]))

(defn log-phase
  [panel phase])

(defn monitor
  [panel]
  (let [log-phase (fn [phase state]
                    (.log js/console panel phase)
                    state)]

    {:will-mount   (partial log-phase "will-mount")
     :did-mount    (partial log-phase "did-mount")
     :will-update  (partial log-phase "will-update")
     :did-update   (partial log-phase "did-update")
     :will-unmount (partial log-phase "will-unmount")
     }))

(comment
  (defn resize-handler
    [event]
    (println "dimensions = " (js->clj event)))
  ;=> nil
  ; {window {:width w :scale s :height h}}
  )

()
(defn resize-mixin
  [handler]
  {:did-mount    (fn [state]
                   (.addEventListener Dimensions "change" handler)
                   state)
   :will-unmount (fn [state]
                   (.removeEventListener Dimensions "change" handler)
                   state)})