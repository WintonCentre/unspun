(ns shared.utils)

(defn deep-merge [& maps]
  (apply merge-with deep-merge maps))


(comment

  (deep-merge {:a1 {:b1 {:c1 3} :b2 4} :a3 3} {:a1 {:b1 {:c2 2}}} {:a1 {:b1 {:c3 23}}})
  ; => {:a1 {:b1 {:c1 3, :c2 2, :c3 23}, :b2 4}, :a3 3}

  )