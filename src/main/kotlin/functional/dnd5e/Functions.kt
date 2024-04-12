package functional.dnd5e

import functional.dnd5e.data.DieRoll
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

val random = Random(System.currentTimeMillis())

fun roll(n: Int) = random.nextInt(n) + 1

fun attackRoll(): () -> Int = { roll(20) }
fun noAdvantage(hitRoll: () -> Int) = hitRoll()
fun advantage(hitRoll: () -> Int) = max(hitRoll(), hitRoll())
fun superAdvantage(hitRoll: () -> Int) = max(hitRoll(), max(hitRoll(), hitRoll()))
fun disadvantage(hitRoll: () -> Int) = min(hitRoll(), hitRoll())
fun greatWeaponsFighting(damageRoll: () -> Int) = damageRoll().takeIf { it > 2 } ?: damageRoll()

fun clamp(actual: Int, lo: Int, hi: Int) = max(lo, min(hi, actual))

/** https://roll20.net/compendium/dnd5e/Rules:Ability%20Scores?expansion=0#toc_1 */
fun modifierScaling(stat: Int) = clamp(stat, 1, 30).let { (it / 2) - 5 }

/** https://roll20.net/compendium/dnd5e/Character%20Advancement#toc_1 */
fun proficiencyScaling(lvl: Int) = when (clamp(lvl, 1, 20)) {
    in 1..4 -> 2
    in 5..8 -> 3
    in 9..12 -> 4
    in 13..16 -> 5
    in 17..20 -> 6
    else -> throw IllegalArgumentException("invalid level $lvl")
}

infix fun Int.d(size:Int) = DieRoll(this, size)
