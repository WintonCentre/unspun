(ns shared.language)
;;;
;; Provide default present participles by declining the infinitive form of a verb
;;;

(defn vowel?
  [c]
  (#{"a" "e" "i" "o" "u"} c))

(defn add-ing
  [class]
  (str (:root class) (apply str (:last-two class)) "ing"))

(defn ie-ying
  [class]
  (str (:root class) "ying"))

(defn cons-e-ing
  [class]
  (str (:root class) (first (:last-two class)) "ing"))

(defn classify [infinitive]
  (let [word (vec infinitive)
        char-count (count word)
        last-two (take-last 2 word)
        last-two-count (count last-two)                     ; 2 or fewer for very short words
        vowels (mapv vowel? last-two)
        ]
    {:root      (apply str (take (- char-count last-two-count) word))
     :last-two  last-two
     :vowels    vowels
     :verb-form (cond
                  (<= char-count 2) :add-ing
                  (= ["i" "e"] vowels) :ie-ying
                  (= [nil "e"] vowels) :cons-e-ing
                  :else :add-ing)}
    ))

(defmulti gen-pp :verb-form)

(defmethod gen-pp :add-ing [word-data] (add-ing word-data))
(defmethod gen-pp :cons-e-ing [word-data] (cons-e-ing word-data))
(defmethod gen-pp :ie-ying [word-data] (ie-ying word-data))
(defmethod gen-pp :cons-undup-ing [word-data] (cons-e-ing word-data))

(def present-participle (comp gen-pp classify))

(comment
  ;;
  ;; tests
  ;;
  (classify "develop")
  ; => {:root "devel", :last-two ("o" "p"), :vowels ["o" nil], :verb-form :add-ing}

  (add-ing (classify "develop"))
  (present-participle "develop")
  ; => "developing"

  (classify "die")
  ; => {:root "d", :last-two ("i" "e"), :vowels ["i" "e"], :verb-form :ie-ying}

  (ie-ying (classify "die"))
  (gen-pp (classify "die"))
  (present-participle "die")
  ; => "dying"

  (classify "try")
  ; => {:root "t", :last-two ("r" "y"), :vowels [nil nil], :verb-form :add-ing}

  (add-ing (classify "try"))
  (gen-pp (classify "try"))
  (present-participle "try")
  ; => "trying"

  (classify "be")
  ; => {:root "", :last-two ("b" "e"), :vowels [nil "e"], :verb-form :add-ing}

  (add-ing (classify "be"))
  (gen-pp (classify "be"))
  (present-participle "be")
  ; => "being"

  (classify "have")
  ; => {:root "ha", :last-two ("v" "e"), :vowels [nil "e"], :verb-form :cons-e-ing}

  (cons-e-ing (classify "have"))
  (gen-pp (classify "have"))
  ; => "having"
  )

