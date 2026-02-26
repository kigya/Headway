package base

public class Prop<T>(initial: T) {
    private var v: T = initial
    public fun set(value: T) {
        v = value
    }

    public fun get(): T = v
    override fun toString(): String = v.toString()
}
