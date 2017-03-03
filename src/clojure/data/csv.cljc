;; Copyright (c) Jonas Enlund. All rights reserved.  The use and
;; distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns ^{:author "Jonas Enlund"
      :doc "Reading and writing comma separated values."}
    clojure.data.csv
  (:refer-clojure :exclude [write IWriter read])
  (:require [clojure.string :as str]
            #?(:cljs [goog.string :as gstring])
            #?(:cljs [goog.string.format]))

  (:import #?(:clj [java.io PushbackReader Reader Writer StringReader EOFException]
              :cljs [])))


;; Reading

(defn- ->int [chr]
  #?(:clj (int chr)
     :cljs (.charCodeAt chr)))

(def ^{:private true} lf (->int \newline))
(def ^{:private true} cr (->int \return))
(def ^{:private true} eof -1)

(defprotocol IPushbackReader
  (read [reader] "Returns the next char (as an int) from the Reader,
-1 if the end of stream has been reached")
  (unread [reader ch] "Push back a single character on to the stream"))

(defprotocol IStringBuilder
  (append [this chr]))

(defprotocol IWriter
  (write [this str]))

#?(:cljs
   (deftype PushbackReader [s buffer ^:mutable idx]
     IPushbackReader
     (read [reader]
       (if (zero? (alength buffer))
         (do
           (set! idx (inc idx))
           (if-let [c (aget s idx)]
             (->int c)
             eof))
         (.pop buffer)))
     (unread [reader ch]
       (.push buffer (char ch)))))

#?(:clj
   (extend-type PushbackReader
     IPushbackReader
     (read [reader]
       (.read reader))
     (unread [reader #^chars chrs]
       (.unread reader chrs))))

#?(:cljs
   (defrecord StringBuilder [^:mutable s]
     Object
     (toString [_] s)
     IStringBuilder
     (append [this chr]
       (set! s (str s chr)))))

#?(:clj
   (extend-type StringBuilder
     IStringBuilder
     (append [this chr]
       (.append this chr))))

#?(:cljs
   (defrecord Writer [^:mutable string]
     Object
     (toString [_] string)
     IWriter
     (write [this i]
       (let [tmp (if (integer? i) (char i) i)]
         (set! string (str string tmp))))))

#?(:clj
   (extend-type Writer
     IWriter
     (write [writer #^chars chrs]
       (.write writer chrs))))

(defn- create-stringbuilder []
  #?(:clj (StringBuilder.)
     :cljs (StringBuilder. "")))

(defn- read-quoted-cell [^PushbackReader reader ^StringBuilder sb sep quote]
  (loop [ch (read reader)]
    (condp == ch
      quote (let [next-ch (read reader)]
              (condp = next-ch
                quote (do
                        (append sb (char quote))
                          (recur (read reader)))
                sep :sep
                lf  :eol
                cr  (let [next-next-ch (read reader)]
                      (when (not= next-next-ch lf)
                        (unread reader next-next-ch))
                      :eol)
                eof :eof
                (throw
                 #?(:clj
                    (Exception. ^String (format "CSV error (unexpected character: %c)" next-ch))
                    :cljs
                    (js/Error. (gstring/format "CSV error (unexpected character: %c)" next-ch))))))
      eof (throw ( #?(:clj EOFException. :cljs js/Error.)  "CSV error (unexpected end of file)"))
      (do (append sb (char ch))
          (recur (read reader))))))

(defn- read-cell [^PushbackReader reader ^StringBuilder sb sep quote]
  (let [first-ch (read reader)]
    (if (== first-ch quote)
      (read-quoted-cell reader sb sep quote)
      (loop [ch first-ch]
        (condp == ch
          sep :sep
          lf  :eol
          cr (let [next-ch (read reader)]
               (when (not= next-ch lf)
                 (unread reader next-ch))
               :eol)
          eof :eof
          (do
            (append sb (char ch))
              (recur (read reader))))))))

(defn- read-record [reader sep quote]
  (loop [record (transient [])]
    (let [cell (create-stringbuilder)
          sentinel (read-cell reader cell sep quote)]
      (if (= sentinel :sep)
        (recur (conj! record (str cell)))
        [(persistent! (conj! record (str cell))) sentinel]))))

(defprotocol Read-CSV-From
  (read-csv-from [input sep quote]))

#?(:clj
   (extend-protocol Read-CSV-From
     String
     (read-csv-from [s sep quote]
       (read-csv-from (PushbackReader. (StringReader. s)) sep quote))
     Reader
     (read-csv-from [reader sep quote]
       (read-csv-from (PushbackReader. reader) sep quote)))
   :cljs
   (extend-type string
     Read-CSV-From
     (read-csv-from [s sep quote]
       (read-csv-from (PushbackReader. s (array) -1) sep quote))))


(extend-protocol Read-CSV-From
  PushbackReader
  (read-csv-from [reader sep quote]
    (lazy-seq
     (let [[record sentinel] (read-record reader sep quote)]
       (case sentinel
         :eol (cons record (read-csv-from reader sep quote))
         :eof (when-not (= record [""])
                (cons record nil)))))))

(defn read-csv
  "Reads CSV-data from input (String or java.io.Reader) into a lazy
  sequence of vectors.

   Valid options are
     :separator (default \\,)
     :quote (default \\\")"
  [input & options]
  (let [{:keys [separator quote] :or {separator \, quote \"}} options]
    (read-csv-from input (->int separator) (->int quote))))


;; Writing

(defn- write-cell [^Writer writer obj sep quote quote?]
  (let [string (str obj)
	must-quote (quote? string)]
    (when must-quote (write writer (->int quote)))
    (write writer (if must-quote
		     (str/escape string
				 {quote (str quote quote)})
		     string))
    (when must-quote (write writer (->int quote)))))

(defn- write-record [^Writer writer record sep quote quote?]
  (loop [record record]
    (when-first [cell record]
      (write-cell writer cell sep quote quote?)
      (when-let [more (next record)]
	(write writer (->int sep))
	(recur more)))))

(defn- write-csv* #?(:clj [^Writer writer records sep quote quote? ^String newline]
                     :cljs [^Writer writer records sep quote quote? newline])
  (loop [records records]
    (when-first [record records]
      (write-record writer record sep quote quote?)
      (write writer newline)
      (recur (next records)))))

(defn write-csv
  "Writes data to writer in CSV-format.

   Valid options are
     :separator (Default \\,)
     :quote (Default \\\")
     :quote? (A predicate function which determines if a string should be quoted. Defaults to quoting only when necessary.)
     :newline (:lf (default) or :cr+lf)"
  [writer data & options]
  (let [opts (apply hash-map options)
        separator (or (:separator opts) \,)
        quote (or (:quote opts) \")
        quote? (or (:quote? opts) #(some #{separator quote \return \newline} %))
        newline (or (:newline opts) :lf)]
    (write-csv* writer
		data
		separator
		quote
                quote?
		({:lf "\n" :cr+lf "\r\n"} newline))))
