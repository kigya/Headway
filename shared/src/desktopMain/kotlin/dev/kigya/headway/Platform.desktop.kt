package dev.kigya.headway

class DPlatform: Platform {
    override val name: String = "ssm"
}
actual fun getPlatform(): dev.kigya.headway.Platform {
    return DPlatform()
}
