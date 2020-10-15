package com.mikhailgrigorev.game.core.ecs.Components.Data

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
    airValue: Int,
    waterValue: Int,
    earthValue: Int,
    fireValue: Int
) {
    var natureForcesValues = Array<Int>(NatureForces.count) {0}
        private set

    init {
        natureForcesValues[NatureForces.Air.ordinal] = airValue
        natureForcesValues[NatureForces.Water.ordinal] = waterValue
        natureForcesValues[NatureForces.Earth.ordinal] = earthValue
        natureForcesValues[NatureForces.Fire.ordinal] = fireValue

    }
}