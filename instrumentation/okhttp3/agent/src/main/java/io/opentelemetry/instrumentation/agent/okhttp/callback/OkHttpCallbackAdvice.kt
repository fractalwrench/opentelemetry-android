/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.agent.okhttp.callback

import io.opentelemetry.context.Context
import io.opentelemetry.instrumentation.library.okhttp.internal.OkHttpCallbackAdviceHelper
import io.opentelemetry.instrumentation.library.okhttp.internal.TracingCallback
import net.bytebuddy.asm.Advice
import net.bytebuddy.implementation.bytecode.assign.Assigner
import okhttp3.Call
import okhttp3.Callback

internal object OkHttpCallbackAdvice {

    @JvmStatic
    @Advice.OnMethodEnter
    @Advice.AssignReturned.ToArguments(
        Advice.AssignReturned.ToArguments.ToArgument(value = 0, typing = Assigner.Typing.DYNAMIC),
    )
    fun enter(
        @Advice.This call: Call,
        @Advice.Argument(0) callback: Callback,
    ): Callback =
        if (OkHttpCallbackAdviceHelper.propagateContext(call)) {
            TracingCallback(callback, Context.current())
        } else {
            callback
        }
}
