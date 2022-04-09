# Getting Started with Clojure and GraphQL:

Author: Vishal Gautam
Date: 9th April 2022

*Learn how to Create GraphQL Server using Clojure and Lacinia*

In this tutorial I will show you how to create a GraphQL server in Clojure and Lacinia.

Modules

- **Module 1: Create `deps.edn` project**
  - 1.1: Configure deps.edn
  - 1.2: Add source directories
  - 1.3: `user.clj` and `core.clj`
  - 1.4: 🔥 up the nREPL

- **Module 2: Project Introduction: Game Of Thrones DataSet**
  - 2.1: Import Data
  - 2.2: Understanding Data Part 1 - `books.json`


## Module 1: Create deps.edn project

- In this module you will learn how to create a clojure deps.edn project
- You will learn the basics of deps.edn, REPL Driven Development
- By the end of this module you will be ready to start coding

### 1.1 Configure deps.edn

```
> mkdir clj_got && cd clj_got
> touch deps.edn
```


```clj
;; deps.edn
{:paths ["src/main"]
 :deps {org.clojure/clojure {:mvn/version "1.10.3"}
        com.walmartlabs/lacinia {:mvn/version "1.2-alpha-1"}
        org.clojure/data.json {:mvn/version "2.4.0"}}

 :aliases
  {:dev {:extra-paths ["src/dev"]}
   ;; Allow the app to accept external REPL clients via a local connection to port 7777.
   :repl {:jvm-opts ["-Dclojure.server.repl={:port 7777 :accept clojure.core.server/repl}"]}}}

```

### 1.2 Add source directories

1. We will add our clojure code inside main directory
2. We have three dependencies
3. We have two aliases
- dev
- repl - which starts repl in port 7777


Next lets create `src`, `main` and `dev` directories

```
> mkdir src && cd src
> mkdir main dev

```

### 1.3 Add `user.clj` and `core.clj`

Create `user.clj` under `dev`
```clj
(ns user)
```

Create `got/core.clj` root namespace under `main`
```
> cd main
> mkdir got && cd got
> touch core.clj

```

### 1.4 🔥 up the REPL

Finally lets fire up the REPL

```
> clj -A:dev:repl
```


Now we are ready to import data

## Module 2: Project Introduction: Game Of Thrones DataSet

In this module, you will import game of thrones data from my old node project into this new clojure project. Data for the project is found [here](https://github.com/vishalgautamm/graphQLofFireAndIce/tree/master/src/data)


### 2.1: Import Data

Go to this [link](https://github.com/vishalgautamm/graphQLofFireAndIce/tree/master/src/data) and download `books.json`, `characters.json` and `houses.json` json files and save it inside `resources/data` directory.

```
> mkdir resources/data

```

Now that we have downloaded all the data, it's time to funny understand it.


### 2.2 Understanding Data: `books.json`

Lets start by importing our first dependency - `clojure.data.json`

```clj
(ns user
  (:require [clojure.data.json :as json]))
```

We will use `read-str` function to read the books


```clj
(ns user
  (:require [clojure.data.json :as json]))

;;
;; 1.1 books
(def books (json/read-str (slurp "resources/data/books.json")))

```

Currently the keys are in string, we can use `:key-fn` property to convert into keyword

```clj
(ns user
  (:require [clojure.data.json :as json]))

;;
;; 1.1 books
(def books (->  (slurp "resources/data/books.json")
                (json/read-str :key-fn keyword)))

```
