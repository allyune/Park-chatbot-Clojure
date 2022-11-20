(ns ica-chatbot.answers
  (:require [clojure.java.io :as io])
  (:require [clojure.data.json :as json])
  (:require [clojure.string :as str]))


  (def parks-json (io/resource "parks.json"))
  (def parks-info (json/read-json (slurp parks-json)))


  (defn get-park-info [park, request]
    (let [info-about-park (get parks-info park)]
    (get info-about-park request)))


(defn print-transportation [means map]
    (for [m map]
    (for [[k v] m] (println (format "From station %s take %s: %s" k means v)))))


(defn parse-trams [t]
  (let [trams-str (re-find (re-pattern "(?:(?<=, )[^0-9]*|^(?:(?!Metro|metro|METRO|bus|Bus|BUS).)*)trams*[.]* +[Nn]o.[0-9, ]+") t)
        trams-seq (re-seq (re-pattern "[^0-9]*trams*[.]* +[Nn]o.[0-9, ]+") trams-str)
        trams-map (map (fn [x]
                        {(first (str/split x #"\s*trams*[.]* +[Nn]o.\s*"))
                         (str/split (second (str/split x #"\s*trams*[.]* +[Nn]o.\s*")) #",\s*")}) trams-seq)]
        trams-map))


(defn parse-transportation [park]
  (let [t (get-park-info park :transportation)
        trams (parse-trams t)]
        (print-transportation "tram" trams)))


; (def transportations (for [[k v] parks-info] (:transportation v)))
;
;
; (for [t transportations] (println (re-seq (re-pattern "(?:(?<=, )[^0-9]*|^(?:(?!Metro|metro|METRO|bus|Bus|BUS).)*)trams*[.]* +[Nn]o.[0-9, ]+") t)))
