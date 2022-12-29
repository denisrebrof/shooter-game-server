package com.denisrebrof.springboottest.commands.domain

import com.denisrebrof.springboottest.commands.domain.model.Notification
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.PublishProcessor
import org.springframework.stereotype.Service

@Service
class NotificationsRepository {
    private val notificationProcessor = PublishProcessor.create<Notification>()

    val requests: Flowable<Notification> = notificationProcessor

    fun add(data: Notification) = notificationProcessor.onNext(data)
}