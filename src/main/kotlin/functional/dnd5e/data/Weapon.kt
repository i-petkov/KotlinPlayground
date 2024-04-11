package functional.dnd5e.data

import functional.dnd5e.d


// TODO check how [versatile, light, reach, finesse, thrown] affect dmg calculations and
//  interact with feats and fighting styles.
//  are they even needed to be included in the Weapon definition
class Weapon(
    val weaponDie: DieRoll,
    val isHeavy: Boolean = false,
    val isTwoHanded: Boolean = false,
    val hasSecondWeapon: Boolean = false,
    val hasProficientcy: Boolean = true,
    val isMele: Boolean = true,
    val isFinesse: Boolean = false,
    val isLight: Boolean = false
) {
    companion object {
        val GREAT_SWORD = Weapon(weaponDie = 2 d 6, isHeavy = true, isTwoHanded = true)
        val WARHAMMER = Weapon(weaponDie = 1 d 8)
        val WARHAMMER_VERSATILE = Weapon(weaponDie = 1 d 10, isTwoHanded = true)
        val UNARMED = Weapon(weaponDie = 1 d 4, isTwoHanded = false)
    }

    fun canApply(mod: Mod): Boolean = when(mod) {
        Mod.GWF -> isTwoHanded && isMele
    }

    fun canApply(flat: Flat) = when(flat) {
        Flat.DUELING -> !(isTwoHanded || hasSecondWeapon)
        Flat.GWM -> isHeavy && hasProficientcy && isMele
        Flat.SHARPSHOOTER -> hasProficientcy && !isMele
    }
}