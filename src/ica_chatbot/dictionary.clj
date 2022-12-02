(ns ica-chatbot.dictionary
  (:require [clojure.java.io :as io])
  (:require [clojure.data.json :as json]))

(def parks-json (io/resource "parks.json"))

(def parks-info (json/read-json (slurp parks-json)))

(def intent-categories '([:wc "restroom" :facilities]
                         [:parking "parking" :facilities]
                         [:playground "playground" :facilities]
                         [:restaurant "at least one restaurant" :facilities]
                         [:skating "skate" :activities]
                         [:sports "do sports" :activities]
                         [:dogs "walk a dog" :activities]
                         [:biking "ride a bike" :activities]
                         [:attractions ["attractions", "things to see", "tourist sights"] :attractions]))

;more to be added
(def park-names {:letna "Letná"
                 :frantiskanska-zahrada "Frantiskanska Zahrada"})

;more to be added
(def google-place-ids {:letna "ChIJjQpb2NqUC0cRaCCIBrCgONQ"})

;;Positive responses
(def Positive #{"Yes" "YES" "OK" "yes" "Yes please" "yes please" "yeah" "yep"})

;;Negative responses
(def Negative #{"No" "Nope" "NO" "no" "NOPE"})

;;Other responses
(def Other #{"Not sure" "Maybe" "not sure"})

;;User prompts based on the state of the session
(def prompts {:start "What would you like to know?"
              :in-progress "What else would you like to know?"})
