package ie.wit.parking.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.parking.R
import ie.wit.parking.databinding.CardParkingBinding
import ie.wit.parking.models.ParkingModel

interface DonationClickListener {
    fun onDonationClick(donation: ParkingModel)
}

class DonationAdapter constructor(private var donations: ArrayList<ParkingModel>,
                                  private val listener: DonationClickListener)
    : RecyclerView.Adapter<DonationAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardParkingBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val donation = donations[holder.adapterPosition]
        holder.bind(donation,listener)
    }

    fun removeAt(position: Int) {
        donations.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = donations.size

    inner class MainHolder(val binding : CardParkingBinding) :
                            RecyclerView.ViewHolder(binding.root) {

        fun bind(parking: ParkingModel, listener: DonationClickListener) {
            binding.root.tag = parking
            binding.parking = parking
            binding.imageIcon.setImageResource(R.mipmap.ic_launcher_round)
            binding.root.setOnClickListener { listener.onDonationClick(parking) }
            binding.executePendingBindings()
        }
    }
}