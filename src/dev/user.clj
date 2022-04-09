(ns user
  (:require [clojure.data.json :as json]
            [clojure.edn :as edn]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia :refer [execute]]
            [com.walmartlabs.lacinia.schema :as schema]))

;; Helper functions
(defn index-by [coll key-fn]
  (into {} (map (juxt key-fn identity) coll)))

;; Tutorial: Getting Started with GraphQL and Clojure
;; A game of thrones example

;; - Step 1: Understand the data - DONE
;; - Step 2: Install Lacinia + Restart REPL - DONE
;; - Step 3: Create basic schema: Book - DONE

;;
;; Step 1: Understand the data
;; 1.1 books
(def books (->  (slurp "resources/data/books.json")
                (json/read-str :key-fn keyword)
                (index-by :Id)))

;; 1.2 characters
(def characters (count (json/read-str (slurp "resources/data/characters.json")
                                      :key-fn keyword)))

;; 1.3 houses
(def houses (count (json/read-str (slurp "resources/data/houses.json")
                                  :key-fn keyword)))

;; - Step 2: Install Lacinia + Restart REPL - DONE
;; - Step 3: Create basic schema: Book - DONE


;; - Step 4: Create resolvers and schema
(comment
  (schema/compile (attach-resolvers (edn/read-string (slurp "resources/schema.edn"))
                                    {})))
(comment
  (clojure.repl/doc attach-resolvers)
  (clojure.repl/doc schema/compile))

()
(defn get-book [context {:keys [id]} value]
  (get books id))


;; resolvers
(def resolvers {:get-book get-book})


(def got-schema
  (-> (slurp "resources/schema.edn")
      edn/read-string
      (attach-resolvers resolvers)
      schema/compile))


;; Step 5: Test the resolver
(comment
  (clojure.repl/doc execute)
  (execute got-schema
           "{ book_by_id(id: 11) { Id Name }}"
           nil
           nil))


;; Step 6: Make get-book more dynamic
