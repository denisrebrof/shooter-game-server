package com.denisrebrof.springboottest.hideandseekgame.gateways

import com.denisrebrof.springboottest.utils.DisposableService
import io.reactivex.rxjava3.disposables.Disposable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RemoveLeftPlayerFromGameHandler @Autowired constructor(

) : DisposableService() {
    override val handler: Disposable = Disposable.disposed() //TODO
}