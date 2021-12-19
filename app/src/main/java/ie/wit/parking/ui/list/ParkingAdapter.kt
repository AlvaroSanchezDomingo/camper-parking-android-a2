package ie.wit.parking.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.wit.parking.R
import ie.wit.parking.databinding.CardParkingBinding
import ie.wit.parking.models.ParkingModel
import timber.log.Timber

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
    fun calculateRating(parking: ParkingModel): Float{
        var average = 0f
        var count = 0f
        var sum = 0f
        for ((key, value) in parking.reviews) {
            Timber.i("Calculating rating review : $value")
            count +=1
            Timber.i("$key = $value")
            sum += value.rating!!
        }
        average = sum/count
        return average
    }

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
            binding.rating = calculateRating(parking)
            when (parking.category) {
                1 -> binding.categoryIcon.setImageResource(R.drawable.nature_parking)
                2 -> binding.categoryIcon.setImageResource(R.drawable.public_parking)
                3 -> binding.categoryIcon.setImageResource(R.drawable.private_parking)
                4 -> binding.categoryIcon.setImageResource(R.drawable.camping_parking)
                else -> {
                    binding.categoryIcon.setImageResource(R.drawable.camper)
                }
            }

            binding.root.setOnClickListener { listener.onParkingClick(parking) }
            binding.executePendingBindings()
        }
    }
}