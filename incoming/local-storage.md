Local-Storage
=============


The tricky thing is what happens on startup.

We have two key pieces of state.

1. use-cache:
   - if TRUE, changes to the app-state cause the app-cache to be written. On FALSE -> TRUE transition, app-cache is saved
   - if FALSE the app-cache is ignored. On TRUE -> FALSE transition it is cleared.
1. app-cache:
   - A snapshot of the app-state if use-cache is TRUE, or empty if it is FALSE.

On startup:

1. Read use-cache
1. if use-cache TRUE, read app-cache, then initialise app-state to (dissoc app-cache :use-cache)
1. If use-cache FALSE, read default initial-state into app-state.
1. watch use-cache
     - on TRUE->FALSE, clear app-cache, remove app-state watch
     - on FALSE->TRUE, write app-cache,
         - watch app-state
             - on app-state change, write app-cache


