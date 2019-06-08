(ns cljm.core
  (:require [org.httpkit.server :as httpkit]
            [cheshire.core :as json]
            [cljm.env :as env]
            [cljm.routes :as routes]
            [cljm.error-code :as error-code]))

(routes/defroute :get "/_matrix/client/versions" (fn [req] (println 'versions req)))

(defn make-response [result]
  (-> result
      (assoc :headers {"Content-Type" "application/json"})
      (update :body json/generate-string)))

(defn handler [req]
  (make-response
   (if-let [route (routes/find-route (:request-method req) (:uri req))]
     {:status 200
      :body (apply route req)}
     error-code/route-not-found)))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn -main [& args]
  (reset! server (httpkit/run-server #'handler {:port env/port})))
