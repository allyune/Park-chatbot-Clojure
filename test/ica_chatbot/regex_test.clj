(ns ica-chatbot.regex-test
  (:require [clojure.test :refer :all]
            [ica-chatbot.regex :as regex]))

(def intent-input {:biking '("can I ride a bike?"
                              "are bicycles allowed?"
                              "cycling in the park"
                              "can I go biking"
                              "can i cycle"
                              "is it possible to bring a bike"
                              "-bring bike to the park")})

(deftest get-intent-test
  (testing "One of the inputs does not match to a correct intent"
    (is (every? true? (map (fn [x]
                        (= (regex/get-intent x) :biking))
                        (:biking intent-input))))
    (is (every? true? (map (fn [x]
                        (= (regex/get-intent x) :wc))
                        '("toilet" "is water closet available"))))))
