plugins { id("com.android.application") }

android {
    namespace = "com.flymaccin.meadowlarkdemonic"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.flymaccin.meadowlarkdemonic"
        minSdk = 26
        targetSdk = 35
        versionCode = 102
        versionName = "1.0.0-rc3"
    }
    signingConfigs {
        create("release") {
            val storePath = System.getenv("FME_KEYSTORE_PATH")
            if (!storePath.isNullOrBlank()) {
                storeFile = file(storePath)
                storePassword = System.getenv("FME_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("FME_KEY_ALIAS")
                keyPassword = System.getenv("FME_KEY_PASSWORD")
            }
        }
    }
    buildTypes {
        debug { isDebuggable = true }
        release {
            isMinifyEnabled = false
            if (!System.getenv("FME_KEYSTORE_PATH").isNullOrBlank()) signingConfig = signingConfigs.getByName("release")
        }
    }
}
dependencies {
    implementation(project(":core-midi"))
    implementation(project(":core-sequencer"))
    testImplementation("junit:junit:4.13.2")
}
