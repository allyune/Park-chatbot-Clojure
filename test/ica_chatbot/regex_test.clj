(ns ica-chatbot.regex-test
  (:require [clojure.test :refer :all]
            [ica-chatbot.regex :as regex]))

(def intent-input {:biking '("can I ride a bike?"
                              "are bicycles allowed?"
                              "cycling in the park"
                              "can I go biking"
                              "can i cycle"
                              "is it possible to bring a bike"
                              "-bring bike to the park")
                    :wc '("is there a toilet"
                          "is there wc?"
                          "can I use the bathroom"
                          "are there restrooms?"
                          "rest rooms"
                          "wc in Letna?"
                          "water closet available?")
                    :attractions '("what can I see in Letna"
                                   "what are the attractions"
                                   "is it good for sightseeing?"
                                   "what are some tourist sights?")
                    :skiing '("can I ski in Letna?"
                                   "is it possible to ski in letna?"
                                   "skiing available?"
                                   "skiing")})

;;;;;works but not informative

; (deftest get-intent-test
;   (testing "One of the inputs does not match to a correct intent"
;   (is (every? true?
;     (for [[k v] intent-input]
;       (every? true? (map (fn [x]
;         (= (regex/get-intent x) k)) v)))))))


(deftest get-intent-test
  (testing "Where wc intent should not match"
    (is (not-any? true? (map (fn [x]
                        (= (regex/get-intent x) :wc))
                        '("where is a bathroom in Letna?"
                          "where is wc in the park?"
                          "where rest rooms")))))
  (testing "Biking intent matching"
    (is (every? true? (map (fn [x]
                        (= (regex/get-intent x) :biking))
                        (:biking intent-input)))))
  (testing "WC intent matching"
    (is (every? true? (map (fn [x]
                        (= (regex/get-intent x) :wc))
                        (:wc intent-input)))))
  (testing "Attractions intent matching"
    (is (every? true? (map (fn [x]
                        (= (regex/get-intent x) :attractions))
                        (:attractions intent-input)))))
  (testing "Skiing intent matching"
    (is (every? true? (map (fn [x]
                        (= (regex/get-intent x) :skiing))
                        (:skiing intent-input))))))
  ; (testing "Skating intent matching"
  ;   (is (every? true? (map (fn [x]
  ;                       (= (regex/get-intent x) :skating))
  ;                       (:skating intent-input)))))
  ; (testing "Sports intent matching"
  ;   (is (every? true? (map (fn [x]
  ;                       (= (regex/get-intent x) :sports))
  ;                       (:sports intent-input)))))
  ; (testing "Playground intent matching"
  ;   (is (every? true? (map (fn [x]
  ;                       (= (regex/get-intent x) :playground))
  ;                       (:playground intent-input)))))
  ; (testing "Dogs intent matching"
  ;   (is (every? true? (map (fn [x]
  ;                       (= (regex/get-intent x) :dogs))
  ;                       (:dogs intent-input)))))
  ; (testing "Transportation intent matching"
  ;   (is (every? true? (map (fn [x]
  ;                       (= (regex/get-intent x) :transportation))
  ;                       (:transportation intent-input)))))
  ; (testing "Parking intent matching"
  ;   (is (every? true? (map (fn [x]
  ;                       (= (regex/get-intent x) :parking))
  ;                       (:parking intent-input)))))
  ; (testing "Restaurant intent matching"
  ;   (is (every? true? (map (fn [x]
  ;                       (= (regex/get-intent x) :restaurant))
  ;                       (:restaurant intent-input)))))
  ; (testing "Reviews intent matching"
  ;   (is (every? true? (map (fn [x]
  ;                       (= (regex/get-intent x) :reviews))
  ;                       (:reviews intent-input)))))
  ; (testing "Exit intent matching"
  ;   (is (every? true? (map (fn [x]
  ;                       (= (regex/get-intent x) :exit))
  ;                       (:exit intent-input)))))
