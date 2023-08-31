import arrow.optics.Copy
import arrow.optics.copy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.processors.BehaviorProcessor
import io.reactivex.rxjava3.processors.PublishProcessor

open class MVIGameHandler<STATE : Any, INTENT : Any, ACTION : Any> private constructor(
    protected val lifecycle: GameLifecycle<STATE>,
    initialState: STATE,
) : IGameLifecycle<STATE> by lifecycle, Disposable by lifecycle {

    constructor(initialState: STATE) : this(
        lifecycle = GameLifecycle<STATE>(initialState),
        initialState = initialState,
    )

    private val stateProcessor = BehaviorProcessor.createDefault(initialState)
    private val actionProcessor = PublishProcessor.create<ACTION>()

    private val stateValue: STATE
        get() = stateProcessor.value!!

    val state: Flowable<STATE>
        get() = stateProcessor

    val actions: Flowable<ACTION>
        get() = actionProcessor

    fun submit(intent: INTENT) = onIntentReceived(intent, stateValue)

    open fun onIntentReceived(intent: INTENT, state: STATE) = Unit

    override fun setState(state: STATE) = state
        .also(lifecycle::setState)
        .let(stateProcessor::onNext)

    protected fun send(action: ACTION) = actionProcessor.onNext(action)

    protected fun <INNER: STATE> INNER.copyAndSet(copy: Copy<INNER>.() -> Unit) = copy(copy).let(::setState)
}