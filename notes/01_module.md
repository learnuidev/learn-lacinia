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
