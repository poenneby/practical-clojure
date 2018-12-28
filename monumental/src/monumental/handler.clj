(ns monumental.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            [cheshire.core :refer [parse-string]]
            [monumental.core :refer :all]))

(defonce monuments (parse-string (slurp (io/resource "firstHundred.json")) true))

(defroutes app-routes
  (GET "/api/search" [region] (response (monuments-by-region monuments region)))
  (route/not-found "Not Found"))

(def app (->
           app-routes
           (wrap-json-response)
           (wrap-defaults site-defaults)))
