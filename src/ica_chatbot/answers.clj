(ns ica-chatbot.answers
  (:require [clojure.java.io :as io])
  (:require [clojure.data.json :as json]))

  (def parks-json (io/resource "parks.json"))
  (def parks-info (json/read-str (slurp parks-json)))

  (defn get-park-info [park, request]
    (let [info-about-park (get parks-info park)]
    (get info-about-park request "not available")))
