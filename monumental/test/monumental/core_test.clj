(ns monumental.core-test
  (:require [midje.sweet :refer :all]
            [monumental.core :refer :all]))

(facts "about `monuments-by-region`"
  (fact "it should return monuments for matching regions"
    (let [monuments '({:REG "Picardie"})
          region "Picardie"]
    (monuments-by-region monuments region) => monuments))
  (fact "it should return monuments matching part of region name"
    (let [monuments '({:REG "Picardie"})
          region "P"]
    (monuments-by-region monuments region) => monuments)))
