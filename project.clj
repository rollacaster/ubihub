(defproject ubihub "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [cider/piggieback "0.3.1"]
                 [figwheel-sidecar "0.5.16-SNAPSHOT"]
                 [reagent "0.8.0"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-devel "1.6.3"]
                 [ring/ring-defaults "0.3.1"]
                 [compojure "1.6.1"]
                 [fogus/ring-edn "0.3.0"]
                 [http-kit "2.2.0"]]
  :plugins [[lein-figwheel "0.5.16-SNAPSHOT"]
            [lein-cljsbuild "1.1.7"]
            [lein-heroku "0.5.3"]]
  :hooks [leiningen.cljsbuild]
  :main ubihub.core
  :uberjar-name "ubihub.jar"
  :profiles {:uberjar {:aot :all}}
  :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}
  :min-lein-version "2.7.1"
  :cljsbuild
  {:builds {:dev
            {:source-paths ["src"]
             :figwheel true
             :compiler {:main "ubihub.core"
                        :asset-path "out"
                        :output-to "resources/public/main.js"}}
            :prod
            {:source-paths ["src"]
             :compiler {:main "ubihub.core"
                        :asset-path "out"
                        :output-to "resources/public/bundle.js"
                        :optimizations :advanced}}}}
  :heroku {:process-types { "web" "java -jar target/ubihub.jar" }})
