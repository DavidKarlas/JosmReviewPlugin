import java.net.URL

plugins {
  id("org.openstreetmap.josm").version("0.7.1")
  id("java")
}

sourceSets {
    main {
        java {
            srcDir("src").include("org/openstreetmap/**")
        }
        resources {
            srcDir(".").include("images/**/*.svg")
        }
    }
}

josm {
  pluginName = "Review Changes"
  debugPort = 2019
  josmCompileVersion = "18193"
  manifest {
    version = "1.0.1"
    description = "JOSM plugin for reviewing changes."
    mainClass = "org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.JosmReviewPlugin"
    minJosmVersion = "18193"
    author = "David Karlaš"
    canLoadAtRuntime = true
    iconPath = "dialogs/reviewPlugin/icon"
    website = URL("https://github.com/DavidKarlas/JosmReviewPlugin")
  }
}