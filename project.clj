(defproject archiet.trm/stac_core "0.1.1-SNAPSHOT"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [org.clojure/clojurescript "1.9.494"]
                 [prismatic/schema "1.1.5"]
                 [org.clojure/test.check "0.9.0"]]
  :profiles {:dev {:dependencies [[clj-http "3.5.0"]]}
             :test {:dependencies [[clj-http "3.5.0"]]}}
  :cljbuild
  {:builds {:dev {:source-paths ["src"]
                  :id "main"
                  :jar true
                  :compiler {:output-to "target/main.js"
                             :output-dir "target"
                             :optimizations :none
                             :pretty-print true}}
            :test {:source-paths ["src" "test"]
                   :incremental  true
                   :compiler     {:output-to     "target/main-test.js"
                                  :output-dir    "target-test"
                                  :optimizations :whitespace
                                  :pretty-print  true}}}})
