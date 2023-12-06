package com.denisrebrof.simplestats.domain

interface ISimpleStatsReceiver {
    fun addLog(content: String)

    fun setProperty(name: String, property: ISimpleStatsProperty)
    fun setProperty(name: String, propertyProvider: () -> ISimpleStatsProperty)
}

interface ISimpleStatsProperty {
    fun getContent(): String
}

fun ISimpleStatsReceiver.setPropertyString(
    name: String,
    propertyValueProvider: () -> String
) = object : ISimpleStatsProperty {
    override fun getContent(): String = propertyValueProvider.invoke()
}.let { this.setProperty(name, it) }