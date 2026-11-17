package com.crootagent.mobile

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.crootagent.mobile.databinding.ActivityMainBinding
import com.crootagent.mobile.ui.adapter.ChatAdapter
import com.crootagent.mobile.ui.viewmodel.AgentViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: AgentViewModel by viewModels()
    private val chatAdapter = ChatAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadSavedPreferences()
        setupRecyclerView()
        setupListeners()
        setupObservers()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.rvMessages.layoutManager = layoutManager
        binding.rvMessages.adapter = chatAdapter
    }

    private fun setupListeners() {
        binding.btnSend.setOnClickListener {
            submitMessage()
        }

        binding.etPrompt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                submitMessage()
                true
            } else {
                false
            }
        }

        binding.btnQuickStatus.setOnClickListener {
            viewModel.executeQuickCommand("git status")
        }

        binding.btnQuickTest.setOnClickListener {
            viewModel.executeQuickCommand("cargo test")
        }

        binding.btnQuickModels.setOnClickListener {
            viewModel.executeQuickCommand("crootagent models")
        }

        binding.btnQuickSessions.setOnClickListener {
            viewModel.executeQuickCommand("session list")
        }

        binding.btnSettings.setOnClickListener {
            showSettingsDialog()
        }
    }

    private fun setupObservers() {
        viewModel.messages.observe(this) { messages ->
            chatAdapter.submitList(messages) {
                if (messages.isNotEmpty()) {
                    binding.rvMessages.smoothScrollToPosition(messages.size - 1)
                }
            }
        }

        viewModel.activeModel.observe(this) { model ->
            binding.tvActiveModel.text = model
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnSend.isEnabled = !isLoading
            binding.btnSend.text = if (isLoading) "..." else getString(R.string.btn_send)
        }
    }

    private fun submitMessage() {
        val prompt = binding.etPrompt.text?.toString().orEmpty()
        if (prompt.isNotBlank()) {
            viewModel.sendMessage(prompt)
            binding.etPrompt.text?.clear()
        }
    }

    private fun loadSavedPreferences() {
        val prefs = getSharedPreferences("crootagent_prefs", Context.MODE_PRIVATE)
        val provider = prefs.getString("provider", "gemini") ?: "gemini"
        val model = prefs.getString("model", "gemini-1.5-flash") ?: "gemini-1.5-flash"
        val apiKey = prefs.getString("api_key", "") ?: ""
        val baseUrl = prefs.getString("base_url", "https://api.openai.com/v1/") ?: "https://api.openai.com/v1/"

        viewModel.updateConfig(provider, model, apiKey, baseUrl)
    }

    private fun showSettingsDialog() {
        val prefs = getSharedPreferences("crootagent_prefs", Context.MODE_PRIVATE)
        val currentProvider = prefs.getString("provider", "gemini") ?: "gemini"
        val currentModel = prefs.getString("model", "gemini-1.5-flash") ?: "gemini-1.5-flash"
        val currentApiKey = prefs.getString("api_key", "") ?: ""
        val currentBaseUrl = prefs.getString("base_url", "https://api.openai.com/v1/") ?: "https://api.openai.com/v1/"

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 16)
        }

        val providerSpinner = Spinner(this)
        val providers = arrayOf("gemini", "groq", "openai", "ollama")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, providers)
        providerSpinner.adapter = spinnerAdapter
        val providerIndex = providers.indexOf(currentProvider).coerceAtLeast(0)
        providerSpinner.setSelection(providerIndex)
        layout.addView(providerSpinner)

        val etModel = EditText(this).apply {
            hint = "Model (e.g. gemini-1.5-flash, llama-3.3-70b)"
            setText(currentModel)
        }
        layout.addView(etModel)

        val etApiKey = EditText(this).apply {
            hint = "API Key"
            setText(currentApiKey)
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        layout.addView(etApiKey)

        val etBaseUrl = EditText(this).apply {
            hint = "Base URL (for OpenAI/Groq/Ollama)"
            setText(currentBaseUrl)
        }
        layout.addView(etBaseUrl)

        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_settings)
            .setView(layout)
            .setPositiveButton("Simpan") { _, _ ->
                val selectedProvider = providerSpinner.selectedItem.toString()
                val selectedModel = etModel.text.toString().trim()
                val selectedApiKey = etApiKey.text.toString().trim()
                val selectedBaseUrl = etBaseUrl.text.toString().trim()

                prefs.edit()
                    .putString("provider", selectedProvider)
                    .putString("model", selectedModel)
                    .putString("api_key", selectedApiKey)
                    .putString("base_url", selectedBaseUrl)
                    .apply()

                viewModel.updateConfig(selectedProvider, selectedModel, selectedApiKey, selectedBaseUrl)
                Toast.makeText(this, "Pengaturan berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
