# Module 5: Adding Houses and Characters

So far, we’ve been working with just a single entity type, Book.

Let’s see what we can do when we add the House and Character entity type to the mix.

Initially, we’ll define each House in terms of an id, a name, and an optional founder and we will define each Character in terms of an id, name and an optional list of allegiances


If this was a relational database, we’d likely have a join table between Character and House, but that can come later. For now, we have a set of house ids inside each Character.

## 5.1 Schema Changes

```clj
{:objects
  ...
 {:House {:description "Houses that exist in Game of thrones"
          :fields
          {:Id {:type Int
                :description "The ID of the House"}
           :Name {:type String
                  :description "The name of the House"}}}
  :Character {:description "Characters from Game of thrones"
              :fields
              {:Id {:type Int
                    :description "The ID of the Character"}
               :Name {:type String
                      :description "The name of the Character"}
               :allegiances {:type (list :House)
                             :description "Allegiance Houses"
                             :resolve :Character/Allegiances}}}}
               :books {:type (list :Books)
                             :description "Allegiance Houses"
                             :resolve :Character/Books}}}}
 :queries {:character_by_id
           {:type :Character
            :args {:id {:type Int}}
            :resolve :query/character-by-id}}}

```

We've added two new type - Character and House

In Lacinia, we use a wrapper, `list`, around a type, to denote a list of that type. In the EDN, the `list` wrapper is applied using the syntax of a function call in Clojure code

Here we’ve defined the `:allegiances` field as `(list :House)`, which means the world won’t end if the result map contains a nil instead of an empty list

We need a field resolver for the `:allegiances` field, to convert from what’s in our data (a set of house ids) into what we are promising in the schema: a list of House objects.

Likewise, we need another field resolver in the `Character` entity to figure out which `Books` are associated with the `character`.

## 5.2 Code Changes

```clj
;; schema.clj
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


```

## 5.3 Testing it out

After reloading code in the REPL, we can exercise these new types and relationships:

```
TODO: Example video
```

For the first time, we’re seeing the “graph” in GraphQL.

## 5.4 Summary

Lacinia provides the mechanism to create relationships between entities, such as between BoardGame and Designer. It still falls on the field resolvers to provide that data for such linkages.

With that in place, the same com.walmartlabs.lacinia/execute function that gives us data about a single entity can traverse the graph and return data from a variety of entities, organized however you need it.
