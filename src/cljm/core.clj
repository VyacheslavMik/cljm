(ns cljm.core
  (:require [org.httpkit.server :as httpkit]
            [cheshire.core :as json]
            [clojure.walk :as walk]
            [ring.util.codec :as codec]
            [cljm.env :as env]
            [cljm.routes :as routes]
            [cljm.error-code :as error-code]
            [clojure.java.io :as io]))

(routes/defroute :get "/_matrix/client/versions" (fn [_] {:versions ["r0.4.0"]}))

(defn make-response [result]
  (-> result
      (assoc :headers {"Content-Type" "application/json"})
      (update :body json/generate-string)))

(defmacro protect [& body]
  (let [handler (last body)
        handler (when (and (list? handler) (= (first handler) 'handler))
                  handler)
        body (if handler
               (butlast body)
               body)]
  `(try
     ~@body
     (catch Throwable t#
       (println t#)
       ~@(if handler
           (rest handler)
           nil)))))

(defmulti parse-params :request-method)

(defmethod parse-params :get [req]
  (when-let [qs (:query-string req)]
    (protect
     [true (walk/keywordize-keys (codec/form-decode qs))]
     (handler [false error-code/not-json]))))

(defn parse-body [{:keys [body]}]
  (protect
    [true
     (when body
       (cond
         (string? body)
         (json/parse-string body keyword)

         (instance? java.io.InputStream body)
         (json/parse-stream (io/reader body) keyword)

         :else
         (throw (Exception. "Format not supported"))))]
    (handler [false error-code/not-json])))

(defmethod parse-params :post [req] (parse-body req))
(defmethod parse-params :put  [req] (parse-body req))

(defn handler [req]
  (make-response
   (if-let [route (routes/find-route (:request-method req) (:uri req))]
     (protect
      (let [[parsed? body] (parse-params req)]
        (if parsed?
          {:status 200
           :body (route {:body body
                         :request req})}
          body))
      (handler error-code/internal-error))
     error-code/route-not-found)))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn -main [& args]
  (reset! server (httpkit/run-server #'handler {:port env/port})))
