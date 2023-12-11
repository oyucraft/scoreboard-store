package dependencies

object Dependencies {
  object Kotlin {
    private const val VERSION = "1.9.10"
    const val JUNIT = "org.jetbrains.kotlin:kotlin-test-junit:${Dependencies.Kotlin.VERSION}"
  }

  object Ktor {
    const val VERSION = "2.3.3"
  }

  object LogBack {
    private const val VERSION = "1.2.11"
    const val CLASSIC = "ch.qos.logback:logback-classic:${Dependencies.LogBack.VERSION}"
  }
}