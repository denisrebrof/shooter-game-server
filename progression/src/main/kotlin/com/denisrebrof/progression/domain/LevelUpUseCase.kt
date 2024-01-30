package com.denisrebrof.progression.domain

import com.denisrebrof.progression.domain.repositories.IUserProgressionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service class LevelUpUseCase @Autowired constructor(
    private val progressionRepository: IUserProgressionRepository
) {

}