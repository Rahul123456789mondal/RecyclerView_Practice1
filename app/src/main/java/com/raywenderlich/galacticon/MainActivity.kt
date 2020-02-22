package com.raywenderlich.galacticon

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity(), ImageRequester.ImageRequesterResponse {

  private lateinit var adapter: RecyclerAdapter
  private lateinit var linearLayoutManager: LinearLayoutManager
  private lateinit var gridLayoutManager: GridLayoutManager
  private var photosList: ArrayList<Photo> = ArrayList()
  private lateinit var imageRequester: ImageRequester

  private val lastVisibleItemPosition : Int
      get() = if (recyclerView.layoutManager == linearLayoutManager){
        linearLayoutManager.findLastVisibleItemPosition()
      } else {
        gridLayoutManager.findLastVisibleItemPosition()
      }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    linearLayoutManager = LinearLayoutManager(this)
    recyclerView.layoutManager = linearLayoutManager
    adapter = RecyclerAdapter(photosList)
    recyclerView.adapter = adapter
    setRecyclerViewScollListener()
    gridLayoutManager = GridLayoutManager(this, 2)
    imageRequester = ImageRequester(this)

  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.action_change_recycler_manager) {
      changeLayoutManager()
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onStart() {
    super.onStart()
    // This adds a check to see if your list is empty , and if yes, it request a photo
    if (photosList.size == 0){
      requestPhoto()
    }

  }

  private fun requestPhoto() {
    try {
      imageRequester.getPhoto()
    } catch (e: IOException) {
      e.printStackTrace()
    }

  }

  override fun receivedNewPhoto(newPhoto: Photo) {
    runOnUiThread {
      // Here, you inform the recycler adapter that you add an item after updating the list of photos
      photosList.add(newPhoto)
      adapter.notifyItemInserted(photosList.size)
    }
  }

  // ScrollListener
  private fun setRecyclerViewScollListener(){
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val totalItemCount = recyclerView.layoutManager!!.itemCount
        if (!imageRequester.isLoadingData && totalItemCount == lastVisibleItemPosition + 1){
          requestPhoto()
        }
      }
    })
  }

  //Layout Changing
  private fun changeLayoutManager(){
    if (recyclerView.layoutManager == linearLayoutManager){
      // if its using the LinerLayoutManager, it swaps in the GridLayoutManager
      recyclerView.layoutManager = gridLayoutManager
      // It requests a new photo if your grid layout only has one photo to show
      if (photosList.size == 1){
        requestPhoto()
      }
    } else {
      // if it using the GrridLayoutManger, it swaps in the LinerLayoutManager.
      recyclerView.layoutManager = linearLayoutManager
    }
  }

}

























