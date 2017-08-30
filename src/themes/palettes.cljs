(ns themes.palettes)

;;;
; See https://www.materialpalette.com/blue/orange
;;;
(def palettes {

               :dark-blue-grey-pink                         ;;WebAIM.org contrast checked
               {:dark-primary   "#263238"
                :primary        "#37474f"
                :light-primary  "#CFD8DC"
                :text-icons     "#FFFFFF"
                :accent         "#FF4081"
                :light-accent   "#F48fb1"
                :primary-text   "#212121"
                :secondary-text "#757575"
                :divider        "#BDBDBD"
                :error          "#FF6688"}

               :dark-blue-grey-orange                       ;;WebAIM.org contrast checked
               {:dark-primary   "#263238"
                :primary        "#37474f"
                :light-primary  "#CFD8DC"
                :text-icons     "#FFFFFF"
                :accent         "#FF9800"
                :light-accent   "#FFD180"
                :primary-text   "#212121"
                :secondary-text "#757575"
                :divider        "#BDBDBD"
                :error          "#FF6688"}

               :blue-grey-teal
               {:dark-primary   "#263238"
                :primary        "#37474f"
                :light-primary  "#CFD8DC"
                :text-icons     "#FFFFFF"
                :accent         "#26a69a"
                :light-accent   "#80cbc4"
                :primary-text   "#212121"
                :secondary-text "#757575"
                :divider        "#BDBDBD"
                :error          "#FF6688"}

               :blue-orange
               {:dark-primary   "#1875D1"
                :primary        "#2095F2"
                :light-primary  "#BBDEFB"
                :text-icons     "#FEFEFE"
                :accent         "#FF9800"
                :light-accent   "#ffcc80"
                :primary-text   "#212121"
                :secondary-text "#757575"
                :divider        "#BDBDBD"
                :error          "#FF6688"}

               :teal-orange
               {:dark-primary   "#00838f"
                :primary        "#004d40"
                :light-primary  "#B2EBF2"
                :text-icons     "#FEFEFE"
                :accent         "#ff9100"
                :light-accent   "#ffab91"
                :primary-text   "#212121"
                :secondary-text "#757575"
                :divider        "#BDBDBD"
                :error          "#FF6688"}

               :light-green-teal
               {:dark-primary   "#689F38"
                :primary        "#8BC34A"
                :light-primary  "#DCEDC8"
                :text-icons     "#FEFEFE"
                :accent         "#004d40"
                :light-accent   "#80cbc4"
                :primary-text   "#212121"
                :secondary-text "#757575"
                :divider        "#BDBDBD"
                :error          "#FF6688"}

               :dark-on-light
               {:dark-primary   "#ffffff"
                :primary        "#eeeeee"
                :light-primary  "#666666"
                :text-icons     "#000000"
                :accent         "#304ffe"                       ; "#263238"
                :light-accent   "#303f9f"                      ; "#999999"
                :primary-text   "#444444"
                :secondary-text "#222222"
                :divider        "#666666"
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

(comment
  val
  (mapv val palettes)
  (mapv #(merge {:key val} (val %)) palettes)
  )