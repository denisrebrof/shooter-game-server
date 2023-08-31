import io.reactivex.rxjava3.disposables.Disposable

open class GameLifecycle<STATE : Any> private constructor(
    private val storage: LifecycleStorage<STATE>,
    initialState: STATE,
) : IGameLifecycle<STATE>, Disposable by storage {

    constructor(initialState: STATE) : this(
        storage = LifecycleStorage<STATE>(),
        initialState = initialState,
    )

    private val lifecycles = LifecycleStorage<STATE>()

    private var lastState = initialState

    override fun setState(state: STATE) = state
        .also(lifecycles::update)
        .let(::lastState::set)

    override fun add(
        disposable: Disposable,
        lifecycle: LifecyclePeriod<STATE>?
    ): Boolean {
        val matchPeriod = lifecycle == null || lifecycle.contains(lastState)
        if (matchPeriod)
            return lifecycles.add(disposable, lifecycle)

        disposable.dispose()
        return false
    }
}

interface IGameLifecycle<STATE: Any> {
    fun setState(state: STATE)
    fun add(disposable: Disposable, lifecycle: LifecyclePeriod<STATE>?): Boolean
}