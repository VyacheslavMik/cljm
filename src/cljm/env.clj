(ns cljm.env)

(def port (or (System/getenv "PORT") 2019))
