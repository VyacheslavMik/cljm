(ns cljm.error-code)

(defn make-error-code [status code message]
  {:status status
   :body {:errcode code
          :error message}})

(defn not-found [message]
  (make-error-code 404 "M_NOT_FOUND" message))

(def route-not-found (not-found "Route not found"))
