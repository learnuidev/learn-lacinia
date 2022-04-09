# Module 3: Getting Started with GraphQL and Lacinia

## 3.1 What is GraphQL?


## 3.2 What is Lacinia?

## 3.3 Initial Schema

This tutorial is a variation of the [original](https://lacinia.readthedocs.io/en/latest/tutorial/init-schema.html) tutorial

At this stage, we’re still just taking baby steps, and getting our bearings.

By the end of this stage, we’ll have a minimal schema and be able to execute our first query.

Our initial schema is just for the `Book` entity, and a single operation to retrieve a book by its id:

```clj
;; resources/schema.edn
{:objects
 {:Book {:description "Books written by George R. R. Martin"
         :fields
         {:Id {:type Int
               :description "The ID of the book"}
          :Name {:type String
                 :description "The name of the book"}
          :ISBN {:type String
                 :description "ISBN Number"}
          :NumberOfPages {:type String
                          :description "The number of pages in the book"}
          :Publisher {:type String
                      :description "The name of the publisher"}
          :MediaType {:type String
                      :description "Type of Media"}
          :Country {:type String
                    :description "Country where the book was first published"}
          :ReleaseDate {:type String
                        :description "Year when the book was released"}
          :Authors {:type (list String)
                    :description "Authors of the book"}
          :FollowedBy {:type Int
                       :description "Next Book ID"}
          :PrecededById {:type Int
                         :description "Previous Book ID"}}}}
 :queries {:book_by_id
           {:type :Book
            :args {:id {:type Int}}
            :resolve :query/book-by-id}}}

```

A Lacinia schema is an EDN file. It is a map of maps; the top level keys identify the type of definition:
 - :objects,
 - :queries,
 - :interfaces,
 - :enums, and so forth.

 The inner maps are keywords to a type-specific structure.

 This schema defines a single query, `book_by_id` that returns an object as defined by the `Book` type.

> See documentation about [Objects](https://lacinia.readthedocs.io/en/latest/objects.html), [Fields](https://lacinia.readthedocs.io/en/latest/fields.html), and [Queries](https://lacinia.readthedocs.io/en/latest/queries.html).

A schema is declarative: it defines what operations are possible, and what types and fields exist, but has nothing to say about where any of the data comes from. In fact, Lacinia has no opinion about that either! GraphQL is a contract between a consumer and a provider for how to request and present data, it’s not any form of database layer, object relational mapper, or anything similar.

Instead, Lacinia handles the parsing of a client query, and guides the execution of that query, ultimately invoking application-specific callback hooks: field resolvers. Field resolvers are the only source of actual data. Ultimately, field resolvers are simple Clojure functions, but those can’t, and shouldn’t, be expressed inside an EDN file. Instead we put a placeholder in the EDN, and then attach the actual resolver later.

The keyword `:query/book-by-id` is just such a placeholder; we’ll see how it is used shortly.

We’ve made liberal use of the :description property in the schema. These descriptions are intended for developers who will make use of your GraphQL interface. Descriptions are the equivalent of doc-strings on Clojure functions, and we’ll see them show up later when we discuss GraphiQL. It’s an excellent habit to add descriptions early, rather than try and go back and add them in later.

We’ll add more fields, more types, relationships between types, and more operations in later chapters.

We’ve also demonstrated the use of a few Lacinia conventions in our schema:

- Built-in scalar types, such as ID, String, and Int are referenced as symbols. [1]
- Schema-defined types, such as `:Book`, are referenced as keywords.
- Fields are lower-case names, and types are CamelCase.


In addition, all GraphQL names (for fields, types, and so forth) must contain only alphanumerics and the underscore. The dash character is, unfortunately, not allowed. If we tried to name the query `query-by-id`, Lacinia would throw a [clojure.spec](https://clojure.org/guides/spec) validation exception when we attempted to use the schema. [2]

In Lacinia, there are base types, such as `String` and `:Book` and wrapped types, such as `(non-null String)`. The two wrappers are `non-null` (a value must be present) and list (the type is a `list` of values, not a single value). These can even be combined!

Notice that the return type of the book_by_id query is `:Book` and not `(non-null :Book)`. This is because we can’t guarantee that a game can be resolved, if the id provided in the client query is not valid. If the client provides an invalid id, then the result will be nil, and that’s not considered an error.

In any case, this single `Book` entity is a good starting point.

## 3.4 schema namespace

With the schema defined, the next step is to write code to load the schema into memory, and make it operational for queries:

```clj
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
```

This code loads the schema EDN file, attaches field resolvers to the schema, then compiles the schema. The compilation step is necessary before it is possible to execute queries. Compilation reorganizes the schema, computes various defaults, perform verifications, and does a number of other necessary steps.

We’re using a namespaced keyword for the resolver in the schema, and in the resolver-map function; this is a good habit to get into early, before your schema gets very large.

The field resolver in this case is a placeholder; it ignores all the arguments passed to it, and simply returns nil. Like all field resolver functions, it accepts three arguments: a context map, a map of field arguments, and a container value. We’ll discuss what these are and how to use them shortly.

## 3.5 `user` namespace

A key advantage of Clojure is REPL-oriented [3] development: we want to be able to run our code through its paces almost as soon as we’ve written it - and when we change code, we want to be able to try out the changed code instantly.

Clojure, by design, is almost uniquely good at this interactive style of development. Features of Clojure exist just to support REPL-oriented development, and its one of the ways in which using Clojure will vastly improve your productivity!

We can add a bit of scaffolding to the user namespace, specific to our needs in this project. When you launch a REPL, it always starts in this namespace.

We can define the user namespace in the `dev` folder; this ensures that it is not included with the rest of our application when we eventually package and deploy the application.

```clj
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

```
