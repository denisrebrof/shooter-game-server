import com.denisrebrof.springboottest.collisions.Collider
import com.denisrebrof.springboottest.collisions.PhysicsSystem
import org.junit.jupiter.api.Test
import java.util.*

class PhysicsSystemPerformanceTest {

    private val r = Random()

    @Test
    fun compareTime() {
        val defTime = getDefaultCalcTime()
        val impTime = getImprovedCalcTime()
        assert(defTime > impTime)
    }

    private fun getImprovedCalcTime(): Long {
        val static = generateRandomColliders(2000, 20.0)
        val dynamic = generateRandomColliders(2000, 20.0)

        val dynamicMap = dynamic
                .mapIndexed { index, collider -> index.toLong() to collider }
                .toMap()

        val system = CollidersMap(static).let(::PhysicsSystem)
        system.set(dynamicMap)

        val startTime = System.currentTimeMillis()

        for (id in dynamicMap.keys) {
            system.move(id, 1.0, 0.0)
        }

        val endTime = System.currentTimeMillis()
        return endTime - startTime
    }

    private fun getDefaultCalcTime(): Long {
        val static = generateRandomColliders(2000, 20.0)
        val dynamic = generateRandomColliders(2000, 20.0)

        val staticMap = static
                .mapIndexed { index, collider -> -(index - 1).toLong() to collider }
                .toMap()

        val dynamicMap = dynamic
                .mapIndexed { index, collider -> index.toLong() to collider }
                .toMap()

        val all = staticMap + dynamicMap

        val startTime = System.currentTimeMillis()
        for ((id, collider) in dynamicMap) {
            val collides = all.any { (otherId, otherCollider) -> id != otherId && otherCollider.collides(collider) }
            if (collides)
                continue

            collider.x += 1
        }
        val endTime = System.currentTimeMillis()
        return endTime - startTime
    }

    private fun generateRandomColliders(count: Int, mapSize: Double): List<Collider> {
        return (0..count).map { generateRandomCollider(mapSize) }
    }

    private fun generateRandomCollider(mapSize: Double): Collider {
        val isCircle = r.nextDouble() > 0.5
        return when {
            isCircle -> Collider.Circle(
                    r.nextDouble() * mapSize,
                    r.nextDouble() * mapSize,
                    1.0
            )

            else -> Collider.Box(
                    r.nextDouble() * mapSize,
                    r.nextDouble() * mapSize,
                    1.0,
                    1.0
            )
        }
    }
}