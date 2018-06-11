(ns graphics.scales
  (:require [cljs.pprint :refer [cl-format]]))

(def e10 (Math.sqrt 50))
(def e5 (Math.sqrt 10))
(def e2 (Math.sqrt 2))

(defn tick-step [start stop preferred-count]
  (let [step0 (/ (Math.abs (- stop start)) (max 0 preferred-count))
        step1 (Math.pow 10 (Math.floor (/ (Math.log step0) Math.LN10)))
        error (/ step0 step1)
        step (cond
               (>= error e10) (* 10 step1)
               (>= error e5) (* 5 step1)
               (>= error e2) (* 2 step1)
               :else step1)]
    (if (< stop start) (- step) step)))

(defn preferred-ticks [start stop preferred-count]
  (let [step (tick-step start stop preferred-count)]
    (range
      (* (Math.ceil (/ start step)) step)
      (+ (* (Math.floor (/ stop step)) step) (/ step 2))
      step)))

(defn numeric-format-specifier [scale]
  "Provide a default format specifier for numeric scales.
  Note that values larger than 1 must be rounded to integers for these
  formats to work sensibly."
  (let [abs-in (map Math.abs (:in scale))
        abs-step (Math.abs (apply tick-step (conj (:in scale) (:tick-count scale))))]
    (cond
      (< abs-step 0.00001)
      "~(~3,1e~)"
      (> (apply max abs-in) 99999)
      "~(~,1e~)"
      (>= abs-step 1)
      "~d"
      (>= abs-step 0.1)
      "~1$"
      (>= abs-step 0.01)
      "~$"
      (>= abs-step 0.001)
      "~3$"
      (>= abs-step 0.0001)
      "~0,4f"
      :else
      "~0,5f")))

(defn- scale-ticks [a-scale tick-count]
  (apply preferred-ticks (conj (:in a-scale) tick-count)))

(defn- linear [[x1 x2] [y1 y2]] (fn [x] (+ y1 (* (/ (- x x1) (- x2 x1)) (- y2 y1)))))

(defn- linear-nice [[start stop :as input] & [p-count]]
  "Return a nice domain given an input range and optionally a tick count"
  (let [n (if (nil? p-count) 10 p-count)
        step (tick-step start stop n)]
    (if (not (or (js/isNaN step) (nil? step)))

      (let [step (tick-step (* (Math.floor (/ start step)) step)
                            (* (Math.ceil (/ stop step)) step)
                            n)]
        [(* (Math.floor (/ start step)) step)
         (* (Math.ceil (/ stop step)) step)])

      input)))

(defn create-linear-scale [in out tick-count]
  {:in                    in
   :out                   out
   :i->o                  (fn [scale] (linear (:in scale) (:out scale)))
   :o->i                  (fn [scale] (linear (:out scale) (:in scale)))
   :ticks                 (fn [scale] (scale-ticks scale tick-count))
   :tick-format-specifier (fn [scale] (numeric-format-specifier scale))
   :tick-count            tick-count
   })

(defn i->o [scale] ((:i->o scale) scale))
(defn o->i [scale] ((:o->i scale) scale))
(defn ticks [scale] ((:ticks scale) scale))
(defn tick-format-specifier [scale] ((:tick-format-specifier scale) scale))

(defn internal-ticks [scale]
  (let [[low high] (:in scale)]
    (filter #(and (> % low) (< % high)) (ticks scale))))

(defn bounded-ticks [scale]
  "Maybe this should be the usual ticks of a bounded or clipped scale?"
  (let [[in0 in1] (:in scale)
        comparator (if (< in0 in1) < >)]
    (vec (into (apply sorted-set-by comparator (internal-ticks scale)) (:in scale)))))

(defn- format-ticks [specifier ticks]
  (map #(cl-format nil specifier %) ticks))

(defn formatted-internal-ticks [scale]
  (format-ticks (tick-format-specifier scale) (internal-ticks scale)))

(defn formatted-ticks [scale]
  (format-ticks (tick-format-specifier scale) (ticks scale)))