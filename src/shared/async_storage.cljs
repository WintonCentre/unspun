(ns shared.async-storage
  (:require [glittershark.core-async-storage :refer [get-item set-item remove-item]]
            [cljs.core.async :refer [<!]]
            [unspun.db :refer [app-state use-cache stories refreshing flash-error]])
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

(defn clear-scenarios []
  (remove-item ::stories)
  )

;;;
;; Set up some watches so we catch any changes to stories
;;;
(defn watch-stories! []
  (println "watching stories")
  (add-watch stories :unspun.db/stories
             (fn [key stories old new]
               (println "saving stories locally"))))

(defn remove-watch-and-clear-stories! []
  (println "removing stories watch")
  (remove-watch stories :unspun.db/stories)
  (clear-scenarios))

(defn remove-use-cache-watch! []
  (remove-watch use-cache :unspun.db/use-cache))

(defn setup-watches!
  "Should be an idempotent call for figwheel reload purposes"
  []
  (println "setting up use-cache watch")
  (remove-watch use-cache :unspun.db/use-cache)
  (add-watch use-cache :unspun.db/use-cache
             (fn [key use-cache old new]
               (if new
                 (watch-stories!)
                 (remove-watch-and-clear-stories!)))))


(comment
  ;; async-storage tests
  (setup-watches!)
  (remove-use-cache-watch!)

  (add-watch use-cache :unspun.db/use-cache
             (fn [key use-cache old new]
               (println key old "->" new)))


  (defn refresh-change [key refreshing old new]
    (println key "changed from" old "to" new)
    )

  (add-watch refreshing :unspun.db/refreshing refresh-change)
  (remove-watch refreshing :unspun.db/refreshing)
  (reset! refreshing true)
  (reset! refreshing false)

  (go
    (<! (set-item ::foo {:bar "gooey"}))                       ;; => [nil], or [error]
    (println (<! (get-item ::foo))))                        ;; => [nil {:bar "baz"}], or [error nil]

  (go
    (println (<! (get-item ::foo))))

  (remove-item ::foo)

  )