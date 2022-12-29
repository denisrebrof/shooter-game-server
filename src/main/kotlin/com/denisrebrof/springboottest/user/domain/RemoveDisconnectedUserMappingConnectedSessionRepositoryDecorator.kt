package com.denisrebrof.springboottest.user.domain

import com.denisrebrof.springboottest.session.domain.IWSConnectedSessionRepository
import com.denisrebrof.springboottest.user.domain.repositories.IWSUserSessionMappingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class RemoveDisconnectedUserMappingConnectedSessionRepositoryDecorator @Autowired constructor(
    @Qualifier("Base")
    private val target: IWSConnectedSessionRepository,
    private val userSessionMappingRepository: IWSUserSessionMappingRepository
) : IWSConnectedSessionRepository by target {
    override fun removeSession(sessionId: String) {
        userSessionMappingRepository.removeMapping(sessionId)
        target.removeSession(sessionId)
    }
}