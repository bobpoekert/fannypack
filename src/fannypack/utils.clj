(ns fannypack.utils
  (:require [environ.core :refer [env]]
            [clojure.java.io :as io])
  (:import [java.nio.file Path FileSystems]))

(defn prod? []
  (not (env :dev?)))

(def kind-folders {
                   :css "css"
                   :js "js"
                   :sass "sass"})

(def root-path (System/getProperty "user.dir"))

(defn asset-path [kind fnames]
  (if-let [prefix (get kind-folders kind)]
    (->
        (FileSystems/getDefault)
        (.getPath root-path (into-array String (cons "assets" (cons prefix fnames)))))))

(defn digest [^String data]
  (let [res (java.security.MessageDigest/getInstance "SHA1")]
    (.update res (.getBytes data "UTF-8"))
    (javax.xml.bind.DatatypeConverter/printHexBinary (.digest res))))

(defn write-asset!
  [fname content]
  (->
    (io/file root-path "resources" "public" fname)
    (spit content)))

