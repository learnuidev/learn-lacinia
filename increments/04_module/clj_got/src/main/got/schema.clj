(ns got.schema
  (:require [clojure.data.json :as json]
            [clojure.edn :as edn]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia :refer [execute]]
            [clojure.pprint :refer [pprint]]))

;; == Helper functions ==
(defn index-by [coll key-fn]
  (into {} (map (juxt key-fn identity) coll)))


;; == Books Data ==
(def books (->  (slurp "resources/data/books.json")
                (json/read-str :key-fn keyword)
                (index-by :Id)))
(comment
  (pprint books)
  (pprint (first books))
  (count (first books)))


;; == resolvers ==
(defn get-book [context {:keys [id]} value]
  (get books id))

(defn resolver-map []
  {:query/book-by-id get-book})

;; == schema ==
(defn load-schema
  []
  (-> "resources/schema.edn"
      slurp
      edn/read-string
      (attach-resolvers (resolver-map))
      schema/compile))

(comment
  (load-schema))
