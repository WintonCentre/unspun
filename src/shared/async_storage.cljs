(ns shared.async-storage
  (:require [glittershark.core-async-storage :refer [get-item set-item remove-item]]
            [cljs.core.async :refer [<!]]
            [unspun.db :refer [app-state flash-error]])
  (:require-macros [cljs.core.async.macros :refer [go]]))


(defn save-scenarios [app-state]
  (go
    (let [error (<! (set-item ::stories (:stories @app-state)))]
      (when (not= error [nil])
        (flash-error (first error)))))
  )

(defn load-scenarios []
  (go
    (let [[error stories] (<! (get-item ::stories))]
      (if error
        (flash-error error)
        (swap! app-state update :stories stories))))
  )

(defn clear-stories []
  (remove-item ::stories)
  )


(comment
  ;; async-storage tests

  (go
    (<! (set-item ::foo {:bar "gooey"}))                       ;; => [nil], or [error]
    (println (<! (get-item ::foo))))                        ;; => [nil {:bar "baz"}], or [error nil]

  (go
    (println (<! (get-item ::foo))))

  (remove-item ::foo)

  )