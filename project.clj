(defproject gudu.examples "0.2.0-SNAPSHOT"
  :description "Example usages of the gudu routing library."
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring "1.10.0"]
                 [hiccup "1.0.5"]
                 [gudu "0.2.0-SNAPSHOT"]
                 [clj-time "0.15.2"]]
  :ring {:handler gudu-examples.blog/app}
  :plugins [[lein-ring "0.12.6"]])
