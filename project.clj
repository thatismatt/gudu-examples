(defproject gudu.examples "0.1.0-SNAPSHOT"
  :description "Example usages of the gudu routing library."
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [ring "1.2.0-beta1"]
                 [hiccup "1.0.2"]
                 [gudu "0.1.0-SNAPSHOT"]
                 [clj-time "0.4.5"]]
  :ring {:handler gudu-examples.blog/app}
  :plugins [[lein-ring "0.8.3"]])
