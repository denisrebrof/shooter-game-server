import com.denisrebrof.springboottest.collisions.Collider
import com.denisrebrof.springboottest.collisions.PhysicsSystem
import org.junit.jupiter.api.Test

class PhysicsSystemTest {

    private val playerCollider = Collider.Circle(-2.0, 0.0, 1.0)

    private val colliders = listOf(
            Collider.Box(0.0, 0.0, 2.0, 4.0)
    )

    @Test
    fun testPhysicsSystemInitsSuccessfully() {
        val system = CollidersMap(colliders).let(::PhysicsSystem)
        system.set(0, playerCollider)
        assert(system.get(0) != null)
    }

    @Test
    fun testPhysicsSystemMoves() {
        val system = CollidersMap(colliders).let(::PhysicsSystem)
        system.set(0, playerCollider)
        system.move(0, -1.0, 0.0)
        val newPos = system.get(0)?.x
        assert(newPos == -3.0)
    }

    @Test
    fun testPhysicsSystemBlocks() {
        val system = CollidersMap(colliders).let(::PhysicsSystem)
        system.set(0, playerCollider)
        val oldPos = playerCollider.x
        system.move(0, 0.5, 0.0)
        val newPos = system.get(0)?.x
        assert(newPos == oldPos)
    }

    @Test
    fun testPhysicsSystemBlocksInCorrectPos() {
        val system = CollidersMap(colliders).let(::PhysicsSystem)
        playerCollider.x -= 40
        system.set(0, playerCollider)
        var oldPos = playerCollider.x
        var newPos: Double? = null
        while (newPos == null || (oldPos != newPos && newPos < 0.0)) {
            oldPos = playerCollider.x
            system.move(0, 0.15, 0.0)
            newPos = playerCollider.x
        }
        assert(newPos > -2.5 && newPos < 0.0)
    }
}