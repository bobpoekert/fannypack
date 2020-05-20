(ns fannypack.core
  (:require [fannypack.settings :as settings]
            [fannypack.sass :as sass]
            [fannypack.js :as js]
            [fannypack.css :as css]
            [fannypack.utils :as utils]
            [ring.middleware.file :as rf]))

(def asset-deps
  (atom {:css {}
         :sass {}
         :js {}}))

(def suffixes {
               :css ".css"
               :js ".js"
               :sass ".css"})

(def extensions {
                 "sass" :sass
                 "css" :css
                 "js" :js})

(defn split-fname
  [fname]
  (let [[_ basename suffix] (re-groups (re-find #"^(.*?)\.(.*)$" fname))]
    [basename (get extensions suffix)]))

(defn make-fname [kind fname digest]
  (str fname "-" digest (get suffixes kind)))

(defn update-built-file!
  [kind fname content]
  (let [digest (utils/digest content)]
    (utils/write-asset! (make-fname kind fname digest) content)
    (swap! asset-deps
           (fn [prev]
             (assoc prev kind
                    (assoc (get prev kind) fname digest))))))


(defn compile-asset [kind fname]
  (let [compiler (case kind :js js/compile-asset :css css/compile-asset :sass sass/compile-asset)]
    (compiler fname)))

(defmacro include-asset [fname]
  (if (utils/prod?)
    (let [[kind fname] (split-fname fname)]
      (when-not (contains? (get @asset-deps kind) fname)
        (->>
          (compile-asset kind fname)
          (update-built-file! kind fname)))
      (str "/assets/" (make-fname kind fname (get (get @asset-deps kind) fname))))
    (str "/assets/" fname (get suffixes kind))))


(defn middleware [handler]
  (if (utils/prod?)
    (rf/wrap-resource handler "public")
    (fn [req res raise]
      (let [match (re-find #"/assets/(.*?)-(.*?)\.(.*?)" (:uri req))]
        (if match
          (let [[fname digest suffix] (re-groups match)]
            {:status 200 :body (compile-asset (get extensions suffix) fname)})
          (handler req res raise))))))

