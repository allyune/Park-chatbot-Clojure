(ns ica-chatbot.answers
  (:require [ica-chatbot.parser :as parser])
  (:use [ica-chatbot.system :only [print-out]])
  (:use [ica-chatbot.dictionary])
  (:use [org.clojars.cognesence.matcher.core])
  (:require [clojure.string :as str]))


(defn get-info-category [intent]
  (mfind [[intent '?hrintent '?category] intent-categories] (? category)))


(defn get-park-info [park intent]
  (get-in parks-info [park intent]))


(defn info-found? [park intent]
    (not (nil? (get-park-info park intent))))


(defn info-not-found [park intent]
    (print-out (format "Sorry, I don't have info about %s in %s" (name intent) (get park-names park))))


(defn print-facilities [park intent->phrase response]
  (case response
    true (print-out (format "Yes, there is a %s in %s" intent->phrase park))
    false (print-out (format "Unfortunately, there is no %s in %s" intent->phrase park))))


(defn print-activities [park intent->phrase response]
  (case response
    true (print-out (format "Yes you can %s in %s" intent->phrase park))
    false (print-out (format "Unfortunately, you can't %s in %s. Try checking another park." intent->phrase park))))


(defn print-attractions [park intent->phrase response]
  (print-out (format "ChatBot: You will find the following %s in %s: %s."
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
            (print-out "You can use the following metro stations/lines to get to the park: ")
            (print-out metros)))
        (if (not (nil? trams))
            (do
            (print-out "You can take the tram to the park:")
            (doseq [[k v] trams] (print-out (format "Take lines %s to the station %s" (str/join ", " v) k))))))
  (catch Exception e
        (info-not-found park :transportation))))


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
  (catch Exception e
        (info-not-found park intent))))
