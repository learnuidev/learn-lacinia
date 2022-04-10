(ns user
  (:require [clojure.data.json :as json]
            [clojure.pprint :refer [pprint]]))


(def books (->  (slurp "resources/data/books.json")
                (json/read-str :key-fn keyword)))
(comment
 (pprint books))
