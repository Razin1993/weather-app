package com.razin.weather.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.razin.weather.R
import com.razin.weather.databinding.FragmentSettingsBinding
import com.razin.weather.ui.uiUtils.ThemeHandler

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners(){
        sharedPreferences = requireContext().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)

        val selectedTheme = sharedPreferences.getString("theme", "system_default")
        when (selectedTheme) {
            "system_default" -> binding.rbSystemDefault.isChecked = true
            "light" -> binding.rbLight.isChecked = true
            "dark" -> binding.rbDark.isChecked = true
        }

        binding.ivBack.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.rgTheme.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbSystemDefault -> {
                    saveTheme("system_default")
                }

                R.id.rbLight -> {
                    saveTheme("light")
                }

                R.id.rbDark -> {
                    saveTheme("dark")
                }
            }
        }

    }

    private fun saveTheme(theme: String){
        sharedPreferences.edit().putString("theme", theme).apply()
        ThemeHandler.handleTheme(requireContext())
        requireActivity().recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            val navController = findNavController()
            navController.popBackStack()
            navController.navigate(R.id.homeFragment,null,null)
        }
    }

}