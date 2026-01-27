rootProject.name = "BackyardBirds"

include(":app")
include(":core")
include(":data")
include(":database")
include(":domain")
include(":shared")
include(":features:api_explorer")
include(":features:analytics")
include(":features:predictions")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
