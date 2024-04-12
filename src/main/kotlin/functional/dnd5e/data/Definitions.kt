package functional.dnd5e.data

import functional.dnd5e.adding
import functional.dnd5e.modifierScaling

data class DieRoll(val rollNumber:Int, val rollSize:Int)

data class Attack(val naturalRoll: Int, val proficiency: () -> Int, val abilityModifier: () -> Int) {
    fun rollWithBonuses() = { naturalRoll }.adding(proficiency).adding(abilityModifier)
}

enum class Feat {
    GREAT_WEAPON_MASTER, SHARPSHOOTER
}

enum class FightingStyles {
    GREAT_WEAPON_FIGHTING, DUELING, TWO_WEAPON_FIGHTING
}

enum class Stat {
    STR,
    DEX,
    CONSTITUTION,
    INTELLECT,
    CHARISMA
}

data class Stats(
    val str: Int = 10,
    val dex: Int = 10,
    val const: Int = 10,
    val intellect: Int = 10,
    val charisma: Int = 10
) {
    fun modifier(attackStat: Stat): Int {
        return when(attackStat) {
            Stat.STR -> modifierScaling(str)
            Stat.DEX -> modifierScaling(dex)
            Stat.CONSTITUTION -> modifierScaling(const)
            Stat.INTELLECT -> modifierScaling(intellect)
            Stat.CHARISMA -> modifierScaling(charisma)
        }
    }
}