(ns unspun.screens.mixins)

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