import kotlin.reflect.KClass

interface LifecyclePeriod<STATE : Any> {
    fun contains(state: STATE): Boolean
}

class StateEqualsLifecyclePeriod<STATE : Any>(private val state: STATE) : LifecyclePeriod<STATE> {
    override fun contains(state: STATE): Boolean = this.state == state
}

class StateTypeLifecyclePeriod<STATE : Any>(private val stateType: KClass<out STATE>) : LifecyclePeriod<STATE> {
    override fun contains(state: STATE): Boolean = stateType == state::class
}