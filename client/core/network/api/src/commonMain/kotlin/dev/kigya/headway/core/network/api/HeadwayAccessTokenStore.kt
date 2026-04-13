package dev.kigya.headway.core.network.api

interface HeadwayAccessTokenStore {
    fun current(): String?
    fun update(value: String?)
}
