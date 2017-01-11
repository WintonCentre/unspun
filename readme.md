## unspun

Exponent starter for a NNE/NNP add for journalists. Needs generalising to web.

### Usage

#### Install Exponent [XDE and mobile client](https://docs.getexponent.com/versions/v11.0.0/introduction/installation.html)
    If you don't want to use XDE (not IDE, it stands for Exponent Development Tools), you can use [exp CLI](https://docs.getexponent.com/versions/v11.0.0/guides/exp-cli.html).

``` shell
    yarn install -g exp
```

#### Install [Lein](http://leiningen.org/#install)

#### Install npm modules

``` shell
    yarn install
```

#### Signup using exp CLI

``` shell
    exp signup
```

#### Start the figwheel server
``` shell
    lein figwheel
```

#### Start Exponent server (Using `exp`)

##### Also connect to Android device

``` shell
    exp start -a --lan
```

##### Also connect to iOS Simulator

``` shell
    exp start -i --lan
```

### Add new assets or external modules
1. `require` module:

``` clj
    (def cljs-logo (js/require "./assets/images/cljs.png"))
    (def FontAwesome (js/require "@exponent/vector-icons/FontAwesome"))
```
2. Reload simulator or device

### Make sure you disable live reload from the Developer Menu, also turn off Hot Module Reload.
Since Figwheel already does those.

### Production build (generates js/externs.js and main.js)

``` shell
lein prod-build
```

### Development notes
* DO NOT USE `:optimizations :whitespace` in project.clj as this does not make sense when running in Node, and errors will result.