(ns ica-chatbot.reviews
  (:require [clojure.data.json :as json])
  (:require [ica-chatbot.secrets :as secrets])
  (:require [ica-chatbot.system :as system :only [print-out]])
  (:use [ica-chatbot.dictionary]))

(defn print-review [review]
  (let [author (get review "author_name" "not available")
        text (get review "text" "not available")
        rating (get review "rating" "not available")
        date (get review "relative_time_description" "not available")]
    (system/print-out "Review: ")
    (println "Author: " author)
    (println "Date: " date)
    (println "Rating: " rating "stars")
    (println text)))

(defn print-latest-reviews [park]
  (let [place-id (get google-place-ids park)
        url (format "https://maps.googleapis.com/maps/api/place/details/json?placeid=%s&key=%s" place-id secrets/api-key)
        response (get (json/read-str (slurp url)) "result")
        reviews (get response "reviews")]
    (print-review (first reviews))
    (print-review (second reviews))
    (print-review (nth reviews 3))))
