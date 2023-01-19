(ns ica-chatbot.answers
  (:require [ica-chatbot.regex :as regex]
            [ica-chatbot.system :as system :only [print-out unknown-input-reaction bot-exit]]
            [ica-chatbot.reviews :as reviews :only [print-latest-reviews]]
            [clojure.string :as str])
  (:use [ica-chatbot.dictionary]
        [org.clojars.cognesence.matcher.core]))

(defn intent->phrase [intent]
  (mfind [[intent '?hrintent '?category] intent-categories] (? hrintent)))

(defn get-info-category
  "Matches user intent against a list of intents and returns intent category
   as a keyword"
  [intent]
  (mfind [[intent '?hrintent '?category] intent-categories] (? category)))

(defn get-park-info
  "Returns value of the intent key for specific park from the parks info dictionary"
  [park intent]
  (get-in parks-info [park intent]))

(defn get-available-info [park-name]
  (let [park (get parks-info park-name)
        keys (map name (keys park))]
    (str/join ", " (concat keys ["park reviews"]))))

(defn get-available-info-all-parks []
  (let [intents (distinct (apply concat (map keys (vals parks-info))))]
    (str/replace (str/join ", " (map name intents)) #"dogs" "walking a dog")))

(defn info-not-found
  "Prints out message to the user in case intent key is not found in park info dictionary"
  [park intent]
  (system/print-out (format "Sorry, I don't have info about %s in %s" (name intent) (get park-names park))))

(defn recommend-parks [intent]
  (str/join ", " (map (fn [p]
                        (get park-names p)) (filter #(get-park-info % intent) (keys parks-info)))))

(defn print-recommendations [intent]
  (let [intent->phrase (intent->phrase intent)]
    (system/print-out (format "%s is available in %s" (str/capitalize intent->phrase) (recommend-parks intent)))))

(defn print-facilities
  "Prints information about park facilities (intent category = :facilities)
   based on information obtained from park info dictionary"
  [park intent response]
  (let [intent->phrase (intent->phrase intent)
        park-name (get park-names park)]
    (case response
      true (system/print-out (format "Yes, there is a %s in %s" intent->phrase park-name))
      false (do (system/print-out (format "Unfortunately, there is no %s in %s" intent->phrase park-name))
                (system/print-out (format "You can find %s in the following parks %s"
                                          intent->phrase (recommend-parks intent)))))))

(defn print-activities
  "Prints information about park activities (intent category = :activities)
   based on information obtained from park info dictionary"
  [park intent response]
  (let [intent->phrase (intent->phrase intent)
        park-name (get park-names park)]
    (case response
      true (system/print-out (format "Yes %s is possible in %s" intent->phrase park-name))
      false (do (system/print-out (format "Unfortunately, you can't %s in %s." intent->phrase park-name))
                (system/print-out (format "However, %s is possible in the following parks: %s"
                                          intent->phrase (recommend-parks intent)))))))

(defn print-attractions
  "Prints information about park attractions (intent category = :attractions)
   based on information obtained from park info dictionary"
  [park intent response]
  (let [intent->phrase (intent->phrase intent)
        park-name (get park-names park)]
    (system/print-out (format "You will find the following %s in %s: %s."
                              (rand-nth intent->phrase)
                              park-name
                              response))))

(defn print-transportation
  "Prints information about available transportation to the park
   based on parsed information about the park"
  [park]
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

(defn print-park-info
  "Calls printing function based on the user intent category"
  [park intent]
  (try
    (let [info-category (get-info-category intent)
          park-info (get-park-info park intent)]
      (case info-category
        :facilities (print-facilities park intent park-info)
        :activities (print-activities park intent park-info)
        :attractions (print-attractions park intent park-info)))
    (catch Exception e
      (info-not-found park intent))))

(defn get-answer [park intent]
  (case intent
    :exit (system/bot-exit)
    :reviews (reviews/print-latest-reviews park)
    :unknown (system/unknown-input-reaction)
    :transportation (print-transportation park)
    (print-park-info park intent)))
