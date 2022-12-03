(ns ica-chatbot.answer-test
  (:require [clojure.test :refer :all]
            [ica-chatbot.answers :as answers]))

(deftest get-info-category-test
  (testing "Answers/Get info category test: Intent category matching is incorrect"
  (is (every? true? (map (fn [x]
                      (= (answers/get-info-category x) :facilities))
                      '(:wc :parking :playground :restaurant))))
  (is (every? true? (map (fn [x]
                      (= (answers/get-info-category x) :activities))
                      '(:skating :sports :dogs :biking))))
  (is (not-any? true? (map (fn [x]
                      (= (answers/get-info-category x) :facilitites))
                      '(:skating :sports :dogs :biking :unknown "random" 1234))))))
