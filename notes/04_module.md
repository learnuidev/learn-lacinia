# Module 4: Ring, Reitit Server + Voyager + Playground

## 4.1: Basic GraphQL ring server

### 4.1.1 Adding Dependencies

Lets add ring and reitit library to our dependency list:
- `ring` is a clojure web application library
- `reitit` is a routing library

```clj
;; deps.edn
{:paths ["src/main"]
 :deps {org.clojure/clojure {:mvn/version "1.10.3"}
        com.walmartlabs/lacinia {:mvn/version "1.2-alpha-1"}
        org.clojure/data.json {:mvn/version "2.4.0"}
        ;;
        metosin/reitit {:mvn/version "0.5.15"}
        ring/ring {:mvn/version "1.9.4"}}

 :aliases
  {:dev {:extra-paths ["src/dev"]}
   ;; Allow the app to accept external REPL clients via a local connection to port 7777.
   :repl {:jvm-opts ["-Dclojure.server.repl={:port 7777 :accept clojure.core.server/repl}"]}}}

```

### got.server namespace - Setting up the server

```clj
(ns got.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.request :as request]
            [reitit.ring :as ring]
            [com.walmartlabs.lacinia :as lacinia]
            [clojure.data.json :as json]
            [ring.middleware.resource :as resource]
            [ring.middleware.content-type :as content-type]
            [ring.middleware.not-modified :as not-modified]
            [got.schema :as schema]))


(def got-schema (schema/load-schema))
;;
(defn graphql-handler [request]
  (let [grapql-request (json/read-str (request/body-string request) :key-fn keyword)
        {:keys [query variables]} grapql-request
        result (lacinia/execute got-schema query variables nil)]
    {:status  200
     :body    (json/write-str result)
     :headers {"Content-Type" "application/json"}}))

;;
(def app
  (-> (ring/ring-handler (ring/router ["/graphql" {:post graphql-handler}]))
      (resource/wrap-resource "static")
      content-type/wrap-content-type
      not-modified/wrap-not-modified))

;;
(defonce server (atom nil))

(defn start-server []
  (reset! server (jetty/run-jetty app {:join? false
                                       :port  8080})))

(defn stop-server []
  (when @server
    (.stop @server)))

(comment
  (start-server)
  (stop-server))
```

The `graphql-handler` function does the following:

- It de-serializes the GraphQL request body from the Ring request.
- Then using the `got-schema` created in the previous step, it executes this GraphQL request by invoking the Lacinia's execute function.
- Finally, it serializes the `result` of the `execute` function and send it back to the client as a Ring response.

## 4.2: GraphQL Playground and Voyager

With the GraphQL endpoint up and running, the next step is introspecting the GraphQL schema and try out some more queries.

To introspect, we are going to make use of [Voyager](https://github.com/APIs-guru/graphql-voyager), a tool to visualize GraphQL API as an interactive graph.

To add Voyager, download this [voyager.html](https://github.com/graphqlize/graphqlize-demo/blob/master/clojure/ring/resources/static/voyager.html) file and put it under the resources/static directory.

When you restart the server, the Voyager will be available at http://localhost:8080/voyager.html.

Then to interact with the GraphQL API, let's add the GraphQL Playground. Like Voyager, download this [playground.html](https://github.com/graphqlize/graphqlize-demo/blob/master/clojure/ring/resources/static/playground.html) file and put in the static directory.

This GraphQL playground will be available at http://localhost:8080/playground.html after server restart.
