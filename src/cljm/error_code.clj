(ns cljm.error-code)

(defn make-error-code [status code message]
  {:status status
   :body {:errcode code
          :error message}})

(defn not-found [message]
  (make-error-code 404 "M_NOT_FOUND" message))

(def route-not-found (not-found "Route not found"))

(def internal-error (make-error-code 500
                                     "M_INTERNAL_SERVER_ERROR"
                                     "Internal server error"))

(def not-json (make-error-code 400 "M_NOT_JSON" "Request did not contain valid JSON"))
