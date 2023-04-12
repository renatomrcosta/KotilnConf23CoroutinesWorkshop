package com.kotlinconf.workshop.tasks.cancellation

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

fun main() = runBlocking {
    val myJob = launch {
        while (isActive) {
            try {
                doSomeWorkThatMayFail()
            } catch (e: MyException) {
                println("Oops! ${e.message}")
            }
        }
    }
    delay(2000)
    println("Enough!")
    myJob.cancelAndJoin()
}

suspend fun doSomeWorkThatMayFail() {
    delay(300)
    if (Random.nextBoolean()) {
        println("I'm working...")
    } else {
        throw MyException("Didn't work this time!")
    }
}

class MyException(message: String) : Exception(message)
