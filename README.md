# lein-metabom

[![Clojars Project](https://img.shields.io/clojars/v/org.kipz/lein-metabom.svg)](https://clojars.org/org.kipz/lein-metabom)

## Description

Generates a jar file containing metadata about the current project and its dependencies in a form that container vulnerability scanning tools such as [grype](https://github.com/anchore/grype) recognise.

This is useful if, for example, if you are creating uberjars, graalvm native images or some other lossy repackaging tooling.

## Usage

Put `[org.kipz/lein-metabom "0.1.0"]` into the `:plugins` vector of your `:user`
profile or in the `:plugins` of your poject.clj:

Then run

```shell
lein metabom
```

Which will generate a jar file named `<project-name>-metabom.jar` containing only metadata about the project and its dependencies e.g.

```shell
$ lein metabom
Creating metabom:  /home/kipz/scm/kipz/lein-meta-bom/test-projects/target/test-project-metabom-0.1.0-SNAPSHOT.jar
Found 13 dependencies
Adding metabom entry:  META-INF/MANIFEST.MF
Adding metabom entry:  META-INF/maven/org.kipz/test-project-metabom/pom.xml
Adding metabom entry:  META-INF/maven/org.kipz/test-project-metabom/pom.properties
Adding metabom entry:  META-INF/maven/cc.qbits/knit/pom.xml
Adding metabom entry:  META-INF/maven/cc.qbits/knit/pom.properties
Adding metabom entry:  META-INF/maven/cc.qbits/commons/pom.xml
Adding metabom entry:  META-INF/maven/cc.qbits/commons/pom.properties
Adding metabom entry:  META-INF/maven/org.clojure/clojure/pom.xml
...
```






