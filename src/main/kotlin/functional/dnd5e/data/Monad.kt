package functional.dnd5e.data

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