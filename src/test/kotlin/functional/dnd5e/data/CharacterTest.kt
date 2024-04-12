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

    @BeforeEach
    fun setup() {
        character = Character(Stats(), 1, Stat.CHARISMA)
    }

    // TODO test with lvl scaling
    // TODO test with stats scaling
    @Test
    fun rollAttack() {
        (character as Character).let { chrctr ->
            val normal = Array(trials) { chrctr.attackRoll(::noAdvantage) }.average()
            val advantage = Array(trials) { chrctr.attackRoll(::advantage) }.average()
            val disadvantage = Array(trials) { chrctr.attackRoll(::disadvantage) }.average()
            val superAdvantage = Array(trials) { chrctr.attackRoll(::superAdvantage) }.average()

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
        val dmg = Array(trials) { character!!.rollDamage(Weapon.GREAT_SWORD) }.average()
        println("dmg ~> $dmg")

        assert(dmg in 6.5..8.0) {
            "expected: [6.5, 8.0] ~> actual: $dmg"
        }
    }

    @Test
    fun rollDamage_GWF() {
        character!!.fightingStyles.add(FightingStyles.GREAT_WEAPON_FIGHTING)

        val dmgGWF = Array(trials) { character!!.rollDamage(Weapon.GREAT_SWORD) }.average()
        println("dmgGWF ~> $dmgGWF")

        assert(dmgGWF in 8.0..9.0) {
            "expected: [8.0, 9.0] ~> actual: $dmgGWF"
        }
    }

    @Test
    fun rollDamage_GWM() {
        character!!.feats.add(Feat.GREAT_WEAPON_MASTER)

        val dmgGWM = Array(trials) { character!!.rollDamage(Weapon.GREAT_SWORD) }.average()
        println("dmgGWM ~> $dmgGWM")

        assert(dmgGWM in 16.5..18.0) {
            "expected: [16.5, 18.0] ~> actual: $dmgGWM"
        }
    }

    @Test
    fun rollDamage_MaxAttackStat() {
        character!!.stats = character!!.stats.copy(charisma = 20)

        val dmg = Array(trials) { character!!.rollDamage(Weapon.GREAT_SWORD) }.average()
        println("dmgMaxAttackStat ~> $dmg")

        assert(dmg in 11.5..13.0) {
            "expected: [11.5, 13.0] ~> actual: $dmg"
        }
    }

}