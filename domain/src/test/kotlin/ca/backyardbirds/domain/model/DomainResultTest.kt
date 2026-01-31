package ca.backyardbirds.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DomainResultTest {
    @Test
    fun success_holdsData() {
        val data = listOf("item1", "item2")
        val result: DomainResult<List<String>> = DomainResult.Success(data)

        assertTrue(result is DomainResult.Success)
        assertEquals(data, result.data)
    }

    @Test
    fun failure_holdsMessage() {
        val result: DomainResult<Nothing> = DomainResult.Failure("something went wrong")

        assertTrue(result is DomainResult.Failure)
        assertEquals("something went wrong", result.message)
    }

    @Test
    fun failure_holdsCause() {
        val cause = RuntimeException("root cause")
        val result = DomainResult.Failure("error", cause = cause)

        assertEquals(cause, result.cause)
    }

    @Test
    fun failure_causeIsNullByDefault() {
        val result = DomainResult.Failure("error")

        assertNull(result.cause)
    }
}
