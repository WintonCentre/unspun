(ns unspun.test-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [shared.language-test]
            [shared.client-test]
            [clojure.data.csv-test]))

(defn run-tests []
  (doo-tests 'shared.client-test
             'shared.language-test
             'clojure.data.csv-test))