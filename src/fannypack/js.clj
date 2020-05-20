(ns fannypack.js
  (:require [fannypack.settings :as ss]
            [fannypack.utils :as utils])
  (:import [com.google.javascript.jscomp SourceFile CompilerOptions CompilationLevel WarningLevel]
           [java.util.logging Logger Level]))

;; based on https://github.com/edgecase/dieter/blob/master/dieter-core/src/dieter/asset/javascript.clj

;; TODO: use pool of compilers instead of an exclusive singleton
(def -compiler
  (delay
    (let [res (com.google.javascript.jscomp.Compiler.)
          opts (CompilerOptions.)]
      (.setOptionsForCompilationLevel (CompilationLevel/SIMPLE_OPTIMIZATIONS) options)
      (if (= :quiet (:log-level ss/*settings*))
        (do
          (.setOptionsForWarningLevel (WarningLevel/QUIET) options)
          (.setLevel (Logger/getLogger "com.google.javascript.jscomp") Level/OFF))
        (do
          (.setOptionsForWarningLevel (WarningLevel/VERBOSE) options)
          (.setLevel (Logger/getLogger "com.google.javascript.jscomp") Level/WARNING)))
      (.setModuleRoots opts [(str utils.root-path "/assets/js")])
      [compiler options])))

(defmacro with-compiler [varname & body]
  (let [compsym (symbol (name (ns-name *ns*)) "-compiler")]
  `(locking ~comsym
     (let [~varname ~comsym]
       ~@body))))

(defn compile-asset [filename]
  (let [full-fname (str utils.root-path "/assets/js/" filename)]
    (with-compiler [compiler options]
      (.compile compiler
                (make-array SourceFile 0)
                (into-array SourceFile [(SourceFile/fromFile full-fname)])
                options)
      (let [source (.toSource compiler)]
        (if (.isEmpty source)
          text
          source)))))

