(ns ica-chatbot.answers
  (:require [ica-chatbot.parser :as parser])
  (:require [clojure.java.io :as io])
  (:require [clojure.data.json :as json])
  (:use [org.clojars.cognesence.matcher.core])
  (:use [ica-chatbot.dictionary])
  (:require [clojure.string :as str]))


;Converting info from JSON file to dictionary
(def parks-json (io/resource "parks.json"))
(def parks-info (json/read-json (slurp parks-json)))


(defn get-park-info [park request]
  (let [info-about-park (get parks-info park)]
  (get info-about-park request)))


(defn get-intent-category [intent]
  (mfind [[intent '?hrintent '?category] intent-categories] (? category)))


(defn print-facilities [park intent-translated response]
  (case response
    true (println (format "Yes, there is a %s in %s" intent-translated park))
    false (println (format "Unfortunately, there is no %s in %s" intent-translated park))))


(defn print-activities [park intent-translated response]
  (case response
    true (println (format "Yes you can %s in %s" intent-translated park))
    false (println (format "Unfortunately, you can't %s in %s. Try checking another park." intent-translated park))))


(defn print-attractions [park attractions-translated response]
  (println (format "ChatBot: You will find the following %s in %s: %s."
                    (rand-nth attractions-translated)
                    park
                    response)))


(defn print-park-info [park intent]
  (let [intent-category (get-intent-category intent)
        intent-park-info (get-park-info park intent)
        park-name (get park-names park)]
        (case intent-category
          :facilities (print-facilities park-name (mfind [[intent '?hrintent '?category] intent-categories] (? hrintent)) intent-park-info)
          :activities (print-activities park-name (mfind [[intent '?hrintent '?category] intent-categories] (? hrintent)) intent-park-info)
          :attractions (print-attractions park-name (mfind [[intent '?hrintent '?category] intent-categories] (? hrintent)) intent-park-info)
          :unknown (println " "))))


(defn print-transportation [park]
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
            (doseq [[k v] trams] (println (format "Take lines %s to the station %s" (str/join ", " v) k)))))))
