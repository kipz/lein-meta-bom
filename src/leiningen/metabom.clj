(ns leiningen.metabom
  (:require
   [leiningen.core.classpath :as classpath]
   [leiningen.core.project :as project]
   [leiningen.jar :as jar]
   [leiningen.core.main :as main]
   [clojure.string :as str])
  (:import (java.io FileOutputStream)
           (java.util.zip ZipOutputStream ZipEntry)))

(defn fix-group
  [dep]
  (let [[group artifact] (str/split (str (first dep)) #"/")]
    (if artifact
      [group artifact (str (last dep))]
      [group group (str (last dep))])))

(defn flatten-graph [x]
  (->>
   x
   (tree-seq map? vals)
   (mapcat keys)
   (map #(take 2 %))
   (map fix-group)))

(defn pom-str
  [[group artifact version]]
  (format "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">
  <modelVersion>4.0.0</modelVersion>
  <groupId>%s</groupId>
  <artifactId>%s</artifactId>
  <packaging>jar</packaging>
  <version>%s</version>
  <name>%s</name>
</project>
"
          group
          artifact
          version
          artifact))

(defn add-entry
  [out path content-str]
  (main/info "Adding metabom entry: " path)
  (.putNextEntry out (ZipEntry. path))
  (.write out (.getBytes content-str))
  (.closeEntry out))

(defn props-str
  [[group artifact version]]
  (format "artifactId=%s
groupId=%s
version=%s\n" artifact group version))

(defn write-meta
  [project deps out]
  (let [[g a v] [(:group project) (:name project) (:version project)]
        a (str a "-metabom")]

    (add-entry out
               "META-INF/MANIFEST.MF"
               (format "Manifest-Version: 1.0\nCreated-By: org.kipz/lein-metabom\nName: %s" a))
    (add-entry out
               (format "META-INF/maven/%s/%s/pom.xml" g a)
               (pom-str [g a v]))
    (add-entry out
               (format "META-INF/maven/%s/%s/pom.properties" g a)
               (props-str [g a v]))

    (doseq [[g a :as dep] deps]

      (add-entry out
                 (format "META-INF/maven/%s/%s/pom.xml" g a)
                 (pom-str dep))
      (add-entry out
                 (format "META-INF/maven/%s/%s/pom.properties" g a)
                 (props-str dep)))))

(defn metabom
  "Create the meta jar - no classes"
  [project & args]
  (let [;; creates target directory etc
        _ (jar/get-jar-filename project)
        project (project/merge-profiles project [:metabom])
        deps (->> (classpath/managed-dependency-hierarchy
                   :dependencies :managed-dependencies project)
                  flatten-graph)
        filename (if-let [jar-name (-> project :metabom :jar-name)]
                   (format "%s/%s"
                           (:target-path project)
                           jar-name)
                   (format "%s/%s-metabom-%s.jar"
                           (:target-path project)
                           (:name project)
                           (:version project)))]
    (main/info "Creating metabom: " filename)
    (with-open [out (->  filename
                         (FileOutputStream.)
                         (ZipOutputStream.))]
      (main/info (format "Found %s dependencies" (count deps)))
      (write-meta project deps out))))