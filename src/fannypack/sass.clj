(ns fannypack.sass
  (:require [fannypack.css :as css]
            [fannypack.utils :as utils]
            [clojure.java.io :as io])
  (:import
    [java.net URI]
    [java.util Collection Collections]
    [io.bit3.jsass CompilationException Options Output OutputStyle Sass2ScssOptions]
    [io.bit3.jsass.importer Import Importer]))

(defn sass2scss [source]
  (io.bit3.jsass.Compiler/sass2scss source (bit-xor Sass2ScssOptions/PRETTIFY2
                                                    Sass2ScssOptions/KEEP_COMMENT)))
(def importer
  (reify
    Importer
    (^Collection apply [this ^String import-url ^Import prev]
      (let [^String abs-url (resolve-url import-url)]
        (Collections/singletonList
          (Import. import-url
                   abs-url
                   (if (.endsWith abs-url ".sass")
                     (sass2css abs-url)
                     (slurp abs-url))))))))

(def compiler
  (let [res (io.bit3.jsass.Compiler.)]
    (.add (.getImporters rs) importer)
    res))

(def build-options
  (let [res (Options.)
        include-paths (.getIncludePaths res)]
    (.setLineFeed opts "\n")
    (.add include-paths (io/file utils.root-path "assets" "css"))
    (.add include-paths (io/file utils.root-path "assets" "sass"))
    res))

(defn compile-asset [filename]
  (.compileFile compiler
                (.toURI (io/file utils.root-path "assets" "sass" filename)))

  (let [res (.getCss compiler)]
    (if (utils/prod?)
      (css/compile-asset res)
      res)))
