(defproject uncomplicate/clojurecl "0.4.0-SNAPSHOT"
  :description "ClojureCL is a Clojure library for parallel computations with OpenCL."
  :url "https://github.com/uncomplicate/clojurecl"
  :scm {:name "git"
        :url "https://github.com/uncomplicate/clojurecl"}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0-RC2"]
                 [org.jocl/jocl "0.2.0-RC00"]
                 [org.clojure/core.async "0.2.374"]
                 [vertigo "0.1.3"]
                 [potemkin "0.4.1"] ;; temporary fix for vertigo
                 [clj-tuple "0.2.2"] ;; temporary fix for potemkin
                 ]

  :codox {:metadata {:doc/format :markdown}
          :src-dir-uri "http://github.com/uncomplicate/clojurecl/blob/master/"
          :src-linenum-anchor-prefix "L"
          :output-path "docs/codox"}

  ;;also replaces lein's default JVM argument TieredStopAtLevel=1
  :jvm-opts ^:replace ["-XX:MaxDirectMemorySize=16g" "-XX:+UseLargePages"]

  :profiles {:dev {:plugins [[lein-midje "3.1.3"]
                             [lein-codox "0.9.0"]]
                   :global-vars {*warn-on-reflection* true
                                 *assert* true
                                 *unchecked-math* :warn-on-boxed
                                 *print-length* 128}
                   :dependencies [[midje "1.8.2"]
                                  [criterium "0.4.3"]]}}

  :javac-options ["-target" "1.8" "-source" "1.8" "-Xlint:-options"]
  :source-paths ["src/clojure" "src/opencl"]
  :test-paths ["test/clojure" "test/opencl"]
  :java-source-paths ["src/java"])
