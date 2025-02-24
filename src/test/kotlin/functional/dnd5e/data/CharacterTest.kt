package functional.dnd5e.data

import functional.dnd5e.advantage
import functional.dnd5e.disadvantage
import functional.dnd5e.noAdvantage
import functional.dnd5e.superAdvantage

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach

class CharacterTest {

    private var character: Character? = null
    private val trials = 100_000
    private val mainArm = AttackArm(Weapon.GREAT_SWORD, Stat.STR)

    @BeforeEach
    fun setup() {
        character = Character(Stats(), 1)
    }

    // TODO test with lvl scaling
    // TODO test with stats scaling
    @Test
    fun rollAttack() {
        val attackState = character!!.equip(mainArm)

        val normal = Array(trials) {
            attackState.checkAttackOnly(::noAdvantage).rollWithBonuses().invoke()
        }.average()
        val advantage = Array(trials) {
            attackState.checkAttackOnly(::advantage).rollWithBonuses().invoke()
        }.average()
        val disadvantage = Array(trials) {
            attackState.checkAttackOnly(::disadvantage).rollWithBonuses().invoke()
        }.average()
        val superAdvantage = Array(trials) {
            attackState.checkAttackOnly(::superAdvantage).rollWithBonuses().invoke()
        }.average()

        println("normal: $normal")
        println("advantage: $advantage")
        println("disadvantage: $disadvantage")
        println("superAdvantage: $superAdvantage")

        assert(normal in 12.0..13.0) {
            "expected: [min: 12.0, max: 13.0] ~> actual: $normal"
        }
        assert(advantage in 15.0..16.0) {
            "expected: [min: 15.0, max: 16.0] ~> actual: $advantage"
        }
        assert(disadvantage in 9.0..10.0) {
            "expected: [min: 9.0, max: 10.0] ~> actual: $disadvantage"
        }
        assert(superAdvantage in 17.0..18.0) {
            "expected: [min: 17.0, max: 18.0] ~> actual: $superAdvantage"
        }
    }

    // TODO test with lvl scaling
    // TODO test with stats scaling
    // TODO test with multi wpn roll
    // TODO test with flat mods
    // TODO test with attk mods
    // TODO test flat mods will apply
    // TODO test flat mods will NOT apply
    // TODO test attk mods will apply
    // TODO test attk mods will NOT apply
    @Test
    fun rollDamage() {
        val attackState = character!!.equip(mainArm)
        val dmg = Array(trials) { attackState.checkDamageOnly() }.average()
        println("dmg ~> $dmg")

        assert(dmg in 6.5..8.0) {
            "expected: [6.5, 8.0] ~> actual: $dmg"
        }
    }

    @Test
    fun rollDamage_GWF() {
        character!!.fightingStyles.add(FightingStyles.GREAT_WEAPON_FIGHTING)

        val attackState = character!!.equip(mainArm)
        val dmgGWF = Array(trials) { attackState.checkDamageOnly() }.average()
        println("dmgGWF ~> $dmgGWF")

        assert(dmgGWF in 8.0..9.0) {
            "expected: [8.0, 9.0] ~> actual: $dmgGWF"
        }
    }

    @Test
    fun rollDamage_GWM() {
        character!!.feats.add(Feat.GREAT_WEAPON_MASTER)

        val attackState = character!!.equip(mainArm)
        val dmgGWM = Array(trials) { attackState.checkDamageOnly() }.average()
        println("dmgGWM ~> $dmgGWM")

        assert(dmgGWM in 16.5..18.0) {
            "expected: [16.5, 18.0] ~> actual: $dmgGWM"
        }
    }

    @Test
    fun rollDamage_MaxAttackStat() {
        character!!.stats = character!!.stats.copy(str = 20)

        val attackState = character!!.equip(mainArm)
        val dmg = Array(trials) { attackState.checkDamageOnly() }.average()
        println("dmgMaxAttackStat ~> $dmg")

        assert(dmg in 11.5..13.0) {
            "expected: [11.5, 13.0] ~> actual: $dmg"
        }
    }

}