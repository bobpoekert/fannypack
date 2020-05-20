(ns fannypack.settings)

(defonce ^:dynamic *settings*
  {
   :log-level :normal ;; can be :normal or :quiet
   :mode :debug ;; can be :debug or :prod
   })

(defn is-prod? []
  (= :prod (:mode *settings*)))
