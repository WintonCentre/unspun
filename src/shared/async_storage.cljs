(ns shared.async-storage
  (:require [glittershark.core-async-storage :refer [get-item set-item remove-item]]
            [cljs.core.async :refer [<!]]
            [unspun.db :refer [app-state use-cache stories refreshing flash-error]]
            [shared.ui :refer [legacy-async-storage]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def use-cache-key :unspun.db/use-cache)
(def app-cache-key :unspun.db/app-state)


(defn save-app-state! [new-state]
  ;(println "saved app-state")
  (go (let [[error] (<! (set-item app-cache-key new-state))]
        (when-not (nil? error) (flash-error error)))))

(defn save-use-cache! [new-state]
  ;(println "save-use-cache!")
  (go (let [[error] (<! (set-item use-cache-key new-state))]
        (when-not (nil? error) (flash-error error)))))


(defn add-app-state-watch []
  ;(println "add--app-state-watch")
  (add-watch app-state app-cache-key
             (fn [key app-state old-state new-state]
               ;(println "app-state  -> " new-state)
               (save-app-state! new-state)
               )))

(defn use-cache-changed [key use-cache old new]
  ;(println "use-cache-changed " key old "->" new)
  (if new
    (do
      ;; use-cache is on. Save app-state and arrange to resave it on change
      ;(println "cache on")
      (save-app-state! @app-state)
      (save-use-cache! true)
      (add-app-state-watch))
    (do
      ;; use-cache is off. Clear cache and remove app-state watch
      ;(println "clear app state")
      (remove-watch app-state app-cache-key)
      (save-use-cache! false)
      (go (<! (remove-item app-cache-key)))
      )
    )
  )

(defn add-use-cache-watch []
  (add-watch use-cache use-cache-key use-cache-changed))

(defn setup-watches!
  "Should be an idempotent call for figwheel reload purposes"
  []
  ;(println "setting up use-cache watch")
  (remove-watch use-cache use-cache-key)
  (add-watch use-cache use-cache-key use-cache-changed))

(defn reload-app-state! []
  (go
    ;; avoid interference from active watches by removing them.
    ;; necessary for a figwheel reload
    ;(println "reloading-app-state")
    (remove-watch use-cache use-cache-key)
    (remove-watch app-state app-cache-key)

    ;; read use-cache
    (let [[error using-cache?] (<! (get-item use-cache-key))]
      (if error
        (flash-error error)
        (do                                                 ;(println "cache is " using-cache?)
            (when using-cache?
              ; load app-state from cache
              (go (let [[cache-error cache] (<! (get-item app-cache-key))]
                    (if cache-error
                      (flash-error cache-error)
                      (do
                        ;; simple mindedly replace local app-state with cache
                        ;; this will also update use-cache
                        (reset! app-state cache)
                        (add-app-state-watch))))))))

      ;; we always want to watch use-cache switch
      (add-use-cache-watch))))

(comment
  ;; async-storage tests
  (setup-watches!)

  (reload-app-state!)

  (defn get-use-cache []
    (go (let [rv (<! (get-item use-cache-key))])))
  (get-use-cache)

  (defn get-app-cache []
    (go (let [rv (<! (get-item app-cache-key))]
          (println rv)
          )))
  (get-app-cache)

  (add-watch use-cache :unspun.db/use-cache
             (fn [key use-cache old new]))
  (remove-watch use-cache :unspun.db/use-cache)


  (add-watch app-state :unspun:db/app-state
             (fn [key use-cache old new]
               (println key old "->" new)))
  (remove-watch app-state :unspun:db/app-state)



  (defn refresh-change [key refreshing old new]
    ;(println key "changed from" old "to" new)
    )

  (add-watch refreshing :unspun.db/refreshing refresh-change)
  (remove-watch refreshing :unspun.db/refreshing)
  (reset! refreshing true)
  (reset! refreshing false)

  (go
    (<! (set-item ::foo {:bar "gooey"}))                    ;; => [nil], or [error]
    (println (<! (get-item ::foo)))
    )                                                       ;; => [nil {:bar "baz"}], or [error nil]

  (go
    (println (<! (get-item ::foo))))

  (remove-item ::foo)

  )


(comment
  ;; old stuff

  (defn load-scenarios []
    (go
      (let [[error local-stories] (<! (get-item ::stories))]
        (if error
          (flash-error error)
          ; :todo This may be too simple minded - we may need to merge.
          (println local-stories)
          ;(reset! stories local-stories)
          ))))

  (defn clear-scenarios []
    (remove-item ::stories))

  (defn save-scenarios []
    (go
      (let [error (<! (set-item ::stories @stories))]
        (when (not= error [nil])
          (flash-error (first error))))))

  ;;;
  ;; Set up some watches so we catch any changes to stories
  ;;;
  (defn watch-stories! []
    (println "watching stories")
    (add-watch stories :unspun.db/stories
               (fn [key stories old new]
                 (println "saving stories locally")
                 (save-scenarios))))

  (load-scenarios)

  (defn remove-watch-and-clear-stories! []
    (println "removing stories watch")
    (remove-watch stories :unspun.db/stories)
    (clear-scenarios)))