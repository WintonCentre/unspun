(ns shared.schema)

(def db-schema #{:id                                        ; 1
                 :scenario                                  ; :bacon
                 :owner-id                                  ; 3 -> {:user "winton"}
                 :group-vec                                 ; [1 2] e.g. 1 -> {:group "winton team"}, 2 -> {:group "winton board"}
                 :timestamp                                 ; #inst whenever
                 :permissions                               ; {:read {:owner :group :all} :write {:owner :group :all}}
                 :tags                                      ; #{"bacon" "colon cancer"}
                 :scenario-map                              ; the scenario data in an edn map
                 })

(def default-scenario {:scenario              :bacon
                         :icon                  "ios-man"
                         :subjects              ["person" "people"]
                         :exposure              "eating a bacon sandwich every day"
                         :baseline-risk         0.06
                         :relative-risk         1.18
                         :outcome-verb          "develop"
                         :outcome               "bowel cancer"
                         :with-label            "Bacon every day"
                         :without-label         "Normal"
                         :causative             false
                         :sources-baseline-risk [{:description "Not yet"
                                                  :link-text   "Winton"
                                                  :link-url    "https://winton.maths.cam.ac.uk"}]
                         :sources-relative-risk [{:description "Not yet"
                                                  :link-text   "Winton"
                                                  :link-url    "https://winton.maths.cam.ac.uk"}]
                         })

(def scenario-keys (into #{} (map (fn [[k v]] k) default-scenario)))