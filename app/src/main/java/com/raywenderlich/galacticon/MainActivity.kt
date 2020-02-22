package com.raywenderlich.galacticon

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
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

    setRecyclerViewItemTouchListener() // call the function ItemTouchHelper 

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

  private fun setRecyclerViewItemTouchListener(){

    //1 Create the callback and tell it what events to listen for. It takes two parameters: One for drag directions and one for swipe directions. You’re only interested in swipe. Pass 0 to inform the callback not to respond to drag events.
    val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

      override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder): Boolean {
        //2 Return false in onMove. You don’t want to perform any special behavior here
        return false
      }

      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
        //3 Call onSwiped when you swipe an item in the direction specified in the ItemTouchHelper. Here, you request the viewHolder parameter passed for the position of the item view, and then you remove that item from your list of photos. Finally, you inform the RecyclerView adapter that an item has been removed at a specific position.
        val position = viewHolder.adapterPosition
        photosList.removeAt(position)
        recyclerView.adapter!!.notifyItemRemoved(position)
      }

    }


    //4 Initialize ItemTouchHelper with the callback behavior you defined, and then attach it to the RecyclerView.
    val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
    itemTouchHelper.attachToRecyclerView(recyclerView)
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

























