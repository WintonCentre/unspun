## unspun

Exponent starter for a NNE/NNP add for journalists. Needs generalising to web.

### Usage (for developers)

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
When the figwheel clojure REPL starts, type `(start figwheel)`. This will attempt to connect to the device or simulator under test.

#### Start Exponent server (Using `exp`)

##### Also connect to Android device

``` shell
    exp ios
```

##### Also connect to iOS Simulator

``` shell
    exp android
```
##### Connecting figwheel in the REPL

When the first simulator or device starts up it will connect to figwheel, and you will see a prompt in the REPL. This indicates that the clojurescript REPL has opened on the device.

##### Troubleshooting
Exponent and React-Native create a couple of servers to handle app serving, uploading, and packaging. It's quite easy to get these into a broken state. Easiest to reset all servers with:
```shell
   exp stop --all
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
Make sure XDE does not have developer mode ticked as this inserts extra diagnostics which will slow the app down.
Then build the app with:
``` shell
lein prod-build
```
### Testing the production build
After a production build, run `exp start` and `exp ios` and/or `exp android`. Alternatively enter the `exp start` URL in a device running exponent. Make sure that the XDE settings are set to use tunneling. This will allow the host development system to serve the app through an ssh tunnel using a public URL rather than `localhost`. [`ngrok`](https://ngrok.com) handles all this.

### Publishing
Press the publish button!

### Development notes
* DO NOT USE `:optimizations :whitespace` in project.clj as this does not make sense when running in Node, and errors will result.
