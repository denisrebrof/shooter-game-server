import kotlinx.serialization.Serializable

data class Test3(val test: String)

@Serializable
data class Transform(
    var x: Float,
    var y: Float,
    var z: Float,
    var r: Float,
) {
    fun isClose(other: Transform, distance: Float): Boolean {
        val squaredDist = other.getSquaredDistanceTo(this)
        return squaredDist <= distance * distance
    }

    private fun getSquaredDistanceTo(other: Transform): Float {
        val distX = other.x - x
        val distY = other.y - y
        val distZ = other.z - z
        return distX * distX + distY * distY + distZ * distZ
    }

    companion object
}