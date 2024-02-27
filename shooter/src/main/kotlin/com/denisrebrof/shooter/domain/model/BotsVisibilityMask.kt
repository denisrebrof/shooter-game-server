package com.denisrebrof.shooter.domain.model

import kotlin.math.max
import kotlin.math.min

data class BotsVisibilityMask(
    val ids: Set<Long>,
    private val matrix: List<Boolean>
) {
    private val hash: Int
        get() = ids.hashCode()

    fun getVisibility(
        sourceId: Long,
        targetId: Long
    ): Boolean {
        if (sourceId == targetId)
            return true

        val matrixIndex = getIndex(sourceId, targetId)
        if (matrixIndex < 0)
            return false

        return matrix.getOrElse(matrixIndex) { false }
    }

    fun combine(other: BotsVisibilityMask): BotsVisibilityMask {
        if (other.hash != hash || ids.isEmpty())
            return this

        val resultMatrix = mutableListOf<Boolean>()
        val maxSize = getMatrixSize(ids.size)

        if (maxSize < 1)
            return this

        val indices = max(matrix.lastIndex, other.matrix.lastIndex)
            .coerceAtMost(maxSize - 1)
            .let(0::rangeTo)

        for (i in indices) {
            val originalAssociation = matrix.getOrNull(i) ?: false
            val otherAssociation = other.matrix.getOrNull(i) ?: false
            resultMatrix.add(originalAssociation && otherAssociation)
        }

        return copy(matrix = resultMatrix)
    }

    fun update(new: Set<Long>): BotsVisibilityMask {
        if (ids == new)
            return this

        var result = this

        new
            .filterNot(ids::contains)
            .forEach { result = result.append(it) }

        ids
            .filterNot(new::contains)
            .forEach { result = result.exclude(it) }

        return result
    }

    fun append(id: Long): BotsVisibilityMask {
        if (ids.contains(id))
            return this

        val newIds = ids.plus(id)
        val sourceIndex = newIds.indexOf(id)
        val newAssociations = ids
            .map(newIds::indexOf)
            .map { targetIndex -> getIndex(sourceIndex, targetIndex, newIds.size) }

        val newMatrix = matrix.toMutableList().apply {
            newAssociations.forEach { associationIndex ->
                add(associationIndex, false)
            }
        }
        return copy(
            ids = newIds,
            matrix = newMatrix
        )
    }

    fun exclude(id: Long): BotsVisibilityMask {
        if (!ids.contains(id))
            return this

        val newIds = ids.minus(id)
        val associations = newIds.map { getIndex(it, id) }
        return copy(
            ids = newIds,
            matrix = matrix.filterIndexed { index, _ -> !associations.contains(index) }
        )
    }

    private fun getIndex(
        sourceId: Long,
        targetId: Long
    ): Int = getIndex(
        sourceIndex = ids.indexOf(sourceId),
        targetIndex = ids.indexOf(targetId),
        size = ids.size
    )

    companion object {
        fun empty(ids: Set<Long>) = BotsVisibilityMask(
            ids = ids,
            matrix = emptyList()
        )

        private fun getMatrixSize(idsSize: Int) = when {
            idsSize < 2 -> 0
            else -> (idsSize - 2) * (idsSize + 1) / 2 + 1
        }

        private fun getIndex(
            sourceIndex: Int,
            targetIndex: Int,
            size: Int
        ): Int {
            //If some of requested id's not present
            if (sourceIndex < 0 || targetIndex < 0)
                return -1

            val xIndex = max(sourceIndex, targetIndex)
            val yIndex = min(sourceIndex, targetIndex)

            val skippedCellsCount = yIndex * (yIndex + 1) / 2
            val prevRowsCellCount = size.minus(1) * yIndex
            return xIndex - 1 + prevRowsCellCount - skippedCellsCount
        }
    }
}
