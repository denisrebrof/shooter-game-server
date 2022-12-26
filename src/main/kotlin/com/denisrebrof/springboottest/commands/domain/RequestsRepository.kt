package com.denisrebrof.springboottest.commands.domain

import com.denisrebrof.springboottest.commands.domain.model.RequestData
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.PublishProcessor
import org.springframework.stereotype.Service

@Service
class RequestsRepository {
    private val requestsProcessor = PublishProcessor.create<RequestData>()

    val requests: Flowable<RequestData> = requestsProcessor

    fun add(data: RequestData) = requestsProcessor.onNext(data)
}