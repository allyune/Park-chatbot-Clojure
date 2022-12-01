(ns ica-chatbot.parser
  (:require [clojure.string :as str]))

(defn match-regex [regex input]
  (re-matches (re-pattern regex) input)) ()

(defn get-intent [input]
  (cond
    (not (nil? (match-regex #".*(can|possible|allowed).*(bicycle|cycle|bike|ride a bike).*" input))) :biking
    (not (nil? (match-regex #".*(is|there|where|can|possible)*.*(toilet|wc|bathroom).*" input))) :wc
    (not (nil? (match-regex #".*(get to|commute|transportation|transport).*" input))) :transportation
    (not (nil? (match-regex #".*(reviews|testimonials|feedback).*" input))) :reviews
    (not (nil? (match-regex #".*(:done|done|exit|bye|quit).*" input))) :exit
    :else :unknown))

(defn parse-trams [t]
  (let [trams-str (re-find (re-pattern "(?:(?<=, )[^0-9]*|^(?:(?!Metro|metro|METRO|bus|Bus|BUS).)*)trams*[.]* +[Nn]o.[0-9, ]+") t)
        trams-seq (re-seq (re-pattern "[^0-9]*trams*[.]* +[Nn]o.[0-9, ]+") trams-str)
        trams-map (zipmap
                   (map #(first (str/split % #"\s*trams*[.]* +[Nn]o.\s*")) trams-seq)
                   (map #(str/split (second (str/split % #"\s*trams*[.]* +[Nn]o.\s*")) #",\s*") trams-seq))]
    trams-map))

(defn parse-metro [t]
  (re-find (re-pattern "(?:.* - )*[Mm]etro .*?(?=$|, [A-Za-z]{2,})") t))
