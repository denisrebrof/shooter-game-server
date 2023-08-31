import io.reactivex.rxjava3.disposables.Disposable
import kotlin.reflect.KClass

fun <STATE : Any> IGameLifecycle<STATE>.addToPeriod(
    disposable: Disposable,
    containsCheck: (STATE) -> Boolean
) = add(
    disposable = disposable,
    lifecycle = object : LifecyclePeriod<STATE> {
        override fun contains(state: STATE): Boolean = containsCheck(state)
    }
)

inline fun <STATE : Any, reified DERIVED_STATE : STATE> IGameLifecycle<STATE>.addToPeriodTyped(
    disposable: Disposable,
    crossinline containsCheck: (DERIVED_STATE) -> Boolean
) = add(
    disposable = disposable,
    lifecycle = object : LifecyclePeriod<STATE> {
        override fun contains(state: STATE): Boolean {
            val typedState = state as? DERIVED_STATE ?: return false
            return containsCheck(typedState)
        }
    }
)

fun <STATE : Any> IGameLifecycle<STATE>.addToStateType(
    disposable: Disposable,
    stateType: KClass<out STATE>
) = add(
    disposable = disposable,
    lifecycle = StateTypeLifecyclePeriod(stateType)
)

fun <STATE : Any> IGameLifecycle<STATE>.addToStateEquals(
    disposable: Disposable,
    lifecycleState: STATE
) = add(
    disposable = disposable,
    lifecycle = StateEqualsLifecyclePeriod(lifecycleState)
)