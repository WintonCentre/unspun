(ns graphics.icons)

(def icon-name? #{"ios-add"
                  "ios-add-circle-outline"
                  "ios-alarm"
                  "ios-albums"
                  "ios-alert"
                  "ios-alert-outline"
                  "ios-american-football"
                  "ios-american-football-outline"
                  "ios-analytics"
                  "ios-analytics-outline"
                  "ios-aperture"
                  "ios-aperture-outline"
                  "ios-apps"
                  "ios-apps-outline"
                  "ios-appstore"
                  "ios-appstore-outline"
                  "ios-archive"
                  "ios-archive-outline"
                  "ios-arrow-back"
                  "ios-arrow-back-outline"
                  "ios-arrow-down"
                  "ios-arrow-down-outline"
                  "ios-arrow-dropdown"
                  "ios-arrow-dropdown-circle"
                  "ios-arrow-dropdown-circle-outline"
                  "ios-arrow-dropdown-outline"
                  "ios-arrow-dropleft"
                  "ios-arrow-dropleft-circle"
                  "ios-arrow-dropleft-circle-outline"
                  "ios-arrow-dropleft-outline"
                  "ios-arrow-dropright"
                  "ios-arrow-dropright-circle"
                  "ios-arrow-dropright-circle-outline"
                  "ios-arrow-dropright-outline"
                  "ios-arrow-dropup"
                  "ios-arrow-dropup-circle"
                  "ios-arrow-dropup-circle-outline"
                  "ios-arrow-dropup-outline"
                  "ios-arrow-forward"
                  "ios-arrow-forward-outline"
                  "ios-arrow-round-back"
                  "ios-arrow-round-back-outline"
                  "ios-arrow-round-down"
                  "ios-arrow-round-down-outline"
                  "ios-arrow-round-forward"
                  "ios-arrow-round-forward-outline"
                  "ios-arrow-round-up"
                  "ios-arrow-round-up-outline"
                  "ios-arrow-up"
                  "ios-arrow-up-outline"
                  "ios-at"
                  "ios-at-outline"
                  "ios-attach"
                  "ios-attach-outline"
                  "ios-backspace"
                  "ios-backspace-outline"
                  "ios-barcode"
                  "ios-barcode-outline"
                  "ios-baseball"
                  "ios-baseball-outline"
                  "ios-basket"
                  "ios-basket-outline"
                  "ios-basketball"
                  "ios-basketball-outline"
                  "ios-battery-charging"
                  "ios-battery-charging-outline"
                  "ios-battery-dead"
                  "ios-battery-dead-outline"
                  "ios-battery-full"
                  "ios-battery-full-outline"
                  "ios-beaker"
                  "ios-beaker-outline"
                  "ios-beer"
                  "ios-beer-outline"
                  "ios-bicycle"
                  "ios-bicycle-outline"
                  "ios-bluetooth"
                  "ios-bluetooth-outline"
                  "ios-boat"
                  "ios-boat-outline"
                  "ios-body"
                  "ios-body-outline"
                  "ios-bonfire"
                  "ios-bonfire-outline"
                  "ios-book"
                  "ios-book-outline"
                  "ios-bookmark"
                  "ios-bookmark-outline"
                  "ios-bookmarks"
                  "ios-bookmarks-outline"
                  "ios-bowtie"
                  "ios-bowtie-outline"
                  "ios-briefcase"
                  "ios-briefcase-outline"
                  "ios-browsers"
                  "ios-browsers-outline"
                  "ios-brush"
                  "ios-brush-outline"
                  "ios-bug"
                  "ios-bug-outline"
                  "ios-build"
                  "ios-build-outline"
                  "ios-bulb"
                  "ios-bulb-outline"
                  "ios-bus"
                  "ios-bus-outline"
                  "ios-cafe"
                  "ios-cafe-outline"
                  "ios-calculator"
                  "ios-calculator-outline"
                  "ios-calendar"
                  "ios-calendar-outline"
                  "ios-call"
                  "ios-call-outline"
                  "ios-camera"
                  "ios-camera-outline"
                  "ios-car"
                  "ios-car-outline"
                  "ios-card-outline"
                  "ios-cart-outline"
                  "ios-cash-outline"
                  "ios-chatboxes-outline"
                  "ios-chatbubbles-outline"
                  "ios-checkbox-outline"
                  "ios-checkmark-circle"
                  "ios-checkmark-circle-outline"
                  "ios-checkmark-outline"
                  "ios-clipboard"
                  "ios-clipboard-outline"
                  "ios-clock"
                  "ios-clock-outline"
                  "ios-close"
                  "ios-close-circle"
                  "ios-close-circle-outline"
                  "ios-close-outline"
                  "ios-closed-captioning"
                  "ios-closed-captioning-outline"
                  "ios-cloud"
                  "ios-cloud-circle"
                  "ios-cloud-circle-outline"
                  "ios-cloud-done"
                  "ios-cloud-done-outline"
                  "ios-cloud-download"
                  "ios-cloud-download-outline"
                  "ios-cloud-outline"
                  "ios-cloud-upload"
                  "ios-cloud-upload-outline"
                  "ios-cloudy"
                  "ios-cloudy-night"
                  "ios-cloudy-night-outline"
                  "ios-cloudy-outline"
                  "ios-code"
                  "ios-code-download"
                  "ios-code-download-outline"
                  "ios-code-outline"
                  "ios-code-working"
                  "ios-code-working-outline"
                  "ios-cog"
                  "ios-cog-outline"
                  "ios-color-fill"
                  "ios-color-fill-outline"
                  "ios-color-filter"
                  "ios-color-filter-outline"
                  "ios-color-palette"
                  "ios-color-palette-outline"
                  "ios-color-wand"
                  "ios-color-wand-outline"
                  "ios-compass"
                  "ios-compass-outline"
                  "ios-construct"
                  "ios-construct-outline"
                  "ios-contact"
                  "ios-contact-outline"
                  "ios-contacts"
                  "ios-contacts-outline"
                  "ios-contract"
                  "ios-contract-outline"
                  "ios-contrast"
                  "ios-contrast-outline"
                  "ios-copy"
                  "ios-copy-outline"
                  "ios-create"
                  "ios-create-outline"
                  "ios-crop"
                  "ios-crop-outline"
                  "ios-cube"
                  "ios-cube-outline"
                  "ios-cut"
                  "ios-cut-outline"
                  "ios-desktop"
                  "ios-desktop-outline"
                  "ios-disc"
                  "ios-disc-outline"
                  "ios-document"
                  "ios-document-outline"
                  "ios-done-all"
                  "ios-done-all-outline"
                  "ios-download"
                  "ios-download-outline"
                  "ios-easel"
                  "ios-easel-outline"
                  "ios-egg"
                  "ios-egg-outline"
                  "ios-exit"
                  "ios-exit-outline"
                  "ios-expand"
                  "ios-expand-outline"
                  "ios-eye"
                  "ios-eye-off"
                  "ios-eye-off-outline"
                  "ios-eye-outline"
                  "ios-fastforward"
                  "ios-fastforward-outline"
                  "ios-female"
                  "ios-female-outline"
                  "ios-filing"
                  "ios-filing-outline"
                  "ios-film"
                  "ios-film-outline"
                  "ios-finger-print"
                  "ios-finger-print-outline"
                  "ios-flag"
                  "ios-flag-outline"
                  "ios-flame"
                  "ios-flame-outline"
                  "ios-flash"
                  "ios-flash-outline"
                  "ios-flask"
                  "ios-flask-outline"
                  "ios-flower"
                  "ios-flower-outline"
                  "ios-folder"
                  "ios-folder-open"
                  "ios-folder-open-outline"
                  "ios-folder-outline"
                  "ios-football"
                  "ios-football-outline"
                  "ios-funnel"
                  "ios-funnel-outline"
                  "ios-game-controller-a"
                  "ios-game-controller-a-outline"
                  "ios-game-controller-b"
                  "ios-game-controller-b-outline"
                  "ios-git-branch"
                  "ios-git-branch-outline"
                  "ios-git-commit"
                  "ios-git-commit-outline"
                  "ios-git-compare"
                  "ios-git-compare-outline"
                  "ios-git-merge"
                  "ios-git-merge-outline"
                  "ios-git-network"
                  "ios-git-network-outline"
                  "ios-git-pull-request"
                  "ios-git-pull-request-outline"
                  "ios-glasses"
                  "ios-glasses-outline"
                  "ios-globe"
                  "ios-globe-outline"
                  "ios-grid"
                  "ios-grid-outline"
                  "ios-hammer"
                  "ios-hammer-outline"
                  "ios-hand"
                  "ios-hand-outline"
                  "ios-happy"
                  "ios-happy-outline"
                  "ios-headset"
                  "ios-headset-outline"
                  "ios-heart"
                  "ios-heart-outline"
                  "ios-help"
                  "ios-help-buoy"
                  "ios-help-buoy-outline"
                  "ios-help-circle"
                  "ios-help-circle-outline"
                  "ios-help-outline"
                  "ios-home"
                  "ios-home-outline"
                  "ios-ice-cream"
                  "ios-ice-cream-outline"
                  "ios-image"
                  "ios-image-outline"
                  "ios-images"
                  "ios-images-outline"
                  "ios-infinite"
                  "ios-infinite-outline"
                  "ios-information"
                  "ios-information-circle"
                  "ios-information-circle-outline"
                  "ios-information-outline"
                  "ios-ionic"
                  "ios-ionic-outline"
                  "ios-ionitron"
                  "ios-ionitron-outline"
                  "ios-jet"
                  "ios-jet-outline"
                  "ios-key"
                  "ios-key-outline"
                  "ios-keypad"
                  "ios-keypad-outline"
                  "ios-laptop"
                  "ios-laptop-outline"
                  "ios-leaf"
                  "ios-leaf-outline"
                  "ios-link"
                  "ios-link-outline"
                  "ios-list"
                  "ios-list-box"
                  "ios-list-box-outline"
                  "ios-list-outline"
                  "ios-locate"
                  "ios-locate-outline"
                  "ios-lock"
                  "ios-lock-outline"
                  "ios-log-in"
                  "ios-log-in-outline"
                  "ios-log-out"
                  "ios-log-out-outline"
                  "ios-magnet"
                  "ios-magnet-outline"
                  "ios-mail"
                  "ios-mail-open"
                  "ios-mail-open-outline"
                  "ios-mail-outline"
                  "ios-male"
                  "ios-male-outline"
                  "ios-man"
                  "ios-man-outline"
                  "ios-map"
                  "ios-map-outline"
                  "ios-medal"
                  "ios-medal-outline"
                  "ios-medical"
                  "ios-medical-outline"
                  "ios-medkit"
                  "ios-medkit-outline"
                  "ios-megaphone"
                  "ios-megaphone-outline"
                  "ios-menu"
                  "ios-menu-outline"
                  "ios-mic"
                  "ios-mic-off"
                  "ios-mic-off-outline"
                  "ios-mic-outline"
                  "ios-microphone"
                  "ios-microphone-outline"
                  "ios-moon"
                  "ios-moon-outline"
                  "ios-more"
                  "ios-more-outline"
                  "ios-move"
                  "ios-move-outline"
                  "ios-musical-note"
                  "ios-musical-note-outline"
                  "ios-musical-notes"
                  "ios-musical-notes-outline"
                  "ios-navigate"
                  "ios-navigate-outline"
                  "ios-no-smoking"
                  "ios-no-smoking-outline"
                  "ios-notifications"
                  "ios-notifications-off"
                  "ios-notifications-off-outline"
                  "ios-notifications-outline"
                  "ios-nuclear"
                  "ios-nuclear-outline"
                  "ios-nutrition"
                  "ios-nutrition-outline"
                  "ios-open"
                  "ios-open-outline"
                  "ios-options"
                  "ios-options-outline"
                  "ios-outlet"
                  "ios-outlet-outline"
                  "ios-paper"
                  "ios-paper-outline"
                  "ios-paper-plane"
                  "ios-paper-plane-outline"
                  "ios-partly-sunny"
                  "ios-partly-sunny-outline"
                  "ios-pause"
                  "ios-pause-outline"
                  "ios-paw"
                  "ios-paw-outline"
                  "ios-people"
                  "ios-people-outline"
                  "ios-person"
                  "ios-person-add"
                  "ios-person-add-outline"
                  "ios-person-outline"
                  "ios-phone-landscape"
                  "ios-phone-landscape-outline"
                  "ios-phone-portrait"
                  "ios-phone-portrait-outline"
                  "ios-photos"
                  "ios-photos-outline"
                  "ios-pie"
                  "ios-pie-outline"
                  "ios-pin"
                  "ios-pin-outline"
                  "ios-pint"
                  "ios-pint-outline"
                  "ios-pizza"
                  "ios-pizza-outline"
                  "ios-plane"
                  "ios-plane-outline"
                  "ios-planet"
                  "ios-planet-outline"
                  "ios-play"
                  "ios-play-outline"
                  "ios-podium"
                  "ios-podium-outline"
                  "ios-power"
                  "ios-power-outline"
                  "ios-pricetag"
                  "ios-pricetag-outline"
                  "ios-pricetags"
                  "ios-pricetags-outline"
                  "ios-print"
                  "ios-print-outline"
                  "ios-pulse"
                  "ios-pulse-outline"
                  "ios-qr-scanner"
                  "ios-qr-scanner-outline"
                  "ios-quote"
                  "ios-quote-outline"
                  "ios-radio"
                  "ios-radio-button-off"
                  "ios-radio-button-off-outline"
                  "ios-radio-button-on"
                  "ios-radio-button-on-outline"
                  "ios-radio-outline"
                  "ios-rainy"
                  "ios-rainy-outline"
                  "ios-recording"
                  "ios-recording-outline"
                  "ios-redo"
                  "ios-redo-outline"
                  "ios-refresh"
                  "ios-refresh-circle"
                  "ios-refresh-circle-outline"
                  "ios-refresh-outline"
                  "ios-remove"
                  "ios-remove-circle"
                  "ios-remove-circle-outline"
                  "ios-remove-outline"
                  "ios-reorder"
                  "ios-reorder-outline"
                  "ios-repeat"
                  "ios-repeat-outline"
                  "ios-resize"
                  "ios-resize-outline"
                  "ios-restaurant"
                  "ios-restaurant-outline"
                  "ios-return-left"
                  "ios-return-left-outline"
                  "ios-return-right"
                  "ios-return-right-outline"
                  "ios-reverse-camera"
                  "ios-reverse-camera-outline"
                  "ios-rewind"
                  "ios-rewind-outline"
                  "ios-ribbon"
                  "ios-ribbon-outline"
                  "ios-rose"
                  "ios-rose-outline"
                  "ios-sad"
                  "ios-sad-outline"
                  "ios-school"
                  "ios-school-outline"
                  "ios-search"
                  "ios-search-outline"
                  "ios-send"
                  "ios-send-outline"
                  "ios-settings"
                  "ios-settings-outline"
                  "ios-share"
                  "ios-share-alt"
                  "ios-share-alt-outline"
                  "ios-share-outline"
                  "ios-shirt"
                  "ios-shirt-outline"
                  "ios-shuffle"
                  "ios-shuffle-outline"
                  "ios-skip-backward"
                  "ios-skip-backward-outline"
                  "ios-skip-forward"
                  "ios-skip-forward-outline"
                  "ios-snow"
                  "ios-snow-outline"
                  "ios-speedometer"
                  "ios-speedometer-outline"
                  "ios-square"
                  "ios-square-outline"
                  "ios-star"
                  "ios-star-half"
                  "ios-star-half-outline"
                  "ios-star-outline"
                  "ios-stats"
                  "ios-stats-outline"
                  "ios-stopwatch"
                  "ios-stopwatch-outline"
                  "ios-subway"
                  "ios-subway-outline"
                  "ios-sunny"
                  "ios-sunny-outline"
                  "ios-swap"
                  "ios-swap-outline"
                  "ios-switch"
                  "ios-switch-outline"
                  "ios-sync"
                  "ios-sync-outline"
                  "ios-tablet-landscape"
                  "ios-tablet-landscape-outline"
                  "ios-tablet-portrait"
                  "ios-tablet-portrait-outline"
                  "ios-tennisball"
                  "ios-tennisball-outline"
                  "ios-text"
                  "ios-text-outline"
                  "ios-thermometer"
                  "ios-thermometer-outline"
                  "ios-thumbs-down"
                  "ios-thumbs-down-outline"
                  "ios-thumbs-up"
                  "ios-thumbs-up-outline"
                  "ios-thunderstorm"
                  "ios-thunderstorm-outline"
                  "ios-time"
                  "ios-time-outline"
                  "ios-timer"
                  "ios-timer-outline"
                  "ios-train"
                  "ios-train-outline"
                  "ios-transgender"
                  "ios-transgender-outline"
                  "ios-trash"
                  "ios-trash-outline"
                  "ios-trending-down"
                  "ios-trending-down-outline"
                  "ios-trending-up"
                  "ios-trending-up-outline"
                  "ios-trophy"
                  "ios-trophy-outline"
                  "ios-umbrella"
                  "ios-umbrella-outline"
                  "ios-undo"
                  "ios-undo-outline"
                  "ios-unlock"
                  "ios-unlock-outline"
                  "ios-videocam"
                  "ios-videocam-outline"
                  "ios-volume-down"
                  "ios-volume-down-outline"
                  "ios-volume-mute"
                  "ios-volume-mute-outline"
                  "ios-volume-off"
                  "ios-volume-off-outline"
                  "ios-volume-up"
                  "ios-volume-up-outline"
                  "ios-walk"
                  "ios-walk-outline"
                  "ios-warning"
                  "ios-warning-outline"
                  "ios-watch"
                  "ios-watch-outline"
                  "ios-water"
                  "ios-water-outline"
                  "ios-wifi"
                  "ios-wifi-outline"
                  "ios-wine"
                  "ios-wine-outline"
                  "ios-woman"
                  "ios-woman-outline"})