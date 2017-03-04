(ns shared.async-storage
  (:require [glittershark.core-async-storage :refer [get-item set-item]]
            [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(comment
  (go
    (<! (set-item :foo {:bar "baz"}))                       ;; => [nil], or [error]
    (println (<! (get-item :foo)))))                        ;; => [nil {:bar "baz"}], or [error nil]