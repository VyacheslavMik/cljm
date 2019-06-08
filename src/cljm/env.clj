(ns cljm.env)

(defn env [name & [default]]
  (if-let [v (System/getenv name)]
    v
    (if default
      default
      (throw (Exception. (str name " is required"))))))
    
(def port        (env "PORT" 2019))
(def pg-user     (env "PGUSER"))
(def pg-password (env "PGPASSWORD"))
(def pg-port     (env "PGPORT"))
(def pg-host     (env "PGHOST"))
(def pg-database (env "PGDATABASE"))
