import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class ArrayPerformanceTest {

    @Test
    fun testArrayPerformance() {
        val count = 64 * 1024 * 64
        val intArray = IntArray(count) { 0 }
        val intList = MutableList(count) { 0 }

        val arrayIterationTime = measureTimeMillis {
            for (i in 0 until count)
                intArray[i] += 1
        }

        val iterationStep = 16
        val arrayStepIterationTime = measureTimeMillis {
            for (i in 0 until count step iterationStep)
                intArray[i] += 1
        }

        val listIterationTime = measureTimeMillis {
            for (i in 0 until count)
                intList[i] += 1
        }

        val listIterationStepTime = measureTimeMillis {
            for (i in 0 until count step iterationStep)
                intList[i] += 1
        }

        println("array it time: $arrayIterationTime")
        println("array it (step $iterationStep) time: $arrayStepIterationTime")
        println("list it time: $listIterationTime")
        println("list it (step $iterationStep) time: $listIterationStepTime")
    }
}