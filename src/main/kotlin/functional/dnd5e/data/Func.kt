package functional.dnd5e.data

import functional.dnd5e.advantage
import functional.dnd5e.proficiencyScaling
import functional.dnd5e.roll

val character = Character(Stats(), 1, Stat.CHARISMA)
val weapon = Weapon.GREAT_SWORD

sealed interface Arms {
    val isEquipped: Boolean
    val isShield: Boolean
    val isOffhand: Boolean

    fun isLegalFor(effect: Effect): Boolean = TODO()
}

object NotEquipped : Arms {
    override val isEquipped: Boolean = false
    override val isShield: Boolean = false
    override val isOffhand: Boolean = false

    override fun isLegalFor(effect: Effect): Boolean = false
}

data class AttackArm(val weapon: Weapon = Weapon.UNARMED, val attackingStat: Stat = Stat.STR, override val isOffhand: Boolean = false) : Arms {
    override val isEquipped = false
    override val isShield = false
    // TODO 'Dual Wielder Feat' can Dual Wield with weapons that do not have the 'light' property
    val canDualWield = weapon != Weapon.UNARMED && weapon.isMele && weapon.isLight
    override fun isLegalFor(effect: Effect): Boolean = when (effect) {
        DUELING -> weapon.isMele && weapon.isTwoHanded.not() // TODO also requires only one weapon equipped
        GWF -> weapon.isMele && weapon.isTwoHanded
        GWM -> weapon.isMele && weapon.isHeavy
        SHARPSHOOTER -> weapon.isMele.not() && weapon.isHeavy
        is TwoWeaponFighting -> canDualWield && isOffhand
    }
}

data class Shield(val attackClassMod: Int) : Arms { // LIGHT(+1), MED(+2), HEAVY(+3)
    override val isEquipped: Boolean = false
    override val isShield: Boolean = true
    override val isOffhand: Boolean = true
    override fun isLegalFor(effect: Effect): Boolean  = false
}

data class AttackState(
    val mainHand: Arms = NotEquipped,
    val offHand: Arms = NotEquipped,
    val feats: HashSet<Feat>,
    val fightingStyles: HashSet<FightingStyles>
) {
    fun isDualWielding() = ((mainHand as? AttackArm)?.canDualWield == true) &&
            ((offHand as? AttackArm)?.canDualWield == true)
}

val attackState = character.attackState()

fun Character.attackState() = AttackState(feats = feats, fightingStyles = fightingStyles)

fun Character.proficiencyModifier() = proficiencyScaling(level)

fun Character.abilityModifier() = stats.modifier(attackStat)

//fun Character.attackRoll(): () -> Int = { roll(20) }

//fun Character.attack(): () -> Int {
//    return attackRoll()
//        .with(::advantage)
//        .adding { proficiencyModifier() }
//        .adding { abilityModifier() }
//}

// TODO WIP
fun Character.damageRoll(): () -> Int {
    val attSt = AttackState(
        mainHand = AttackArm(weapon, attackStat),
        offHand = AttackArm(),
        feats = feats,
        fightingStyles = fightingStyles
    )

    return when(attSt.isDualWielding()) {
        true -> {
            { /* main attack */ 10 }
                .with { /* fighting styles */ 12 }
                .with { /* feats */ 14 }
                .adding { proficiencyModifier() }
                .adding { abilityModifier() }
        }
        false -> {

            // TODO when are weapon effects added
            // TODO when are Hunters mark or Curse of Hex added
            // TODO critical hit bonuses

            // TODO: 'Two-Weapon Fighting style' allows you to add your ability Modifier to your bonus action attack (DualWield)
            // TODO: negative ability modifiers must be added to bonus attack (DualWield)
            val bonusAttack = { /* bonus action DW */ 12 }
                .with { /* fighting styles */ 12 }
                .with { /* negative ab_mod */ 10}

            val mainAttack = { /* main attack */ 10 }
                .with { /* fighting styles */ 12 }
                .with { /* feats */ 14 }
                .adding { proficiencyModifier() }
                .adding { abilityModifier() }

            mainAttack.adding(bonusAttack)
        }
    }
}

fun <R> (() -> R).with(f: (() -> R) -> R): () -> R {
    return { f(this) }
}

fun (() -> Int).adding(f: () -> Int): () -> Int {
    return { this() + f() }
}
