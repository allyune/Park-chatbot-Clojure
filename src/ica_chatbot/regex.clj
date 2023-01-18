(ns ica-chatbot.regex
  (:require [clojure.string :as str])
  (:use [org.clojars.cognesence.matcher.core]))

(defn match-regex
  "Matches input against regular expression (regex). Returns boolean"
  [regex input]
  (re-matches (re-pattern regex) input))

(defn get-intent
  "matches user input against regular expressions corresponding to user intents. Returns first match as an intent keyword"
  [input]
  (cond
    (not (nil? (match-regex #".*(bike|bicycle|cycle|biking|cycling|biking|bike).*" input))) :biking
    (not (nil? (match-regex #"^(?!where).*(toilet|wc|bathroom|restroom|rest rooms|w c|water closet).*" input))) :wc
    (not (nil? (match-regex #".*(attractions|what to see|what can I see|things to see|sights|sightseeing|memorial|nature trail|nature|trees).*" input))) :attractions
    (not (nil? (match-regex #".*(can|possible)*.*(skiing|ski).*" input))) :skiing
    (not (nil? (match-regex #".*(can|possible)*.*(skating|skate).*" input))) :skating
    (not (nil? (match-regex #"(?!.*transport).*(sport|sports|play sports|do sports|exercise|train|workout|work out|yoga|fitness).*" input))) :sports
    (not (nil? (match-regex #"^(?!where).*(playground|jungle gym|kids to play|kids play|kids can play|children to play|children can play).*" input))) :playground
    (not (nil? (match-regex #".*(bring a dog|dogs|dog|bring dogs|puppy).*" input))) :dogs
    (not (nil? (match-regex #".*(way to|get to|transportation|transport|commute).*" input))) :transportation
    (not (nil? (match-regex #"^(?!where).*(parking|car parking|car).*" input))) :parking
    (not (nil? (match-regex #".*(restaurant|cafe|bistro|food|breakfast|lunch|dinner).*" input))) :restaurant
    (not (nil? (match-regex #".*(review|testimonial|feedback|people say).*" input))) :reviews
    (not (nil? (match-regex #".*(finish|bye|done|exit|quit)" input))) :exit
    :else nil))


(defn get-park [input]
  (cond
    (not (nil? (match-regex #".*(letna|letná).*" input))) :letna
    (not (nil? (match-regex #".*bertramka.*" input))) :bertramka
    (not (nil? (match-regex #".*riegrovy.*" input))) :riegrovy-sady
    (not (nil? (match-regex #".*(frantiskanska|františkánská|frantiskánská|františkanská|františkánska|františkanská).*" input))) :frantiskanska-zahrada
    (not (nil? (match-regex #".*(obora hvezda|obora hvězda).*" input))) :obora-hvezda
    (not (nil? (match-regex #".*kampa.*" input))) :kampa
    (not (nil? (match-regex #".*(kinskeho|kinského|kinskych|kinských).*" input))) :kinskeho-zahrada
    (not (nil? (match-regex #".*klamovka.*" input))) :klamovka
    (not (nil? (match-regex #".*ladronka.*" input))) :ladronka
    (not (nil? (match-regex #".*(petřín|petrin|petřin|petrín).*" input))) :petrin
    (not (nil? (match-regex #".*stromovka.*" input))) :stromovka
    (not (nil? (match-regex #".*(vysehrad|vyšehrad).*" input))) :vysehrad
    :else nil))

(defn get-module [input]
  (cond
    (not (nil? (match-regex #".*(recommend|where can|where I can).*" input))) :recommend
    (not (nil? (match-regex #".*(identify a dog|see a dog|dog breed).*" input))) :dtree
    :else nil))

(defn get-yes-no [input]
  (cond
    (not (nil? (match-regex #".*(yes|yeah|positive)" input))) :yes
    (not (nil? (match-regex #".*(no|nope)" input))) :no))

(defn parse-trams
  "Parses tram lines and stations from a string containing transportation info.
  Returns a map where keys are station names and values are corresponding lines"
  [t]
  (let [trams-str (re-find (re-pattern "(?:(?<=, )[^0-9]*|^(?:(?!Metro|metro|METRO|bus|Bus|BUS).)*)trams*[.]* +[Nn]o.[0-9, ]+") t)
        trams-seq (re-seq (re-pattern "[^0-9]*trams*[.]* +[Nn]o.[0-9, ]+") trams-str)
        trams-map (zipmap
                   (map #(first
                           (str/split % #"\s*trams*[.]* +[Nn]o.\s*")) trams-seq)
                   (map #(str/split (second
                                      (str/split % #"\s*trams*[.]* +[Nn]o.\s*")) #",\s*")
                                       trams-seq))]
    trams-map))

(defn parse-metro
  "Parses metro line/s and stations from a string containing transportation info.
   Returns a string."
  [t]
  (re-find (re-pattern "(?:.* - )*[Mm]etro .*?(?=$|, [A-Za-z]{2,})") t))
