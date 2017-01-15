(ns shared.scientific)

(defn log10 [x] (/ (Math.log x) (Math.log 10)))

(defn exponent [x]
  (Math.floor (log10 x)))

(defn mantissa [x]
  (/ x (* (Math.pow 10 (exponent x)))))
