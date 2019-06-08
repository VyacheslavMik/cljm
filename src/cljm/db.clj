(ns cljm.db
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [cljm.env :as env]
            [cljm.pg-pool :as pool]))

(defn database-url []
  (str "jdbc:postgresql://" env/pg-host ":" env/pg-port
       "/" env/pg-database
       "?user=" env/pg-user
       "&password=" env/pg-password "&stringtype=unspecified"))

(defn database-opts []
  (let [database-url (database-url)]
    {:connection-timeout  30000
     :idle-timeout        10000
     :minimum-idle        0
     :maximum-pool-size   10
     :connection-init-sql "select 1"
     :data-source.url     database-url}))

(defn make-datasource []
  (let [ds-opts (database-opts)
        ds      (pool/create-pool ds-opts)
        pool    {:datasource ds}]
    pool))

(def db (make-datasource))

(defn query [hsql]
  (let [sql (sql/format hsql :quoting :ansi)]
    (jdbc/query db sql)))
