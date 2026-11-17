package com.crootagent.mobile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.crootagent.mobile.data.repository.AgentRepository
import com.crootagent.mobile.ui.viewmodel.AgentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AgentViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: AgentRepository
    private lateinit var viewModel: AgentViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = AgentRepository(
            provider = "mock",
            activeModel = "gemini-1.5-flash",
            ioDispatcher = testDispatcher
        )
        viewModel = AgentViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialMessageLoaded() {
        val messages = viewModel.messages.value
        assertNotNull(messages)
        assertEquals(1, messages?.size)
        assertEquals("assistant", messages?.first()?.role)
        assertTrue(messages?.first()?.content?.contains("CRootAgent") == true)
    }

    @Test
    fun testSendMessageAppendsUserAndAssistant() = runTest(testDispatcher) {
        viewModel.sendMessage("Halo CRootAgent")

        // Immediately after sending, user message is present
        var messages = viewModel.messages.value
        assertEquals(2, messages?.size)
        assertEquals("user", messages?.get(1)?.role)
        assertEquals("Halo CRootAgent", messages?.get(1)?.content)

        // Advance coroutines to process response
        advanceUntilIdle()

        messages = viewModel.messages.value
        assertEquals(3, messages?.size)
        assertEquals("assistant", messages?.get(2)?.role)
    }

    @Test
    fun testQuickCommandGitStatus() = runTest(testDispatcher) {
        viewModel.executeQuickCommand("git status")
        advanceUntilIdle()

        val messages = viewModel.messages.value
        assertNotNull(messages)
        val lastMessage = messages?.last()
        assertEquals("assistant", lastMessage?.role)
        assertTrue(lastMessage?.content?.contains("git status") == true)
    }

    @Test
    fun testUpdateConfig() {
        viewModel.updateConfig("groq", "llama-3.3-70b-versatile", "fake-key", "https://api.groq.com/openai/v1/")
        assertEquals("groq", viewModel.provider.value)
        assertEquals("llama-3.3-70b-versatile", viewModel.activeModel.value)
    }
}
