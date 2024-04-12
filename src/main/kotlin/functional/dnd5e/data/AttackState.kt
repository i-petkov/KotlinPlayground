package functional.dnd5e.data

import functional.dnd5e.*

// TODO weapon validations before attack
data class AttackState(
    val character: Character,
    val mainHand: Arm = NotEquipped,
    val offHand: Arm = NotEquipped
) {
    val isDualWielding = ((mainHand as? AttackArm)?.canDualWield == true) &&
            ((offHand as? AttackArm)?.canDualWield == true)

    fun attack(opponentAttackClass: Int, advantage: (() -> Int) -> Int): Int {
        val arm = mainHand as AttackArm
        val effects = character.mapEffects(arm)

        return Just(prepareAttack(character, arm, advantage))
            .fmap { checkSuccess(it, opponentAttackClass) }
            .map { calculateDamage(it, arm, effects) }
            .getOr(0)
    }

    fun checkAttackOnly(advantage: (() -> Int) -> Int): Attack {
        val arm = mainHand as AttackArm
        return prepareAttack(character, arm, advantage)
    }

    fun checkDamageOnly(): Int {
        val arm = mainHand as AttackArm
        val effects = character.mapEffects(arm)
        return calculateDamage(false, arm, effects)
    }

    private fun prepareAttack(character: Character, arms: AttackArm, advantage: (()->Int)->Int): Attack {
        val naturalRoll = attackRoll().with(advantage)
        return Attack(naturalRoll(), { character.proficiencyModifier() }, { character.abilityModifier(arms) })
    }

    private fun checkSuccess(attack: Attack, opponentAttackClass: Int): Monad<Boolean> {
        val rollWithBonuses = attack.rollWithBonuses().invoke()
        return when {
            attack.naturalRoll == 20 -> Just(true)
            attack.naturalRoll == 1 -> Nothing()
            rollWithBonuses >= opponentAttackClass -> Just(false)
            else -> Nothing()
        }
    }

    private fun calculateDamage(crit: Boolean, arm: AttackArm, effects: List<Effect>): Int {
        // TODO https://rpg.stackexchange.com/questions/131993/does-dual-wielding-count-as-two-attacks-and-therefore-two-actions
        //  DW makes a second attack as a bonus action -> second attack roll + second dmg roll (feats determine if you add ability modifier to second dmg roll)
        val gr = effects
            .filter { effect -> arm.isLegalFor(effect) }
            .groupBy { it.isRoll }

        val baseAttack = { roll(arm.weapon.weaponDie.rollSize) }

        val rollModifiers = gr.getOrEmpty(true)
        val damageModifiers = gr.getOrEmpty(false)

        val rollRepetitions = if(crit) {
            arm.weapon.weaponDie.rollNumber * 2
        } else {
            arm.weapon.weaponDie.rollNumber
        }

        return rollModifiers.composeEffectsOnToAttack(attack = baseAttack)
            .repeat(rollRepetitions)
            .let { damageModifiers.composeEffectsOnToAttack(attack = it) }
            .let { it() }
    }
}