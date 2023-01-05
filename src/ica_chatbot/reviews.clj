(ns ica-chatbot.reviews
  (:require [clojure.data.json :as json])
  (:require [ica-chatbot.secrets :as secrets])
  (:require [ica-chatbot.system :as system :only [print-out]])
  (:use [ica-chatbot.dictionary]))

(defn print-review
  "Prints a single review obtained from [[reviews/print-latest-reviews]]."
  [review]
  (let [author (get review "author_name" "not available")
        text (get review "text" "not available")
        rating (get review "rating" "not available")
        date (get review "relative_time_description" "not available")]
    (println "Author: " author)
    (println "Date: " date)
    (println "Rating: " rating "stars")
    (println text)))


(defn print-latest-reviews
  "Calls Google Places API with a place id corresponding to the requested park.
   Prints 3 reviews by calling [[reviews/print-review]]."
   [park]
  (let [place-id (get google-place-ids park)
        url (format "https://maps.googleapis.com/maps/api/place/details/json?placeid=%s&key=%s" place-id secrets/api-key)
        response (get (json/read-str (slurp url)) "result")
        reviews (get response "reviews")]
    (doseq [n (range 0 (if (< (count reviews) 3) (count reviews) 3))]
      (system/print-out (str "Review " (+ n 1)))
      (print-review (nth reviews n)))))
