(ns cljm.utils
  (:require [cheshire.core :as json]
            [cljm.core :as cljm]
            [clojure.string :as str]
            [matcho.core :refer :all]))

(defn request* [method uri params]
  (let [req (merge {:uri uri :request-method method} params)
        res (cljm/handler req)]
    (update res :body #(json/parse-string % keyword))))

(defmulti request (fn [method _ & _] method))

(defmethod request :get [method url & [body]]
  (request* method url
            {:query-string (cond
                             (nil? body)
                             ""

                             (string? body)
                             body

                             :else
                             (str/join "&" (map (fn [[k v]] (str k "=" v)) body)))}))

(defn make-response [status body]
  {:status status
   :body body})

(defmacro is [request-params should-be]
  `(match (request ~@request-params)
          (make-response ~@should-be)))
