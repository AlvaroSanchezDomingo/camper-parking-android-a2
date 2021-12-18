package ie.wit.parking.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.parking.databinding.CardReviewBinding
import ie.wit.parking.models.Review


class ReviewAdapter constructor(private var reviews: ArrayList<Review>)
    : RecyclerView.Adapter<ReviewAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardReviewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val parking = reviews[holder.adapterPosition]
        holder.bind(parking)
    }


    override fun getItemCount(): Int = reviews.size

    inner class MainHolder(val binding : CardReviewBinding) :
                            RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.root.tag = review
            binding.review = review
            binding.executePendingBindings()
        }
    }
}