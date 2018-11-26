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

We like to do things test first so let's generate a project using the midje template - we'll call the application `monumental`.

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

Back in the REPL we `require` the dependency and parse the string transforming keys into symbols using the `true` parameter.

```
user=> (require '[cheshire.core])
nil
user=> (parse-string (slurp "../data/firstHundred.json") true)
...
```

We can now try to filter by the :REG (region) symbol in a limited data set by typing **forms** into the REPL
- the **filter** function takes a function predicate and a sequence
```
user=> (filter (fn [x] (= "Picardie" (:REG x))) '({:REG "Picardie"}))
({:REG "Picardie"})
```

And once we got that working we can filter using the whole file:
```
user=> (filter (fn [m] (= "Picardie" (:REG m))) (parse-string (slurp "../data/firstHundred.json") true))
...
```

The REPL is a good place to get rapid feedback about our code


## Testing

The tests can be run using

`> lein midje`

or left running with hot reload using

`? lein midje :autotest`

### First test

Let's move on with our solution - the first requirement is to get monuments by their region.

So we come up with a first test

```

```
