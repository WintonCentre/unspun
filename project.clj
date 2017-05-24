(defproject unspun "0.1.0-SNAPSHOT"
  :description "A relative risks interpreter and visualiser"
  :url "http://wintoncentre.maths.cam.ac.uk"
  :license {:name "MIT License"
            :url  "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [org.clojure/clojurescript "1.9.521"]
                 [org.clojure/core.async "0.3.442"]
                 [rum "0.10.8" :exclusions [cljsjs/react cljsjs/react-dom sablono]]
                 [cljs-exponent "0.1.6"]
                 [react-native-externs "0.0.2-SNAPSHOT"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [core-async-storage "0.2.0" :exclusions [org.clojure/core.async]]]
  :plugins [[lein-cljsbuild "1.1.6"]
            [lein-figwheel "0.5.10" :exclusions [org.clojure/core.async]]
            [lein-environ "1.1.0"]]
  :clean-targets ["target/" "main.js"]
  :aliases {"figwheel"        ["run" "-m" "user" "--figwheel"]
            "themes"          ["run" "-m" "themes"]
            "externs"         ["do" "clean"
                               ["run" "-m" "externs"]
                               ["run" "-m" "themes"]]
            "rebuild-modules" ["run" "-m" "user" "--rebuild-modules"]
            "prod-build"      ^{:doc "Recompile code with prod profile."}
                              ["externs"
                               ["with-profile" "prod" "cljsbuild" "once" "main"]]
            "travis"          ^{:doc "Run travis test locally"}
                              ["with-profile" "test" "doo" "phantom" "test" "once"]}

  :doo {:build "test"}

  :profiles {:dev  {:dependencies [[figwheel-sidecar "0.5.10" :exclusions [org.clojure/core.async]]
                                   [com.cemerick/piggieback "0.2.1"]]
                    :source-paths ["src" "notest" "env/dev"]
                    :cljsbuild    {:builds [{:id           "main"
                                             :source-paths ["src" "notest" "env/dev"]
                                             :figwheel     true
                                             :compiler     {:output-to     "target/not-used.js"
                                                            :main          "env.main"
                                                            :output-dir    "target"
                                                            :optimizations :none}}]}
                    :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}
             :prod {:cljsbuild {:builds [{:id           "main"
                                          :source-paths ["src" "notest" "env/prod"]
                                          :compiler     {:output-to          "main.js"
                                                         :main               "env.main"
                                                         :output-dir         "target"
                                                         :static-fns         true
                                                         :externs            ["js/externs.js"]
                                                         :parallel-build     false
                                                         :optimize-constants false
                                                         :optimizations      :advanced
                                                         :closure-defines    {"goog.DEBUG" false}}}]}}
             :test {:doo          {:build "test"}
                    :plugins      [[lein-doo "0.1.7"]]
                    :source-paths ["src" "test" "env/test"]
                    :cljsbuild    {:builds [{:id           "test"
                                             :source-paths ["src" "test" "env/test"]
                                             :compiler     {:output-to     "target/resources/test.js"
                                                            :main          "env.main"
                                                            :output-dir    "target"
                                                            :pretty-print  true
                                                            :source-map    false
                                                            :optimizations :none}}]}}})
