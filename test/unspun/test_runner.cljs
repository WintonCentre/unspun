(ns unspun.test-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [shared.language-test]))

(defn run-tests []
  (doo-tests 'shared.language-test))