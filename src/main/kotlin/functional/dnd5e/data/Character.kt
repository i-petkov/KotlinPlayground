package functional.dnd5e.data

import functional.dnd5e.*

class Character(
    var stats: Stats,
    var level: Int
) {
    val feats: HashSet<Feat> = HashSet()
    val fightingStyles: HashSet<FightingStyles> = HashSet()

    fun equip(mainHand: AttackArm, offHand: Arm = NotEquipped): AttackState {
        return AttackState(this, mainHand, offHand)
    }

    fun proficiencyModifier() = proficiencyScaling(level)

    fun abilityModifier(arms: AttackArm) = stats.modifier(arms.attackingStat)

    fun mapEffects(arms: AttackArm): List<Effect> {
        val addAbilityModifier = arms.isMainHand || fightingStyles.contains(FightingStyles.TWO_WEAPON_FIGHTING)

        val featEffects: List<Effect> = feats.map {
            when(it) {
                Feat.GREAT_WEAPON_MASTER -> GreatWeaponsMaster
                Feat.SHARPSHOOTER -> Sharpshooter
            }
        }

        val fightingStylesEffects: List<Effect> = fightingStyles.mapNotNull {
            when(it) {
                FightingStyles.GREAT_WEAPON_FIGHTING -> GreatWeaponFighting
                FightingStyles.DUELING -> Dueling
                FightingStyles.TWO_WEAPON_FIGHTING -> null
            }
        }

        return if (addAbilityModifier) {
            featEffects + fightingStylesEffects + AbilityModifier { it() + abilityModifier(arms) }
        } else {
            featEffects + fightingStylesEffects
        }
    }
}