(ns ica-chatbot.regex
  (:require [clojure.data.json :as json]))
  (:require [clojure.string :as str])

  (defn match-regex [regex input]
     (re-matches (re-pattern regex) input))()
  
  
   (defn get-intent [input]
     (cond
       (not (nil? (match-regex #".*(can|possible).*(bicycle|cycle|bike|ride a bike).*" input))) :biking
       (not (nil? (match-regex #".*(is|there|where|can|possible).*(toilet|wc|bathroom|restroom|rest room).*" input))) :wc
       (not (nil? (match-regex #".*(is|there|where|can|possible).*(attractions|memorial|nature trail|nature|trees).*" input))) :attractions
       (not (nil? (match-regex #".*(can|possible).*(skiing|ski).*" input))) :skiing
       (not (nil? (match-regex #".*(can|possible).*(skating|skate).*" input))) :skating
       (not (nil? (match-regex #".*(is|there|where|can|possible).*(sport|sports|play sports|do sports).*" input))) :sports
       (not (nil? (match-regex #".*(is|there|where|can|possible).*(playground|play).*" input))) :playground
       (not (nil? (match-regex #".*(can|possible).*(bring a dog|dogs|dog|bring dogs).*" input))) :dogs
       (not (nil? (match-regex #".*(way to|get to|transportation|transport).*" input))) :transportation
       (not (nil? (match-regex #".*(can|possible|is|there|where).*(parking|car parking|park|car).*" input))) :parking  
       (not (nil? (match-regex #".*(reviews|testimonials|feedback)." input))) :reviews
       (not (nil? (match-regex #".*(:finished|bye|done|exit|quit)"))) :exit
       :else :unknown))
  
  
   (defn parse-input []
     (let [input (read-line)
           intent (get-intent input)]
     )) 