(defproject monumental "0.0.1-SNAPSHOT"
  :description "Cool new project to do things and stuff"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [cheshire "5.8.1"]
                 [compojure "1.6.1"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.3.2"]
                 [ring-cors "0.1.12"]]
  :plugins [[lein-ring "0.12.4"]]
  :ring {:handler monumental.handler/app}
  :profiles {:dev {:dependencies [[midje "1.9.4"]
                                  [javax.servlet/servlet-api "2.5"]
                                  [ring/ring-mock "0.3.2"]]}})

