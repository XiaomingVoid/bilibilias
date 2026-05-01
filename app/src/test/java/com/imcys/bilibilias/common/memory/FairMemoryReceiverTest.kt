package com.imcys.bilibilias.common.memory

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FairMemoryReceiverTest {

    @Test
    fun intentActionShouldTakePriorityOverBundleAction() {
        val resolved = FairMemoryReceiver.resolveFairMemoryAction(
            intentAction = "itgsa.intent.action.KILL",
            bundleAction = "trim",
        )

        assertEquals(FairMemoryReceiver.ACTION_KILL, resolved)
    }

    @Test
    fun bundleActionShouldBeUsedAsFallback() {
        val resolved = FairMemoryReceiver.resolveFairMemoryAction(
            intentAction = null,
            bundleAction = " kill ",
        )

        assertEquals(FairMemoryReceiver.ACTION_KILL, resolved)
    }

    @Test
    fun unknownActionsShouldReturnNull() {
        val resolved = FairMemoryReceiver.resolveFairMemoryAction(
            intentAction = "custom.intent.UNKNOWN",
            bundleAction = "noop",
        )

        assertNull(resolved)
    }
}
