(defproject fannypack "0.1.0-SNAPSHOT"
  :description "An asset packer"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :java-source-paths ["src/java"]
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [environ "1.2.0"]
                 [io.bit3/jsass "5.10.3"]
                 [com.google.javascript/closure-compiler "v20200517"]]
  :plugins [[lein-environ "1.2.0"]]
  :repl-options {:init-ns fannypack.core})
