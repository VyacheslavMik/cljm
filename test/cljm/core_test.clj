(ns cljm.core-test
  (:require [clojure.test :refer [deftest]]
            [cljm.utils :refer [is]]))

(deftest versions
  (is (:get "/_matrix/client/versions")
      (200 {:versions ["r0.4.0"]})))

(deftest core
  (is (:get "/some-url")
      (404 {:errcode "M_NOT_FOUND"
            :error "Route not found"})))
