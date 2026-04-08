package fr.leandremru.rickandmortylocations

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform