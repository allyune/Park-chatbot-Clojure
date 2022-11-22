(ns ica-chatbot.answers
  (:require [ica-chatbot.parser :as parser])
  (:require [clojure.java.io :as io])
  (:require [clojure.data.json :as json]))


;User intents by category and "translated" into human language
(def intent-facilities {:wc "restroom"
                        :parking "parking"
                        :playground "playground"})
(def intent-activities {:biking "ride a bike"
                        :skating "skate"
                        :sports "do sports"
                        :dogs "walk a dog"})
(def intent-attractions {:attractions ["attractions", "things to see", "tourist sights"]})


;Converting info from JSON file to clojure format
(def parks-json (io/resource "parks.json"))
(def parks-info (json/read-json (slurp parks-json)))


(defn get-park-info [park, request]
  (let [info-about-park (get parks-info park)]
  (get info-about-park request)))


(defn get-intent-category [intent]
  (cond
    (not (nil? (get intent-facilities intent))) :facilities
    (not (nil? (get intent-activities intent))) :activities
    (not (nil? (get intent-attractions intent))) :attractions
    :else :unknown))
  ; (if (not (nil? (get intent-facilities intent)))


(defn get-transportation [park]
  (let [t (get-park-info park :transportation)
        trams (parser/parse-trams t)
        metros (parser/parse-metro t)]
        (if (not (nil? metros))
            (do
            (println "You can use the following metro stations/lines to get to the park: ")
            (println metros)))
        (if (not (nil? trams))
            (do
            (println "You can take the tram to the park:")
            (for [m trams]
              (for [[k v] m] (println (format "Take lines: %s to the station %s" v k))))))))

; (def transportations (for [[k v] parks-info] (:transportation v)))
;
;
; (for [t transportations] (println (re-seq (re-pattern "(?:(?<=, )[^0-9]*|^(?:(?!Metro|metro|METRO|bus|Bus|BUS).)*)trams*[.]* +[Nn]o.[0-9, ]+") t)))
