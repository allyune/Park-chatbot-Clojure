(ns ica-chatbot.answers
  (:require [ica-chatbot.regex :as regex])
  (:require [ica-chatbot.system :as system :only [print-out]])
  (:use [ica-chatbot.dictionary])
  (:use [org.clojars.cognesence.matcher.core])
  (:require [clojure.string :as str]))

(defn get-info-category [intent]
  "Matches user intent against a list of intents and returns intent category
   as a keyword"
  (mfind [[intent '?hrintent '?category] intent-categories] (? category)))

(defn get-park-info [park intent]
  "Returns value of the intent key for specific park from the parks info dictionary"
  (get-in parks-info [park intent]))

(defn info-not-found [park intent]
  "Prints out message to the user in case intent key is not found in park info dictionary"
  (system/print-out (format "Sorry, I don't have info about %s in %s" (name intent) (get park-names park))))

(defn print-facilities [park intent->phrase response]
  "Prints information about park facilities (intent category = :facilities)
   based on information obtained from park info dictionary"
  (case response
    true (system/print-out (format "Yes, there is a %s in %s" intent->phrase park))
    false (system/print-out (format "Unfortunately, there is no %s in %s" intent->phrase park))))

(defn print-activities [park intent->phrase response]
  "Prints information about park activities (intent category = :activities)
   based on information obtained from park info dictionary"
  (case response
    true (system/print-out (format "Yes you can %s in %s" intent->phrase park))
    false (system/print-out (format "Unfortunately, you can't %s in %s. Try checking another park." intent->phrase park))))

(defn print-attractions [park intent->phrase response]
  "Prints information about park attractions (intent category = :attractions)
   based on information obtained from park info dictionary"
  (system/print-out (format "ChatBot: You will find the following %s in %s: %s."
                            (rand-nth intent->phrase)
                            park
                            response)))

(defn print-transportation [park]
  "Prints information about available transportation to the park
   based on parsed information about the park"
  (try
    (let [t (get-park-info park :transportation)
          trams (regex/parse-trams t)
          metros (regex/parse-metro t)]
      (when-not (nil? metros)
        (system/print-out "You can use the following metro stations/lines to get to the park: ")
        (system/print-out metros))
      (when-not (nil? trams)
        (system/print-out "You can take the tram to the park:")
        (doseq [[k v] trams] (system/print-out (format "Take lines %s to the station %s" (str/join ", " v) k)))))
    (catch Exception e
      (info-not-found park :transportation))))

(defn print-park-info [park intent]
  "Calls printing function based on the user intent category"
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
