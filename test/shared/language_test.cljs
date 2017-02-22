(ns shared.language-test
  (:require [cljs.test :refer-macros [deftest is testing async]]
            [shared.language :refer [present-participle classify]]))

(deftest classify-test
  (testing "verb classifier works properly"
    (is (= (classify "die") {:root "d", :last-two '("i" "e"), :vowels ["i" "e"], :verb-form :ie-ying})
        "die -> dying")
    (is (= (classify "be") {:root "", :last-two '("b" "e"), :vowels [nil "e"], :verb-form :add-ing})
        "be ->  being")
    (is (= (classify "have") {:root "ha", :last-two '("v" "e"), :vowels [nil "e"], :verb-form :cons-e-ing})
        "have ->  having")))

(deftest present-participle-test
  (testing "generation of present participle from infinitive form of a verb"
    (is (= (present-participle "die") "dying") "die -> dying")
    (is (= (present-participle "be") "being") "be -> being")
    (is (= (present-participle "have") "having") "have -> having")
    ))