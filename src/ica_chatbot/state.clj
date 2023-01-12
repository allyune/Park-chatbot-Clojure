(ns ica-chatbot.state
  (:use [org.clojars.cognesence.matcher.core]
        [clojure.set])
  (:require [ica-chatbot.regex :as regex :only [get-intent]]
            [ica-chatbot.system :as system :only [print-out unknown-input-reaction get-user-input bot-exit]]
            [clojure.string :as str]))

(def state '#{(park nil) (intent nil) (module nil)})

(def ops
    '{:update-park {:pre ((park ?p) (intent ?i))
                    :del ((park ?p) (park nil))
                    :add park}
      :update-intent {:pre ((park ?p) (intent ?i))
                      :del ((intent ?i) (intent nil))
                      :add intent}
      :update-module {:pre ((module ?m))
                      :del ((module ?m) (module nil))
                      :add module}})

(defn apply-op [state
                {:keys [pre del add]}
                new-val]
(mfind* [pre state]
  (union #{(list add new-val)}
    (difference state (mout del)))))

(defn update-both [old-state park intent]
  (-> old-state (apply-op (:update-park ops) park) (apply-op (:update-intent ops) intent)))

(defn update-park [old-state park]
  (apply-op old-state (:update-park ops) park))

(defn update-intent [old-state intent]
  (apply-op old-state (:update-intent ops) intent))

(defn update-module [old-state module]
  (apply-op old-state (:update-module ops) module))

(defn update-state [input old-state]
  (if (= (regex/get-intent input) :exit)
    (system/bot-exit))
    (let [curr-park (mfind* ['((park ?p)) old-state] (? p))
          curr-intent (mfind* ['((intent ?i)) old-state] (? i))
          new-park (regex/get-park input)
          new-intent (regex/get-intent input)]
          (cond
            ;(nil nil)
            (and (nil? curr-park) (nil? curr-intent))
              (update-both old-state new-park new-intent)
            ;(?p nil)
            (and (not (nil? curr-park)) (nil? curr-intent))
              (mcond [(list new-park new-intent)]
                ((nil nil) (update-park old-state nil))
                ((nil ?i) (update-intent old-state new-intent))
                ((?p nil) (update-park old-state (? p)))
                ((?p ?i) (update-both old-state (? p) (? i))))
            ;(nil ?i)
            (and (nil? curr-park) (not (nil? curr-intent)))
              (mcond [(list new-park new-intent)]
                ((nil nil) (update-intent old-state nil))
                ((?p nil) (update-park old-state (? p)))
                ((nil ?i) (update-intent old-state (? i)))
                ((?p ?i) (update-both old-state (? p) (? i))))
            ;(?p ?i)
            (not (some nil? (list curr-park curr-intent)))
              (mcond [(list new-park new-intent)]
                ((?p nil) (update-both old-state (? p) nil))
                ((nil ?i) (update-intent old-state (? i)))
                ((?p ?i) (update-both old-state (? p) (? i)))))))
