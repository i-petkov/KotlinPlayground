package functional.dnd5e

import functional.dnd5e.data.Effect

fun List<Effect>.composeEffectsOnToAttack(index: Int = 0, attack: () -> Int): () -> Int =
    if (index in indices) {
        composeEffectsOnToAttack(index + 1, attack = { get(index).func(attack) })
    } else {
        attack
    }

fun Map<Boolean, List<Effect>>.getOrEmpty(key: Boolean) = getOrDefault(key, emptyList())

/** up to 8 times... */
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

fun <R> (() -> R).with(f: (() -> R) -> R): () -> R {
    return { f(this) }
}

fun (() -> Int).adding(f: () -> Int): () -> Int {
    return { this() + f() }
}