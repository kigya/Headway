import extension.commonMainDependencies

plugins {
    alias(libs.plugins.convention.base.sharedLibrary)
    alias(libs.plugins.convention.component.serialization)
    alias(libs.plugins.apollo)
}

apollo {
    service("headway") {
        packageName.set("dev.kigya.headway.core.apollo.generated")
        mapScalarToKotlinLong("Long")
        schemaFiles.from(file("src/commonMain/graphql/schema.graphqls"))
    }
}

commonMainDependencies {
    projects {
        implementation(core.networkApi)
    }
    libs {
        implementation(apollo.runtime)
    }
}
