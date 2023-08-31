import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.*

class LifecycleStorage<STATE : Any> : Disposable {

    private var disposed: Boolean = false

    private val composite = CompositeDisposable()

    private val lifecyclePeriodSubscriptions = LinkedList<LifecyclePeriodHandler<STATE>>()

    fun update(state: STATE) {
        if (disposed)
            return

        val iterator = lifecyclePeriodSubscriptions.iterator()
        while (iterator.hasNext()) {
            val handler = iterator.next()
            if (handler.period.contains(state))
                continue

            handler
                .disposable
                .also(composite::remove)
                .dispose()
            iterator.remove()
        }
    }

    fun add(
        disposable: Disposable,
        lifecycle: LifecyclePeriod<STATE>? = null
    ): Boolean {
        if (disposed) {
            disposable.dispose()
            return false
        }

        composite.add(disposable)
        if (lifecycle == null)
            return true

        LifecyclePeriodHandler(disposable, lifecycle).let(lifecyclePeriodSubscriptions::add)
        return true
    }

    override fun dispose() {
        composite.dispose()
        lifecyclePeriodSubscriptions.clear()
        disposed = true
    }

    override fun isDisposed(): Boolean = disposed

    private class LifecyclePeriodHandler<STATE : Any>(
        val disposable: Disposable,
        val period: LifecyclePeriod<STATE>
    )
}