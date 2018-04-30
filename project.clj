(defproject shopping-list "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [cider/piggieback "0.3.1"]
                 [figwheel-sidecar "0.5.16-SNAPSHOT"]
                 [reagent "0.8.0"]]
  :plugins [[lein-figwheel "0.5.16-SNAPSHOT"]]
  :main ^:skip-aot shopping-list.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}
  :cljsbuild {
    :builds [{:id "dev"
             :source-paths ["src"]
             :figwheel true
             :compiler {:main "shopping-list.core"}}]
  })
