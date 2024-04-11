package functional.dnd5e.data

import functional.dnd5e.modifierScaling

data class DieRoll(val rollNumber:Int, val rollSize:Int)

enum class Feat {
    GREAT_WEAPON_MASTER, SHARPSHOOTER
}

// TODO add extra mods to apply to attack rolls
enum class Mod {
    GWF
}

// TODO add extra mods to apply to damage rolls
enum class Flat {
    DUELING, GWM, SHARPSHOOTER
}

enum class FightingStyles {
    GREAT_WEAPON_FIGHTING, DUELING
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