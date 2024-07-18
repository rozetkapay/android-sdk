package com.rozetkapay.sdk.domain.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class ResultUseCase<in P, R> {

    operator fun invoke(params: P): Flow<R> = flow {
        emit(doWork(params))
    }

    suspend fun executeSync(params: P): R = doWork(params)

    protected abstract suspend fun doWork(params: P): R
}

fun <R> ResultUseCase<Any?, R>.invoke(): Flow<R> {
    return this.invoke(null)
}