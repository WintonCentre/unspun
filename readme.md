## unspun
[![Build Status](https://travis-ci.org/WintonCentre/unspun.svg?branch=master)](https://travis-ci.org/WintonCentre/unspun)

Exponent starter for a NNE/NNP app for journalists and clinicians. A web version is also needed.

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
    exp start -i
```

##### Also connect to iOS Simulator

``` shell
    exp start -a
```
##### Connecting figwheel in the REPL

When the first simulator or device starts up it will connect to figwheel, and you will see a prompt in the REPL. This indicates that the clojurescript REPL has opened on the device.

##### Troubleshooting
The emulators can be fussy about which protocol and host they use to connect to the local exp server. figwheel-bridge.js has some config related to port numbers that is relevant.

Currently:
* android emulator now needs http://aq-875.gmp26.unspun.exp.direct:80   (host: tunnel protocol:http)
* IOS needs explanation://localhost:19000 (host: localhost protocol: exp). It is occasionally happy with the android setting.

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
