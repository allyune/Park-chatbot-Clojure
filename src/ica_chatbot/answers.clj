(ns ica-chatbot.answers
  (:require [ica-chatbot.parser :as parser])
  (:use [org.clojars.cognesence.matcher.core])
  (:use [ica-chatbot.dictionary])
  (:require [clojure.string :as str]))


(defn get-info-category [intent]
  (mfind [[intent '?hrintent '?category] intent-categories] (? category)))


(defn get-park-info [park intent]
  (get-in parks-info [park intent]))


(defn info-found? [park intent]
    (not (nil? (get-park-info park intent))))


(defn info-not-found [park intent]
    (println (format "Sorry, I don't have info about %s in %s" (name intent) (get park-names park))))


(defn print-facilities [park intent->phrase response]
  (case response
    true (println (format "Yes, there is a %s in %s" intent->phrase park))
    false (println (format "Unfortunately, there is no %s in %s" intent->phrase park))))


(defn print-activities [park intent->phrase response]
  (case response
    true (println (format "Yes you can %s in %s" intent->phrase park))
    false (println (format "Unfortunately, you can't %s in %s. Try checking another park." intent->phrase park))))


(defn print-attractions [park intent->phrase response]
  (println (format "ChatBot: You will find the following %s in %s: %s."
                    (rand-nth intent->phrase)
                    park
                    response)))


(defn print-transportation [park]
  (try
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
            (doseq [[k v] trams] (println (format "Take lines %s to the station %s" (str/join ", " v) k))))))
  (catch Exception e)
  (finally (info-not-found park :transportation))))


(defn print-park-info [park intent]
  (try
  (let [info-category (get-info-category intent)
        park-info (get-park-info park intent)
        park-name (get park-names park)
        intent->phrase (mfind [[intent '?hrintent '?category] intent-categories] (? hrintent))]
        (case info-category
          :facilities (print-facilities park-name intent->phrase park-info)
          :activities (print-activities park-name intent->phrase park-info)
          :attractions (print-attractions park-name intent->phrase park-info)))
  (catch Exception e)
  (finally (info-not-found park intent))))
