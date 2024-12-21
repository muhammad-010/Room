package com.example.puasasunnah

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.puasasunnah.database.Jadwal
import com.example.puasasunnah.database.JadwalDao
import com.example.puasasunnah.databinding.ItemPuasaBinding

typealias OnClickJadwal = (Jadwal) -> Unit
typealias OnFavoriteClick = (Jadwal) -> Unit

class JadwalAdapter(
    private val listJadwalDao: List<Jadwal>,
    private val onClickJadwal: OnClickJadwal,
    private val onFavoriteClick: OnFavoriteClick
): RecyclerView.Adapter<JadwalAdapter.ItemJadwalViewHolder>() {
    inner class ItemJadwalViewHolder(private val binding: ItemPuasaBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: Jadwal){
            with(binding){
                txtJadwal.setText(data.humanDate)
                jadwalItem.setOnClickListener{
                    onClickJadwal(data)
                }
                iconFavorite.setImageResource(
                    if (data.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
                )
                iconFavorite.setOnClickListener {
                    onFavoriteClick(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemJadwalViewHolder {
        val binding = ItemPuasaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemJadwalViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listJadwalDao.size
    }

    override fun onBindViewHolder(holder: ItemJadwalViewHolder, position: Int) {
        holder.bind(listJadwalDao[position])
    }
}