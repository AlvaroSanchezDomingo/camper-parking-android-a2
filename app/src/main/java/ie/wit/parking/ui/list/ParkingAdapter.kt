package ie.wit.parking.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.wit.parking.R
import ie.wit.parking.databinding.CardParkingBinding
import ie.wit.parking.models.ParkingModel

interface ParkingClickListener {
    fun onParkingClick(parking: ParkingModel)
}

class ParkingAdapter constructor(private var parkings: ArrayList<ParkingModel>,
                                 private val listener: ParkingClickListener)
    : RecyclerView.Adapter<ParkingAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardParkingBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val parking = parkings[holder.adapterPosition]
        holder.bind(parking,listener)
    }

    fun removeAt(position: Int) {
        parkings.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = parkings.size

    inner class MainHolder(val binding : CardParkingBinding) :
                            RecyclerView.ViewHolder(binding.root) {

        fun bind(parking: ParkingModel, listener: ParkingClickListener) {
            binding.root.tag = parking
            binding.parking = parking
            if (parking.image != ""){
                Picasso.get()
                    .load(parking.image)
                    .resize(200, 200)
                    .into(binding.imageIcon)
            }
            binding.root.setOnClickListener { listener.onParkingClick(parking) }
            binding.executePendingBindings()
        }
    }
}