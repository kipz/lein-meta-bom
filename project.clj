(defproject org.kipz/lein-meta-bom "0.1.0-SNAPSHOT"
  :description "Generate a thin jar file representing a bill of materials (BOM) for use by scanners like grype, trivy etc"
  :url "https://github.com/kipz/lein-meta-bom"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :deploy-repositories {"releases" {:url "https://repo.clojars.org" :creds :gpg}}
  :eval-in-leiningen true)
