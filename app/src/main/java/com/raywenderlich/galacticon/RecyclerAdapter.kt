package com.raywenderlich.galacticon

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*

class RecyclerAdapter(private val photos : ArrayList<Photo>) : RecyclerView.Adapter<RecyclerAdapter.PhotoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.PhotoHolder {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_row, false)
        return PhotoHolder(inflatedView)
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: RecyclerAdapter.PhotoHolder, position: Int) {
        val itemPhoto = photos[position]
        holder.bindPhoto(itemPhoto)
    }

    // Make the class extend RecyclerView.ViewHolder, allowing the adapter to use it as a ViewHolder.
    class PhotoHolder(v : View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        private val TAG : String = "Recycler View Click"
        // Add a reference to the view you’ve inflated to allow the ViewHolder to access the ImageView and TextView as an extension property. Kotlin Android Extensions plugin adds hidden caching functions and fields to prevent the constant querying of views.
        private var view : View = v
        private var photo : Photo? = null

        init {
            v.setOnClickListener(this)
        }
        // This binds the photo to the pfotoholder
        fun bindPhoto(photo : Photo){
            this.photo = photo
            Picasso.with(view.context).load(photo.url).into(view.itemImage)
            view.itemDate.text = photo.humanDate
            view.itemDescription.text = photo.explanation
        }

        override fun onClick(v: View?) {

            val context = itemView.context
            val showPhotoIntent = Intent(context, PhotoActivity::class.java)
            showPhotoIntent.putExtra(PHOTO_KEY, photo)
            context.startActivity(showPhotoIntent)
            Log.d(TAG, "CLICK!")
        }

        companion object{
            private val PHOTO_KEY = "PHOTO"
        }

    }


}






























































































