# Practical Clo*j*ure

## Setup your development environment

- You need Java 8 or above installed and `JAVA_HOME` set.

- Install Leiningen: https://leiningen.org/#install
  - And the **lein-midje** plugin (for more expressive tests)
  > `echo '{:user {:plugins [[lein-midje "3.2.1"]]}}' > ~/.lein/profiles.clj`

### Editor/IDE
- For Visual Studio Code users I recommend installing the [Calva plugin](https://marketplace.visualstudio.com/items?itemName=cospaia.clojure4vscode) - setup instructions [here](https://github.com/BetterThanTomorrow/calva/wiki/Getting-Started#dependencies)

- If you use IntelliJ I recommend installing the [Cursive plugin](https://plugins.jetbrains.com/plugin/8090-cursive) - and there is a free licence for private usage.


## The application

Let's say we've been asked to build a REST API that exposes information related to French monuments.

This is going to involve working with large amounts of data so we reach for Clojure which is data oriented by nature.

A sample of the data can be found in the `data` directory.

## Generate a project

We like to do things test first so let's generate a project using the midje template - we call the application `monumental`.

`> lein new midje monumental`

and have a look at the project structure:

```
> cd monumental
> tree
.
├── README.md
├── project.clj
├── src
│   └── monumental
│       └── core.clj
└── test
    └── monumental
        └── core_test.clj
```

## Explore the Data

So we've been given a sample file. Let's start a REPL and explore the data

`> lein repl`

The clojure core library provides a large number of useful functions.

We can easily read a file into a string

`user=> (slurp "../data/firstHundred.json")`

But it would be more useful to have the string parsed into EDN - the Extensible Data Notation used by Clojure

For this we need to add **cheshire** to our `project.clj` - and we'll update to clojure 1.9.0 while we're at it!

**project.clj**
```
(defproject monumental "0.0.1-SNAPSHOT"
  :description "Cool new project to do things and stuff"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [cheshire "5.8.1"]]
  :profiles {:dev {:dependencies [[midje "1.9.0"]]}})

```

We start the REPL back up and `require` the dependency and parse the string transforming keys into symbols using the `true` parameter.

```
user=> (require '[cheshire.core :refer :all])
nil
user=> (parse-string (slurp "../data/firstHundred.json") true)
...
```

We can now try to filter by the :REG (region) symbol in a limited data set by typing **forms** into the REPL
- the **filter** function takes a function predicate and a sequence
```
user=> (filter (fn [r] (= "Picardie" (:REG r))) '({:REG "Picardie"}))
({:REG "Picardie"})
```

And once we got that working we can filter using the whole file:
```
user=> (filter (fn [m] (= "Picardie" (:REG m))) (parse-string (slurp "../data/firstHundred.json") true))
...
```

The REPL is a good place to experiment and try out things quickly.


## Testing

The tests can be run using

`> lein midje`

or left running with hot reload using

`> lein midje :autotest`

You'll notice that the tests in the generated project fail.

They intentionally do so to allow verifying the tests work. Go ahead and make them pass.

### First test

Let's move on with our solution - the first requirement is to get monuments by their region.

We will assume that we have our sequence of monuments and come up with a first test:


`monumental/test/monumental/core_test.clj`
```
(ns monumental.core-test
  (:require [midje.sweet :refer :all]
            [monumental.core :refer :all]))

(facts "about `monuments-by-region`"
  (fact "it should return monuments for matching regions"
    (let [monuments '({:REG "Picardie"})
          region "Picardie"]
    (monuments-by-region monuments region) => monuments)))
```

And we make the test pass by implementing the `monuments-by-region` function:


`monumental/src/monumental/core.clj`
```
(ns monumental.core)

(defn monuments-by-region [monuments region]
  (filter (fn [m] (= region (:REG m))) monuments))
```

## Exposing the data over the wire

To make use of this function we'll move on to creating the REST API endpoint

For this we will use **compojure** which is a routing library for **ring** taking care of some boilerplate associated with http request and response handling

Let's add it to our dependencies:

`project.clj`
```
(defproject monumental "0.0.1-SNAPSHOT"
  :description "Cool new project to do things and stuff"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [cheshire "5.8.1"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]]
  :plugins [[lein-ring "0.12.4"]]
  :ring {:handler monumental.handler/app}
  :profiles {:dev {:dependencies [[midje "1.9.4"]
                                  [javax.servlet/servlet-api "2.5"]
                                  [ring/ring-mock "0.3.2"]]}})
```
Apart from the obvious inclusion of **compojure** we see:
- **ring/ring-defaults** gives us sensible defaults
- **lein-ring** plugin let us start and stop servers
- **ring-mock** will be used to test our endpoints

Finally we added the `:ring` symbol to configure the handler.

Next we create that handler.

`monumental/src/monumental/handler.clj`
```
(ns monumental.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
```


With this in place we can now start up a server to verify that our handler is working correctly:

`lein ring server`

This opens up the root uri (http://localhost:3000/) in your default browser. And you should see "Hello World" printed.

Alternatively you can start a server without opening a browser.

`lein ring server-headless`

## Putting it all together

With the http server in place we can define our monument search endpoint `/api/search`

`monumental/src/monumental/handler.clj`
```
(ns monumental.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]
            [monumental.core :refer :all]]))

(def monuments '({:REG "Picardie"}))

(defroutes app-routes
  (GET "/api/search" [region] (monuments-by-region monuments region))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
```

We limit the monuments to 1 monument for now in order to test this end to end.

The `api/search` endpoint takes a `region` query parameter which we pass to our `monuments-by-region` function.

We restart the server to test the endpoint:
`http://localhost:3000/api/search?region=Picardie`

and we discover that the endpoint returns EDN and not JSON which we'd typically expect from our REST API.

A middleware will help us transform the EDN into JSON.

Add the necessary dependency (ring-json)
`project.clj`
```
(defproject monumental "0.0.1-SNAPSHOT"
  :description "Cool new project to do things and stuff"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [cheshire "5.8.1"]
                 [compojure "1.6.1"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.3.2"]]
  :plugins [[lein-ring "0.12.4"]]
  :ring {:handler monumental.handler/app}
  :profiles {:dev {:dependencies [[midje "1.9.4"]
                                  [javax.servlet/servlet-api "2.5"]
                                  [ring/ring-mock "0.3.2"]]}})
```

`require` the json middleware and response utility we need. The wrap the search endpoint in a `response`
`monumental/src/monumental/handler.clj`
```
(ns monumental.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            [monumental.core :refer :all]))

(def monuments '({:REG "Picardie"}))

(defroutes app-routes
  (GET "/api/search" [region] (response (monuments-by-region monuments region)))
  (route/not-found "Not Found"))

(def app (->
           app-routes
           (wrap-json-response)
           (wrap-defaults site-defaults)))
```

The Thread macro `->` used here allow us to chain our function calls in an imperative left to right style.


## Evolution of search

It would probably be more useful to search for regions starting with a sequence of letters

Let's write another test to enhance our function.

```
  (fact "it should return monuments matching part of region name"
    (let [monuments '({:REG "Picardie"})
          region "P"]
    (monuments-by-region monuments region) => monuments)))
```

We can make our failing test pass by modifying the function predicate
```
(ns monumental.core
  (:require [clojure.string :as str]))

(defn monuments-by-region [monuments region]
  (filter (fn [m] (str/starts-with? (:REG m) region)) monuments))
```


Finally we can test the endpoint using the real monument file

But first we'll put the file in a resources directory (Java developers will feel right at home with this one)

```
mkdir -p resources
cp ../data/firstHundred.json resources
```

And then reading this file into the `monuments` variable **def**ined **once** at startup

```
(ns monumental.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            [cheshire.core :refer [parse-string]]
            [monumental.core :refer :all]))

(defonce monuments (parse-string (slurp (io/resource "firstHundred.json")) true))

(defroutes app-routes
  (GET "/api/search" [region] (response (monuments-by-region monuments region)))
  (route/not-found "Not Found"))

(def app (->
           app-routes
           (wrap-json-response)
           (wrap-defaults site-defaults)))
```


## Distributing our code

We are ready to ship the code and we do so by generating an **uberjar** using the lein-ring plugin:

`lein ring uberjar`

You then run the application like any old executable jar:
`java -jar <path to generated jar>`


## Conclusion


