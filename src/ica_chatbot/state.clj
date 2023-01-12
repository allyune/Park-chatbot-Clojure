(ns ica-chatbot.state
  (:use [org.clojars.cognesence.matcher.core]
        [clojure.set])
  (:require [ica-chatbot.regex :as regex]
            [ica-chatbot.system :as system :only [print-out unknown-input-reaction get-user-input bot-exit]]
            [clojure.string :as str]))

(def state '#{(park nil) (intent nil) (module :default)})

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

(defn update-park-intent [old-state park intent]
  (-> old-state (apply-op (:update-park ops) park) (apply-op (:update-intent ops) intent)))

(defn update-park [old-state park]
  (apply-op old-state (:update-park ops) park))

(defn update-intent [old-state intent]
  (apply-op old-state (:update-intent ops) intent))

(defn update-module [old-state module]
  (apply-op old-state (:update-module ops) module))

(defn update-all [old-state park intent module]
  (-> old-state (apply-op (:update-park ops) park)
                (apply-op (:update-intent ops) intent)
                (apply-op (:update-module ops) module)))

(defn update-state [input old-state]
  (cond
    (= (regex/get-intent input) :exit) (system/bot-exit)
    :else
      (let [curr-park (mfind* ['((park ?p)) old-state] (? p))
            curr-intent (mfind* ['((intent ?i)) old-state] (? i))
            new-park (regex/get-park input)
            new-intent (regex/get-intent input)
            new-module (regex/get-module input)]
          ;;cond matches current state,
          ;;mcond matches new state parsed from user input
          (cond
            ;when switching to non-default module, intent is reset to nil for the next iteration
            (not (nil? new-module)) (update-all old-state new-park nil new-module)
            ;(nil nil)
            (and (nil? curr-park) (nil? curr-intent))
              (update-all old-state new-park new-intent :default)
            ;(?p nil)
            (and (not (nil? curr-park)) (nil? curr-intent))
              (mcond [(list new-park new-intent)]
                ((nil nil) (-> old-state (update-park nil) (update-module :default)))
                ((nil ?i) (-> old-state (update-intent (? i)) (update-module :default)))
                ((?p nil) (-> old-state (update-park (? p)) (update-module :default)))
                ((?p ?i) (update-all old-state (? p) (? i) :default)))
            ;(nil ?i)
            (and (nil? curr-park) (not (nil? curr-intent)))
              (mcond [(list new-park new-intent)]
                ((nil nil) (-> old-state (update-intent nil) (update-module :default)))
                ((?p nil) (-> old-state (update-park (? p)) (update-module :default)))
                ((nil ?i) (-> old-state (update-intent (? i)) (update-module :default)))
                ((?p ?i) (update-all old-state (? p) (? i) :default)))
            ;(?p ?i)
            (not (some nil? (list curr-park curr-intent)))
              (mcond [(list new-park new-intent)]
                ((?p nil) (update-all old-state (? p) nil :default))
                ((nil ?i) (-> old-state (update-intent (? i)) (update-module :default)))
                ((?p ?i) (update-all old-state (? p) (? i) :default)))))))
