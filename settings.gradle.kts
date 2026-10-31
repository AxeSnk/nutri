pluginManagement {
    repositories {
        // Рабочие источники первыми (проверено curl); медленные зеркала убраны
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "Nutri"
// Платный AI-модуль подключается одной строкой, когда будет готов:
// include(":ai")
include(":app")
