package com.example.playlistmaker.mediateka.ui.playlists

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.R
import com.example.playlistmaker.config.App.Companion.TAG
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.main.ui.RootActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

open class CreatePlaylistFragment : Fragment() {
    private val viewModel: CreatePlaylistViewModel by viewModel()
    var _binding: FragmentCreatePlaylistBinding? = null
    val binding get() = _binding!!

    var selectedImageUri: Uri? = null
    var coverImagePath: String? = null
    val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                binding.playlistCover.scaleType = ImageView.ScaleType.CENTER_CROP
                binding.playlistCover.setImageURI(uri)
            } else {
                Log.d(TAG, "No image selected")
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentActivity = activity
        if (currentActivity is RootActivity) {
            currentActivity.binding.bottomNavigationView.isVisible = false
        }
        binding.buttonCreate.isEnabled = false

        binding.playlistName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.isNullOrEmpty()) {
                    binding.buttonCreate.isEnabled = false
                } else {
                    binding.buttonCreate.isEnabled = true
                }
            }

        })



        binding.playlistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonCreate.setOnClickListener {
            lifecycleScope.launch {
                val isPlaylistCreated = createPlaylist()
                showCustomToast(
                    getString(
                        when (isPlaylistCreated) {
                            true -> R.string.playlist_created_message
                            false -> R.string.playlist_name_is_taken
                        },
                        binding.playlistName.text
                    )
                )
                if (isPlaylistCreated) requireActivity().supportFragmentManager.popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleExit()
                }
            })
        binding.createToolbar.setNavigationOnClickListener {
            handleExit()
        }
    }


    private suspend fun createPlaylist(): Boolean {
        val name = binding.playlistName.text.toString()
        val description = binding.playlistDescription.text.toString()
        if (selectedImageUri != null) {
            coverImagePath = viewModel.saveCover(selectedImageUri!!)
        }
        return viewModel.createPlaylist(name, description, coverImagePath)
    }

   fun showCustomToast(message: String) {
       val layout = layoutInflater.inflate(R.layout.playlist_toast, null)
       val toastTextView = layout.findViewById<TextView>(R.id.toast_text)
       toastTextView.text = message
       Toast(requireContext())
           .apply {
               duration = Toast.LENGTH_SHORT
               view = layout
               setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, 100)
           }
           .show()
   }

    fun handleExit() {
        if (binding.playlistName.text.isNullOrBlank() &&
            binding.playlistDescription.text.isNullOrBlank() &&
            (selectedImageUri == null)
        ) {
            requireActivity().supportFragmentManager.popBackStack()
        } else {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.finish_creating_playlist_title)
                .setMessage(R.string.finish_creating_playlist_message)
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }.setPositiveButton(R.string.finish) { _, _ ->
                    requireActivity().supportFragmentManager.popBackStack()
                }.show()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val currentActivity = activity
        if (currentActivity is RootActivity) {
            currentActivity.binding.bottomNavigationView.isVisible = true
        }
        _binding = null
    }

}