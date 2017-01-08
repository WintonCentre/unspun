(ns themes.palettes)

;;;
; See https://www.materialpalette.com/blue/orange
;;;
(def palettes
  {:blue-orange
   {:dark-primary   "#1875D1"
    :primary        "#2095F2"
    :light-primary  "#BBDEFB"
    :text-icons     "#FEFEFE"
    :accent         "#FE9700"
    :primary-text   "#212121"
    :secondary-text "#757575"
    :divider        "#BDBDBD"
    :error          "#FF6688"}

   :cyan-deep-orange
   {:dark-primary   "#0097A7"
    :primary        "#00BCD4"
    :light-primary  "#B2EBF2"
    :text-icons     "#FEFEFE"
    :accent         "#FF5722"
    :primary-text   "#212121"
    :secondary-text "#757575"
    :divider        "#BDBDBD"
    :error          "#FF6688"}

   :light-green-teal
   {:dark-primary   "#689F38"
    :primary        "#8BC34A"
    :light-primary  "#DCEDC8"
    :text-icons     "#FEFEFE"
    :accent         "#009688"
    :primary-text   "#212121"
    :secondary-text "#757575"
    :divider        "#BDBDBD"
    :error          "#FF6688"}

   :deep-purple-pink
   {:dark-primary   "#512DA8"
    :primary        "#673AB7"
    :light-primary  "#D1C4E9"
    :text-icons     "#FEFEFE"
    :accent         "#FF4081"
    :primary-text   "#212121"
    :secondary-text "#757575"
    :divider        "#BDBDBD"
    :error          "#FF6688"}

   :grey-brown
   {:dark-primary   "#616161"
    :primary        "#9E9E9E"
    :light-primary  "#F5F5F5"
    :text-icons     "#FEFEFE"
    :accent         "#795548"
    :primary-text   "#212121"
    :secondary-text "#757575"
    :divider        "#BDBDBD"
    :error          "#FF6688"}})

(def header-background "#616161"
  )
(def header-foreground "#FEFEFE"
  )


(def palette-keys (vec (keys palettes)))
(def palette-vals (mapv val palettes))

(defn- next-palette-index [n]
  (mod (inc n) (count (palette-keys))))

(defn get-palette [n]
  "get current palette"
  (palette-vals n))
