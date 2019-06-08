(ns cljm.routes
  (:require [clojure.string :as str]))

(defonce routes (atom {}))
;; (reset! routes {})

(defn parse-uri [uri]
  (let [parts (str/split (subs uri 1) #"/")]
    parts))

(defn make-path [method uri]
  (into [method] (parse-uri uri)))

(defmacro defroute [method uri f]
  (let [path (make-path method uri)]
    `(swap! ~'cljm.routes/routes assoc-in ~path ~f)))

(defn find-route [method uri]
  (let [path (make-path method uri)]
    (get-in @routes path)))
  
