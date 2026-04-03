package org.ub.animations

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform