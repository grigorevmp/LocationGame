package com.mikhailgrigorev.game.core.wrappers

class LockableAbility <Ability> (
    ability: Ability? = null,
    locked: Boolean = true
    ) {

    var ability: Ability? = ability
        private set

    var locked: Boolean = locked
        private set

    fun lock(){
        locked = true
    }

    fun unlock() {
        locked = false
    }
}