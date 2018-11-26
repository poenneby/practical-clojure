# Practical Clo*j*ure

## Setup your development environment

- You need Java 8 or above installed and `JAVA_HOME` set.

- Install Leiningen: https://leiningen.org/#install
  - And the **lein-midje** plugin `echo '{:user {:plugins [[lein-midje "3.2.1"]]}}' > ~/.lein/profiles.clj`

### Editor/IDE
- For Visual Studio Code users I recommend installing the [Calva plugin](https://marketplace.visualstudio.com/items?itemName=cospaia.clojure4vscode) - setup instructions [here](https://github.com/BetterThanTomorrow/calva/wiki/Getting-Started#dependencies)

- If you use IntelliJ I recommend installing the [Cursive plugin](https://plugins.jetbrains.com/plugin/8090-cursive) - and there is a free licence for private usage.


## The application

Let's say we've been asked to build a REST API that exposes information related to French monuments.

This is going to involve working with large amounts of data so we reach for Clojure which is data oriented by nature.

A sample of the data can be found in the `data` directory.

## Generate a project

We like to do things test first so let's generate a project using the midje template - we'll call the application `monumental`.

`lein new midje monumental`


```
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


