package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.TestCaseOrder
import io.kotest.extensions.spring.SpringExtension
import java.util.Locale

object ProjectConfig : AbstractProjectConfig() {
    override val testCaseOrder = TestCaseOrder.Random

    override val specExecutionOrder = SpecExecutionOrder.Random

    override fun extensions() = listOf(SpringExtension)

    override fun beforeAll() {
        Locale.setDefault(Locale.ENGLISH)
    }
}
