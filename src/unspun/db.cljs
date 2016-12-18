(ns unspun.db
  (:require [rum.core :as rum]))

(defonce app-state (atom {:palette-index 0
                          :brand-title   "Winton Centre"}))

(def palette-index (rum/cursor-in app-state [:palette-index]))
(def brand-title (rum/cursor-in app-state [:brand-title]))