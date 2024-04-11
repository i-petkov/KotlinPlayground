package functional.dnd5e.data

import functional.dnd5e.greatWeaponsFighting
import functional.dnd5e.modifierScaling
import functional.dnd5e.proficiencyScaling
import functional.dnd5e.roll
import kotlin.math.max

class Character(
    var stats: Stats,
    var level: Int,
    val attackStat: Stat
) {
    val feats: HashSet<Feat> = HashSet()
    val fightingStyles: HashSet<FightingStyles> = HashSet()

    /**
     * val char = Character(Stats(), 1, Stat.STR)
     *
     * char.rollAttack(::noAdvantage)
     * char.rollAttack(::advantage)
     * char.rollAttack(::disadvantage)
     * char.rollAttack(::superAdvantage)
     */
    fun rollAttack(advantage: (() -> Int) -> Int): Int {
        return advantage { roll(20) } + stats.modifier(attackStat, ::modifierScaling) + proficiencyScaling(level)
    }

    fun rollDamage(weapon: Weapon): Int {
        var attack = { roll(weapon.weaponDie.rollSize) }
        // mods apply on each die roll (applies to attack)
        // * GWF
        // * etc..
        if (fightingStyles.contains(FightingStyles.GREAT_WEAPON_FIGHTING) && weapon.canApply(Mod.GWF))
            attack = attack.applyMod(Mod.GWF)

        var damage = attack.repeat(weapon.weaponDie.rollNumber)()
        // flat bonuses apply only once per attack (applies to damage)
        // * DUELING
        // * GWM
        // * etc..
        if (fightingStyles.contains(FightingStyles.DUELING) && weapon.canApply(Flat.DUELING))
            damage = damage.applyFlat(Flat.DUELING)
        if (feats.contains(Feat.GREAT_WEAPON_MASTER) && weapon.canApply(Flat.GWM))
            damage = damage.applyFlat(Flat.GWM)
        if (feats.contains(Feat.SHARPSHOOTER) && weapon.canApply(Flat.SHARPSHOOTER))
            damage = damage.applyFlat(Flat.SHARPSHOOTER)

        // TODO assuming provided attack stat
        //  eventually should provide calculation for attack stat base of:
        //  * Mele vs Ranged Weapon (STR vs DEX)
        //  * Finesse Weapon (STR vs DEX)
        //  * Warlock Hexblade (CHAR vs STR vs DEX)
        return damage + stats.modifier(attackStat, ::modifierScaling)
    }

    private fun (()-> Int).applyMod(mod: Mod): () -> Int = when(mod) {
        Mod.GWF -> { { greatWeaponsFighting(this) } }
    }

    private fun Int.applyFlat(modifier: Flat) = when(modifier) {
        Flat.DUELING -> this + 2
        Flat.GWM -> this + 10
        Flat.SHARPSHOOTER -> this + 10
    }

    private fun (() -> Int).repeat(times: Int): () -> Int = when(times) {
        1 -> this
        2 -> { { this() + this() } }
        3 -> { { this() + this() + this() } }
        4 -> { { this() + this() + this() + this() } }
        5 -> { { this() + this() + this() + this() + this() } }
        6 -> { { this() + this() + this() + this() + this() + this() } }
        7 -> { { this() + this() + this() + this() + this() + this() + this() } }
        8 -> { { this() + this() + this() + this() + this() + this() + this() + this() } }
        else -> throw UnsupportedOperationException("Not implemented")
    }
}