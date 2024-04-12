package functional.dnd5e.data

sealed interface Arm {
    val isShield: Boolean
    val isMainHand: Boolean

    fun isLegalFor(effect: Effect): Boolean
}

object NotEquipped : Arm {
    override val isShield: Boolean = false
    override val isMainHand: Boolean = false

    override fun isLegalFor(effect: Effect): Boolean = false
}

data class AttackArm(val weapon: Weapon = Weapon.UNARMED, val attackingStat: Stat = Stat.STR, override val isMainHand: Boolean = true) : Arm {
    override val isShield = false
    // TODO 'Dual Wielder Feat' can Dual Wield with weapons that do not have the 'light' property
    val canDualWield = weapon != Weapon.UNARMED && weapon.isMele && weapon.isLight
    override fun isLegalFor(effect: Effect): Boolean = when (effect) {
        Dueling -> weapon.isMele && weapon.isTwoHanded.not() // TODO also requires only one weapon equipped
        GreatWeaponFighting -> weapon.isMele && weapon.isTwoHanded
        GreatWeaponsMaster -> weapon.isMele && weapon.isHeavy
        Sharpshooter -> weapon.isMele.not() && weapon.isHeavy
        is AbilityModifier -> isMainHand || canDualWield
    }
}

data class Shield(val attackClassMod: Int) : Arm { // LIGHT(+1), MED(+2), HEAVY(+3)
    override val isShield: Boolean = true
    override val isMainHand: Boolean = false
    override fun isLegalFor(effect: Effect): Boolean  = false
}
