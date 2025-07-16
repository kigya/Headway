import com.android.build.api.dsl.CommonExtension
import extension.configureIfExists

configureIfExists(CommonExtension::class.java) {
    buildFeatures.compose = true
}
