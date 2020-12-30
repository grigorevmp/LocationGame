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
    airValue: Int = 0,
    waterValue: Int = 0,
    earthValue: Int = 0,
    fireValue: Int = 0
) {
    var natureForcesValues = Array(NatureForces.count) {0}
        private set

    init {
        natureForcesValues[NatureForces.Air.ordinal] = airValue
        natureForcesValues[NatureForces.Water.ordinal] = waterValue
        natureForcesValues[NatureForces.Earth.ordinal] = earthValue
        natureForcesValues[NatureForces.Fire.ordinal] = fireValue

    }
}