package com.kotlinconf.workshop.kettle.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// Task: Implement a custom averageOfLast flow operator
fun Flow<Double>.averageOfLast(n: Int): Flow<Double> = flow {
    val buffer = ArrayDeque<Double>(n)
    collect { value ->
        buffer.add(value)
        println("Adding $value to buffer")
        if (buffer.size == n) {
            println("Buffer full")
            val average = buffer.average()
            println("Emitting average $average")
            emit(average)
            buffer.removeFirst()
        }
    }
}
