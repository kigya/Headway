package dev.kigya.headway

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
