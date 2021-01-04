package com.mikhailgrigorev.game.core.data

enum class NatureForces {
    Air,
    Water,
    Earth,
    Fire;

    companion object {
        var count = NatureForces.values().size
            private set
    }
}

class NatureForcesValues (
    air: Int = 0,
    water: Int = 0,
    earth: Int = 0,
    fire: Int = 0
) {
    var values = Array(NatureForces.count) {0}
        private set

    init {
        values[NatureForces.Air.ordinal] = air
        values[NatureForces.Water.ordinal] = water
        values[NatureForces.Earth.ordinal] = earth
        values[NatureForces.Fire.ordinal] = fire

    }
}