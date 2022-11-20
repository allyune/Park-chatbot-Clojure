(ns ica-chatbot.answers
  (:require [clojure.java.io :as io])
  (:require [clojure.data.json :as json]))

  (def parks-json (io/resource "parks.json"))
  (def parks-info (json/read-json (slurp parks-json)))

  (defn get-park-info [park, request]
    (let [info-about-park (get parks-info park)]s
    (get info-about-park request)))


(defn parse-transportation [park]
  (let [t (get-park-info park :transportation)
        trams (re-find (re-pattern "(?:(?<=, )[^0-9]*|^(?:(?!Metro|metro|METRO|bus|Bus|BUS).)*)trams*[.]* +[Nn]o.[0-9, ]+") t)]
        (println trams)))


; (def transportations (for [[k v] parks-info] (:transportation v)))
;
;
; (for [t transportations] (println (re-seq (re-pattern "(?:(?<=, )[^0-9]*|^(?:(?!Metro|metro|METRO|bus|Bus|BUS).)*)trams*[.]* +[Nn]o.[0-9, ]+") t)))
