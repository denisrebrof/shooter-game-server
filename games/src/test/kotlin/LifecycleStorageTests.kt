import io.reactivex.rxjava3.disposables.Disposable
import org.junit.jupiter.api.Test

class LifecycleStorageTests {

    private enum class TestState {
        State1,
        State2
    }

    @Test
    fun testStorageMainLifecycleWorksCorrectly() {
        val storage = LifecycleStorage<TestState>()
        val subscription = Disposable.empty()
        storage.add(subscription)
        storage.dispose()

        assert(subscription.isDisposed)
    }

    @Test
    fun testStoragePeriodLifecycleWorksCorrectly() {
        val storage = LifecycleStorage<TestState>()
        val stateOneSubscription = Disposable.empty()
        val stateOnePeriod = StateEqualsLifecyclePeriod(TestState.State1)
        storage.add(stateOneSubscription, stateOnePeriod)

        assert(!stateOneSubscription.isDisposed)

        storage.update(TestState.State2)

        assert(stateOneSubscription.isDisposed)
    }
}