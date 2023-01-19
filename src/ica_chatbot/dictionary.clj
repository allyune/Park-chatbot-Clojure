(ns ica-chatbot.dictionary
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]))

(def parks-json (io/resource "parks.json"))

(def parks-info (json/read-json (slurp parks-json)))

(def intent-categories '([:wc "restroom" :facilities]
                         [:parking "parking" :facilities]
                         [:playground "playground" :facilities]
                         [:restaurant "at least one restaurant" :facilities]
                         [:skating "skating" :activities]
                         [:sports "doing sports" :activities]
                         [:dogs "walking a dog" :activities]
                         [:biking "riding a bike" :activities]
                         [:attractions ["attractions", "things to see", "tourist sights"] :attractions]))

(def park-names {:letna "Letná"
                 :bertramka "Bertramka"`
                 :riegrovy-sady "Riegrovy sady"
                 :frantiskanska-zahrada "Františkánská zahrada"
                 :obora-hvezda "Obora Hvězda"
                 :kampa "Kampa"
                 :kinskeho-zahrada "Kinského zahrada"
                 :klamovka "Klamovka"
                 :ladronka "Ladronka"
                 :petrin "Petřín"
                 :stromovka "Stromovka"
                 :vysehrad "Vyšehrad"})

(def google-place-ids {:letna "ChIJjQpb2NqUC0cRaCCIBrCgONQ"
                       :bertramka "ChIJGaAUz1SUC0cR2AUnGYuNsrA"
                       :riegrovy-sady "ChIJa80b9JqUC0cRwYdD5WqTMDc"
                       :frantiskanska-zahrada "ChIJL2WboO2UC0cRlw47PEdAPVQ"
                       :obora-hvezda "ChIJwd-VWtO_C0cR4cFrUtJDO3Q"
                       :kampa "ChIJ4eTG7OSUC0cRXMru0PQo-Kk"
                       :kinskeho-zahrada "ChIJ3UB98gKVC0cRwGV3D_tmxY4"
                       :klamovka "ChIJ0yzCP6aVC0cRrBAF4iVfwa0"
                       :ladronka "ChIJbbnu8HWVC0cRN6y9rOEUqsw"
                       :petrin "ChIJrS4zTP2UC0cRMI7zhXJrRX0"
                       :stromovka "ChIJ2QFPvc-UC0cR-yep7ub8N78"
                       :vysehrad "ChIJCd13PWeUC0cRZihTX2RPn0w"})

;;Positive responses
(def Positive #{"Yes" "YES" "OK" "yes" "Yes please" "yes please" "yeah" "yep"})

;;Negative responses
(def Negative #{"No" "Nope" "NO" "no" "NOPE"})

;;Other responses
(def Other #{"Not sure" "Maybe" "not sure"})

;;User prompts based on the state of the session
(def prompts {:start "What would you like to know?"
              :in-progress "What else would you like to know?"})
