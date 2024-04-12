package functional.dnd5e.data

import functional.dnd5e.*

sealed interface Monad<T> {
    fun <K> map(f: (T) -> K): Monad<K>
    fun <K> fmap(f: (T) -> Monad<K>): Monad<K>
    fun getOr(default: T): T
}

class Just<T>(val value: T) : Monad<T> {
    override fun <K> map(f: (T) -> K): Monad<K> = Just(f(value))
    override fun <K> fmap(f: (T) -> Monad<K>): Monad<K> = f(value)
    override fun getOr(default: T): T = value
}

class Nothing<T> : Monad<T> {
    override fun <K> map(f: (T) -> K): Monad<K> = Nothing()
    override fun <K> fmap(f: (T) -> Monad<K>): Monad<K> = Nothing()
    override fun getOr(default: T): T = default
}

fun main() {
    val character = Character(Stats(charisma = 20), 20, Stat.CHARISMA)
    print(calculateAttack(character, ::advantage))
}

fun calculateAttack(character: Character, advantage: (()->Int)->Int): Int {
    val attackState = AttackState(
        mainHand = AttackArm(Weapon.WARHAMMER),
        offHand = Shield(3),
        feats = character.feats,
        fightingStyles = character.fightingStyles
    )

    return if (attackState.isDualWielding()) {
        calculateAttack(character, attackState.mainHand, advantage).getOr(0) +
                calculateAttack(character, attackState.offHand, advantage).getOr(0)
    } else {
        calculateAttack(character, attackState.mainHand, advantage).getOr(0)
    }
}

fun calculateAttack(character: Character, arms: Arms, advantage: (()->Int)->Int): Monad<Int> {
    val effects = mapEffects(character)
    return Just(arms)
        .fmap { if (it is AttackArm) Just(it) else Nothing() }
        .fmap { arm: AttackArm ->
            val func = { roll(20) }
                .with(advantage)
                .adding { character.proficiencyModifier() }
                .adding { character.stats.modifier(arm.attackingStat) }

            Just(func()).fmap {
                // calc attack
                when {
                    // TODO crit can only occur on a natural 20 roll (before modifiers)
                    it == 20 -> Just(true)
                    it >= 10 -> Just(false)
                    else -> Nothing()
                }
            }.map { isCrit ->
                // calk dmg

                // TODO https://rpg.stackexchange.com/questions/131993/does-dual-wielding-count-as-two-attacks-and-therefore-two-actions
                //  DW makes a second attack as a bonus action -> second attack roll + second dmg roll (feats determine if you add ability modifier to second dmg roll)

                val gr = effects
                    .filter { effect -> arm.isLegalFor(effect) }
                    .groupBy { it.isRoll }

                val baseAttack = { roll(arm.weapon.weaponDie.rollSize) }

                val rollModifiers = gr.getOrEmpty(true)
                val damageModifiers = gr.getOrEmpty(false)

                val rollRepetitions = if(isCrit) {
                    weapon.weaponDie.rollNumber * 2
                } else {
                    weapon.weaponDie.rollNumber
                }

                rollModifiers.composeEffectsOnToAttack(attack = baseAttack)
                    .repeat(rollRepetitions)
                    .let { damageModifiers.composeEffectsOnToAttack(attack = it) }
                    .let { it() }
            }
        }
}


fun mapEffects(character: Character): List<Effect> {
    val feats: List<Effect> = character.feats.map {
        when(it) {
            Feat.GREAT_WEAPON_MASTER -> GWM
            Feat.SHARPSHOOTER -> SHARPSHOOTER
        }
    }

    val fightingStyles: List<Effect> = character.fightingStyles.map {
        when(it) {
            FightingStyles.GREAT_WEAPON_FIGHTING -> GWF
            FightingStyles.DUELING -> DUELING
            FightingStyles.TWO_WEAPON_FIGHTING -> TwoWeaponFighting(character)
        }
    }

    return feats + fightingStyles
}

fun List<Effect>.composeEffectsOnToAttack(index: Int = 0, attack: () -> Int): () -> Int =
    if (index in indices) {
        composeEffectsOnToAttack(index + 1, attack = { get(index).func(attack) })
    } else {
        attack
    }

fun Map<Boolean, List<Effect>>.getOrEmpty(key: Boolean) = getOrDefault(key, emptyList())

fun (() -> Int).repeat(times: Int): () -> Int = when(times) {
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

sealed interface Effect {
    val isRoll: Boolean
    val func: (()->Int)->Int
}

object GWF : Effect {
    override val isRoll: Boolean = true
    override val func: (() -> Int) -> Int = ::greatWeaponsFighting
}

object GWM : Effect {
    override val isRoll: Boolean = false
    override val func: (() -> Int) -> Int = { it() + 10 }
}

object SHARPSHOOTER : Effect {
    override val isRoll: Boolean = false
    override val func: (() -> Int) -> Int = { it() + 10 }
}

object DUELING : Effect {
    override val isRoll: Boolean = false
    override val func: (() -> Int) -> Int = { it() + 2 }
}

class TwoWeaponFighting(character: Character) : Effect {
    override val isRoll: Boolean = false
    override val func: (() -> Int) -> Int = { it() + character.abilityModifier() }
}
