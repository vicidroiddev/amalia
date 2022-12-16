package buildSrc

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.publish.maven.MavenPublication
import java.net.URI

object PublishHelper {
    fun MavenPublication.ensurePomDetails(libraryName: String) {
        pom {
            name.set(libraryName)
            description.set("Amalia is an MVP/MVI implementation dictating a straightforward uni-directional flow of view states to render and view events to process.")
            url.set("https://vicidroiddev.github.io/amalia/")
            developers {
                developer {
                    id.set("ViciDroid")
                    name.set("ViciDroid")
//          email.set("")
                }
            }

            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
        }
//    pom.withXml {
//      val dependenciesNode = asNode().appendNode("dependencies")
//      val configurationNames = arrayOf("implementation", "api")
//      configurationNames.forEach { configurationName ->
//        configurations[configurationName].allDependencies.forEach {
//          if (it.group != null) {
//            val dependencyNode = dependenciesNode.appendNode("dependency")
//            dependencyNode.appendNode("groupId", it.group)
//            dependencyNode.appendNode("artifactId", it.name)
//            dependencyNode.appendNode("version", it.version)
//          }
//        }
//      }
//    }
    }

    fun RepositoryHandler.useLocalBuildMavenRepo(uri: URI) {
        maven {
            name = "myrepo"
            url = uri
        }
    }
}