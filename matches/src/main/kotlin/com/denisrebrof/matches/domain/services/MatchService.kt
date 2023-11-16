package com.denisrebrof.matches.domain.services

import com.denisrebrof.matches.domain.model.Match
import org.springframework.stereotype.Service
import java.util.*

@Service
class MatchService(
    private val listeners: List<IMatchServiceListener>,
) {
    private val matches = Collections.synchronizedMap(mutableMapOf<String, Match>())
    private val participantIdToMatchId = Collections.synchronizedMap(mutableMapOf<Long, String>())

    fun create(participants: Set<Long>) {
        val matchId = UUID.randomUUID().toString()
        if (matches.containsKey(matchId))
            return

        val time = Date().time
        val match = Match(matchId, participants, time)
        matches[matchId] = match
        participants.forEach { participantIdToMatchId[it] = matchId }
        listeners.forEach { listener -> listener.onMatchStarted(match) }
    }

    fun remove(matchId: String) {
        val match = matches.remove(matchId) ?: return
        match.participants.forEach(participantIdToMatchId::remove)
        listeners.forEach { listener -> listener.onMatchFinished(match) }
    }

    fun getMatches(): List<Match> = matches.values.toList()

    fun get(matchId: String) = matches[matchId]

    fun getByUserId(userID: Long) = participantIdToMatchId[userID]

    fun hasByUserId(userID: Long): Boolean = participantIdToMatchId.containsKey(userID)

    fun addUsers(matchId: String, vararg userIds: Long) {
        val match = matches[matchId] ?: return
        val newParticipants = userIds.filterNot(::hasByUserId)
        val matchParticipants = match.participants.toMutableSet()
        matchParticipants.addAll(newParticipants)
        matches[matchId] = match.copy(participants = matchParticipants)
        newParticipants.forEach { participantIdToMatchId[it] = matchId }
        val newParticipantsArray = newParticipants.toLongArray()
        listeners.forEach { listener -> listener.onJoined(match, *newParticipantsArray) }
    }

    fun removeUser(matchId: String, vararg userIds: Long) {
        val match = matches[matchId] ?: return
        val existingParticipants = userIds.filter(::hasByUserId)
        val matchParticipants = match.participants.toMutableSet()
        matchParticipants.removeAll(existingParticipants.toSet())
        existingParticipants.forEach(participantIdToMatchId::remove)

        if (matchParticipants.isEmpty())
            return remove(matchId)

        val newMatch = match.copy(participants = matchParticipants)
        matches[matchId] = newMatch
        val existingParticipantsArray = existingParticipants.toLongArray()
        listeners.forEach { listener -> listener.onLeft(match, *existingParticipantsArray) }
    }

    companion object {
        val MAX_PARTICIPANTS = 16
    }
}