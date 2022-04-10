(ns user
  (:require [clojure.data.json :as json]
            [clojure.pprint :refer [pprint]]
            [clojure.edn :as edn]
            [com.walmartlabs.lacinia :refer [execute]]
            [got.schema :refer [load-schema]]))

;; ==== Tutorial Start
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
                #_(index-by :Id)))
(comment
  (pprint books)
  (pprint (first books))
  (count (first books)))
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

;; ============ Tutorial END
(comment
  (load-schema))

(def got-schema (load-schema))


(defn q
  [query-string]
  (execute got-schema query-string nil nil))

;; Test the resolver
(comment
  (clojure.repl/doc execute)
  (q "{ book_by_id(id: 11) { Id Name }}"))
