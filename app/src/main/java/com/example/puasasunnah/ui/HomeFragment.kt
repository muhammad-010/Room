package com.example.puasasunnah.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.puasasunnah.JadwalAdapter
import com.example.puasasunnah.database.Jadwal
import com.example.puasasunnah.database.JadwalDao
import com.example.puasasunnah.database.JadwalDatabase
import com.example.puasasunnah.databinding.FragmentHomeBinding
import com.example.puasasunnah.model.ApiResponse
import com.example.puasasunnah.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mJadwalDao: JadwalDao
    private lateinit var executorService: ExecutorService
    private lateinit var adapterJadwal: JadwalAdapter
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("JADWAL", "==================== MULAI ======================")

        val arrayMonth = listOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )
        val monthAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayMonth
        )
        executorService = Executors.newSingleThreadExecutor()
        val db = JadwalDatabase.getDatabase(requireContext())
        mJadwalDao = db!!.jadwalDao()!!

        with(binding){
            spinnerMonth.adapter = monthAdapter

            spinnerMonth.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedMonth = position + 1
                        getJadwalList(selectedMonth) { jadwalList ->
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
                                        lifecycleScope.launch {
                                            delete(jadwal)
                                            jadwal.isFavorite = false
                                            adapterJadwal.notifyItemChanged(jadwalList.indexOf(jadwal))
                                        }
                                        Toast.makeText(requireContext(), "Jadwal dihapus dari favorite", Toast.LENGTH_SHORT).show()
                                    } else {
                                        lifecycleScope.launch {
                                            insert(jadwal)
                                            jadwal.isFavorite = true
                                            adapterJadwal.notifyItemChanged(jadwalList.indexOf(jadwal))
                                        }
                                        Toast.makeText(requireContext(), "Jadwal ditambahkan ke favorite", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                            rvJadwal.apply {
                                layoutManager = LinearLayoutManager(requireContext())
                                adapter = adapterJadwal
                            }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
        }
    }

    private suspend fun checkJadwal(id: Int): Boolean {
        return withContext(Dispatchers.IO) {
            val jadwal = mJadwalDao.getJadwalSync(id)
            jadwal != null
        }
    }

    private fun insert(jadwal: Jadwal){
        executorService.execute{
            mJadwalDao.insert(jadwal)
        }
    }
    private fun delete(jadwal: Jadwal) {
        executorService.execute { mJadwalDao.delete(jadwal) }
    }

    private fun getJadwalList(month: Int, callback: (List<Jadwal>) -> Unit) {
        val jadwalList = mutableListOf<Jadwal>()
        val client = ApiClient.getInstance()

        client.getApiResponse(month).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        for (item in response.body()!!.data) {
                            val isFavorite = withContext(Dispatchers.IO) { checkJadwal(item.id) }

                            val jadwalData = Jadwal(
                                id = item.id,
                                categoryId = item.category.id,
                                typeId = item.type.id,
                                date = item.date,
                                year = item.year,
                                month = item.month,
                                day = item.day,
                                humanDate = item.humanDate,
                                category = item.category,
                                type = item.type,
                                isFavorite = isFavorite
                            )
                            jadwalList.add(jadwalData)
                        }
                        callback(jadwalList)
                    }
                } else {
                    Log.e("API_ERROR", "Response not successful: ${response.message()}")
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("API_ERROR", "Error fetching API data: ${t.message}")
                callback(emptyList())
            }
        })
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}