(ns shared.language-test
  (:require [cljs.test :refer-macros [deftest is testing async]]
            [shared.language :refer [present-participle classify]]))

(deftest classify-test
  (testing "generate classification maps properly"
    (is (= (classify "die") {:root "d", :last-two '("i" "e"), :vowels ["i" "e"], :verb-form :ie-ying})
        "die -> dying")
    (is (= (classify "be") {:root "", :last-two '("b" "e"), :vowels [nil "e"], :verb-form :add-ing})
        "be ->  being")
    (is (= (classify "have") {:root "ha", :last-two '("v" "e"), :vowels [nil "e"], :verb-form :cons-e-ing})
        "have ->  having")))