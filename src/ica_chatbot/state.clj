(ns ica-chatbot.state
  (:use [org.clojars.cognesence.matcher.core])
  (:use [clojure.set])
  (:require [ica-chatbot.regex :as regex :only [get-intent]])
  (:require [ica-chatbot.system :as system :only [print-out unknown-input-reaction get-user-input bot-exit]])
  (:require [clojure.string :as str]))

(def state '#{(park nil) (intent nil) (username nil)})

(def ops
    '{:add-park {:pre ((park nil) (intent ?i) (username ?u))
                 :del ((park nil))
                 :add park}
      :add-intent {:pre ((park ?p) (intent nil) (username ?u))
                   :del ((intent nil))
                   :add intent}
      :add-username {:pre ((park ?p) (intent ?i) (username nil))
                    :del ((username nil))
                    :add username}
      :update-park {:pre ((park ?p) (intent ?i) (username ?u))
                    :del ((park ?p))
                    :add park}
      :update-intent {:pre ((park ?p) (intent ?i) (username ?u))
                   :del ((intent ?i))
                   :add intent}})

(defn apply-op
  [state
  {:keys [pre del add]}
   new-val]
(mfind* [pre state]
(union #{(list add new-val)}
(difference
state
(mout del)))))

(defn update-both [old-state park intent]
  (apply-op (apply-op old-state (:update-park ops) park) (:update-intent ops) intent))

(defn add-both [old-state park intent]
  (apply-op (apply-op old-state (:add-park ops) park) (:add-intent ops) intent))

(defn add-park [old-state park]
  (apply-op old-state (:add-park ops) park))

(defn update-park [old-state park]
  (apply-op old-state (:update-park ops) park))

(defn add-intent [old-state intent]
  (apply-op old-state (:add-intent ops) intent))

(defn update-intent [old-state intent]
  (apply-op old-state (:update-intent ops) intent))

(defn add-park-update-intent [old-state park intent]
  (apply-op (apply-op old-state (:add-park ops) park) (:update-intent ops) intent))

(defn update-park-add-intent [old-state park intent]
  (apply-op (apply-op old-state (:update-park ops) park) (:add-intent ops) intent))

(defn analyze-input [input old-state]
  (if (= (regex/get-intent input) :exit)
    (system/bot-exit))
    (let [curr-park (mfind* ['((park ?p)) old-state] (? p))
          curr-intent (mfind* ['((intent ?i)) old-state] (? i))
          new-park (regex/get-park input)
          new-intent (regex/get-intent input)]
          (cond
            (and (nil? curr-park) (nil? curr-intent))
              (add-both old-state new-park new-intent)
            (and (not (nil? curr-park)) (nil? curr-intent))
              (mcond [(list new-park new-intent)]
                ((nil nil) (update-park old-state nil))
                ((nil ?i) (add-intent old-state new-intent))
                ((?p nil) (update-park old-state (? p)))
                ((?p ?i) (update-park-add-intent old-state (? p) (? i))))
            (and (nil? curr-park) (not (nil? curr-intent)))
              (mcond [(list new-park new-intent)]
                ((nil nil) (update-intent old-state nil))
                ((?p nil) (add-park old-state (? p)))
                ((nil ?i) (update-intent old-state (? i)))
                ((?p ?i) (add-park-update-intent old-state (? p) (? i))))
            (not (some nil? (list curr-park curr-intent)))
              (mcond [(list new-park new-intent)]
                ((?p nil) (update-both old-state (? p) nil))
                ((nil ?i) (update-intent old-state (? i)))
                ((?p ?i) (update-both old-state (? p) (? i)))))))
