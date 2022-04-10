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


;; == Houses Data ==
(def houses (->  (slurp "resources/data/houses.json")
                 (json/read-str :key-fn keyword)
                 (index-by :Id)))
(comment
  (pprint houses)
  (pprint (first houses))
  (count (first houses)))

;; == Houses Data ==
(def characters (->  (slurp "resources/data/characters.json")
                     (json/read-str :key-fn keyword)
                     (index-by :Id)))
(comment
  (pprint characters)
  (pprint (first characters))
  (count (first characters)))


;; == resolvers ==
(defn get-book [context {:keys [id]} value]
  (get books id))

(defn get-character [context {:keys [id]} value]
  (get characters id))

(defn get-house [id]
   (get houses id))

(defn get-allegiances [context args {:keys [Allegiances]}]
  (map get-house Allegiances))


(defn get-books [context args {:keys [Books]}]
  (map #(get books %) Books))

(defn resolver-map []
  {:query/book-by-id get-book
   :query/character-by-id get-character
   :Character/Allegiances get-allegiances
   :Character/Books get-books})

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
