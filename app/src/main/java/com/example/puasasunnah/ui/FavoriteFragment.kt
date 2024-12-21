package com.example.puasasunnah.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.puasasunnah.JadwalAdapter
import com.example.puasasunnah.database.Jadwal
import com.example.puasasunnah.database.JadwalDao
import com.example.puasasunnah.database.JadwalDatabase
import com.example.puasasunnah.databinding.FragmentFavoriteBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var mJadwalDao: JadwalDao
    private lateinit var executorService: ExecutorService
    private lateinit var adapterJadwal: JadwalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        executorService = Executors.newSingleThreadExecutor()
        val db = JadwalDatabase.getDatabase(requireContext())
        mJadwalDao = db!!.jadwalDao()!!

        loadFavorites()

    }

    override fun onResume() {
        super.onResume()
    }

    private fun loadFavorites() {
        mJadwalDao.getAllFavorites().observe(viewLifecycleOwner) { jadwalList ->
            adapterJadwal = JadwalAdapter(
                jadwalList,
                onClickJadwal = { jadwal ->
                    Toast.makeText(
                        requireContext(),
                        "Tanggal ${jadwal.humanDate} adalah ${jadwal.type.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onFavoriteClick = { jadwal ->
                    if (jadwal.isFavorite) {
                        delete(jadwal)
                        Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT).show()
                    } else {
                        insert(jadwal)
                        Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show()
                    }
                    jadwal.isFavorite = !jadwal.isFavorite
                    adapterJadwal.notifyItemChanged(jadwalList.indexOf(jadwal))
                }
            )
            binding.rvJadwalFavorite.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = adapterJadwal
            }
        }
    }

    private fun insert(jadwal: Jadwal) {
        executorService.execute {
            mJadwalDao.insert(jadwal)
        }
    }

    private fun delete(jadwal: Jadwal) {
        executorService.execute {
            mJadwalDao.delete(jadwal)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
