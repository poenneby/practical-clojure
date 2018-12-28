(ns monumental.core
  (:require [clojure.string :as str]))

(defn monuments-by-region [monuments region]
  (filter (fn [m] (str/starts-with? (:REG m) region)) monuments))

