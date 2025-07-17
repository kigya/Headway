package base

open class AndroidApplicationConventionParams {
    var namespace: String? = null
    var versionCode: Int? = null
    var versionName: String? = null
    val resourceConfigurations: MutableList<String> = mutableListOf()
}
