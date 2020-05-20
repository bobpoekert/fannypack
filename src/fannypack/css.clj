(ns fannypack.css
  (:require [fannypack.utils :as utils])
  (:import [com.platform.yui.compressor CssCompressor]
           [java.io StringWriter FileReader]))

(defn compile-asset [filename]
  (let [filepath (str utils.root-path "/assets/css/" filename)]
    (if (utils/prod?)
      (with-open [inf (FileReader. filepath)]
        (let [res (StringWriter.)
              compressor (CssCompressor. inf)]
          (.compress compressor res 0)
          (.toString res)))
      (slurp filepath))))
