(ns ica-chatbot.parser
  (:require [clojure.string :as str]))


(defn parse-trams [t]
    (let [trams-str (re-find (re-pattern "(?:(?<=, )[^0-9]*|^(?:(?!Metro|metro|METRO|bus|Bus|BUS).)*)trams*[.]* +[Nn]o.[0-9, ]+") t)
          trams-seq (re-seq (re-pattern "[^0-9]*trams*[.]* +[Nn]o.[0-9, ]+") trams-str)
          trams-map (map (fn [x]
                          {(first (str/split x #"\s*trams*[.]* +[Nn]o.\s*"))
                           (str/split (second (str/split x #"\s*trams*[.]* +[Nn]o.\s*")) #",\s*")}) trams-seq)]
          trams-map))


(defn parse-metro [t]
  (re-find (re-pattern "(?:.* - )*[Mm]etro .*?(?=$|, [A-Za-z]{2,})") t))


   (defn match-regex [regex input]
     (re-matches (re-pattern regex) input))()


   (defn get-intent [input]
     (cond
       (not (nil? (match-regex #".*(can|possible).*(bicycle|cycle|bike|ride a bike).*" input))) "biking"
       (not (nil? (match-regex #".*(is|there|where|can|possible).*(toilet|wc|bathroom).*" input))) "wc"
       :else :unknown))


   (defn parse-input []
     (let [input (read-line)
          intent (get-intent input)]
     ))

