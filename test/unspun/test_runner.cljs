(ns unspun.test-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [shared.language-test]
            [clojure.data.csv-test]))

(defn run-tests []
  (doo-tests 'shared.language-test
             'clojure.data.csv-test))