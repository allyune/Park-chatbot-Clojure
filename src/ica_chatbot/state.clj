(ns ica-chatbot.state
  (:use [org.clojars.cognesence.matcher.core]
        [clojure.set]
        [ica-chatbot.dtree :only [dtree-find-child the-dtree]])
  (:require [ica-chatbot.regex :as regex]
            [ica-chatbot.system :as system :only [print-out unknown-input-reaction get-user-input bot-exit]]
            [clojure.string :as str]))

(def state '#{(park nil) (intent nil) (module :default) (node nil)})

(def ops
  '{:update-park {:pre ((park ?p) (intent ?i))
                  :del ((park ?p) (park nil))
                  :add park}
    :update-intent {:pre ((park ?p) (intent ?i))
                    :del ((intent ?i) (intent nil))
                    :add intent}
    :update-module {:pre ((module ?m))
                    :del ((module ?m) (module nil))
                    :add module}
    :update-node {:pre ((node ?n))
                  :del ((node ?n) (node nil))
                  :add node}})

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

(defn update-node [old-state node]
  (apply-op old-state (:update-node ops) node))

(defn update-all [old-state park intent module node]
  (-> old-state (apply-op (:update-park ops) park)
      (apply-op (:update-intent ops) intent)
      (apply-op (:update-module ops) module)
      (apply-op (:update-node ops) node)))

(defn update-state [input old-state]
  (let [new-intent (regex/get-intent input)
        curr-node (mfind* ['((node ?n)) old-state] (? n))
        yes-no (regex/get-yes-no input)]
  ;handling exit intent and dtree evaluation
    (cond
      (= new-intent :exit) (system/bot-exit)
      (not (some nil? (list yes-no curr-node))) (update-all old-state nil yes-no :dtree (dtree-find-child curr-node (name yes-no)))
      :else
      (let [curr-park (mfind* ['((park ?p)) old-state] (? p))
            curr-module (mfind* ['((module ?m)) old-state] (? m))
            curr-intent (mfind* ['((intent ?i)) old-state] (? i))
            new-park (regex/get-park input)
            new-module (regex/get-module input)]
          ;;cond matches current state,
          ;;mcond matches new state parsed from user input
        (cond
            ;switching to recommendation module - intent is reset to nil for the next iteration
          (= new-module :recommend)
          (update-all old-state new-park nil new-module nil)
            ;switching to dtree module
          (= new-module :dtree)
          (-> old-state (update-module new-module) (update-node the-dtree) (update-intent :start-dtree))
            ;detecting wrong answer when in dtree mode
          (and (every? nil? (list new-park new-intent new-module)) (= curr-module :dtree))
          (update-all old-state nil nil curr-module curr-node)
            ;if curr-park and intent are empty - updating the status
          (and (nil? curr-park) (nil? curr-intent))
          (update-all old-state new-park new-intent :default nil)
            ;curr state - (?p nil)
          (and (not (nil? curr-park)) (nil? curr-intent))
          (mcond [(list new-park new-intent)]
                 ((nil nil) (-> old-state (update-park nil) (update-module :default) (update-node nil)))
                 ((nil ?i) (-> old-state (update-intent (? i)) (update-module :default) (update-node nil)))
                 ((?p nil) (-> old-state (update-park (? p)) (update-module :default) (update-node nil)))
                 ((?p ?i) (update-all old-state (? p) (? i) :default nil)))
            ;;curr state - (nil ?i)
          (and (nil? curr-park) (not (nil? curr-intent)))
          (mcond [(list new-park new-intent)]
                 ((nil nil) (-> old-state (update-intent nil) (update-module :default) (update-node nil)))
                 ((?p nil) (-> old-state (update-park (? p)) (update-module :default) (update-node nil)))
                 ((nil ?i) (-> old-state (update-intent (? i)) (update-module :default) (update-node nil)))
                 ((?p ?i) (update-all old-state (? p) (? i) :default nil)))
            ;;curr state - (?p ?i)
          (not (some nil? (list curr-park curr-intent)))
          (mcond [(list new-park new-intent)]
                 ((?p nil) (update-all old-state (? p) nil :default nil))
                 ((nil ?i) (-> old-state (update-intent (? i)) (update-module :default) (update-node nil)))
                 ((?p ?i) (update-all old-state (? p) (? i) :default nil))))))))
