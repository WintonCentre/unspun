(ns shared.gesture-responders)


;;;
;; The pan responder mixin is a Rum mixin based on https://facebook.github.io/react-native/docs/panresponder.html.
;; The responder stat is stored in local component Rum state at `key`.
;;;
def pan-responder [key]
{:will-mount (fn [state]
               state)
 }