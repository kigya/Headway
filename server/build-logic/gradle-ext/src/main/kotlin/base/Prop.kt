package base

public class Prop<T>(initial: T) {
    private var _value: T = initial
    public fun set(value: T) {
        _value = value
    }

    public fun get(): T = _value
    override fun toString(): String = _value.toString()
}
