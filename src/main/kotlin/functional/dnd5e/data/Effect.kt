package functional.dnd5e.data

import functional.dnd5e.greatWeaponsFighting

sealed interface Effect {
    val isRoll: Boolean
    val func: (()->Int)->Int
}

object Dueling : Effect {
    override val isRoll: Boolean = false
    override val func: (() -> Int) -> Int = { it() + 2 }
}

object GreatWeaponFighting : Effect {
    override val isRoll: Boolean = true
    override val func: (() -> Int) -> Int = ::greatWeaponsFighting
}

object GreatWeaponsMaster : Effect {
    override val isRoll: Boolean = false
    override val func: (() -> Int) -> Int = { it() + 10 }
}

object Sharpshooter : Effect {
    override val isRoll: Boolean = false
    override val func: (() -> Int) -> Int = { it() + 10 }
}

class AbilityModifier(override val func: (() -> Int) -> Int) : Effect {
    override val isRoll: Boolean = false
}