package com.kigya.headway.data.model

import java.io.Serializable

data class SourceDomainModel(
    val id: String? = null,
    val name: String? = null,
) : Serializable {
    companion object {
        const val serialVersionUID = 2L
    }
}