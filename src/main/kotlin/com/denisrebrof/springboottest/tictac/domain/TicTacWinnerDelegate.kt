package com.denisrebrof.springboottest.tictac.domain

object TicTacWinnerDelegate {
    fun getWinnerId(cellIds: List<Long>, side: Int): Long? {
        val rows = cellIds.chunked(side)

        val sideRange = (0 until side)

        val columns = rows
            .map { row -> row.mapIndexed { index, item -> index to item } }
            .flatten()
            .groupBy(Pair<Int, Long>::first)
            .mapValues { it.value.map { (_, item) -> item } }
            .values
            .toList()

        val topLeftBottomRightDiagonal = sideRange.mapNotNull { index ->
            rows.getOrNull(index)?.getOrNull(index)
        }

        val bottomLeftTopRightDiagonal = sideRange.mapNotNull { index ->
            rows.getOrNull(side - 1 - index)?.getOrNull(index)
        }

        val diagonals = listOf(topLeftBottomRightDiagonal, bottomLeftTopRightDiagonal)
        val lines = rows + columns + diagonals
        return lines.firstNotNullOfOrNull(::getLineWinnerId)
    }

    private fun getLineWinnerId(line: List<Long>): Long? {
        val firstId = line.firstOrNull() ?: return null
        val returnFirstID = line.all(firstId::equals) && firstId != 0L
        return when {
            returnFirstID -> firstId
            else -> null
        }
    }
}