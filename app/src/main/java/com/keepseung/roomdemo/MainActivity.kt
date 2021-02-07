package com.keepseung.roomdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.keepseung.roomdemo.databinding.ActivityMainBinding
import com.keepseung.roomdemo.db.Subscriber
import com.keepseung.roomdemo.db.SubscriberDatabase
import com.keepseung.roomdemo.db.SubscriberRepository

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var subscriberViewModel: SubscriberViewModel
    private lateinit var adapter: MyRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        // application 객체 사용해 dao생성
        val dao = SubscriberDatabase.getInstance(application).subscriberDAO
        // dao 객체 사용해 respository 생성
        val respository = SubscriberRepository(dao)
        // repository 객체 사용해 커스텀으로 만든 뷰 모델 펙토리 생성
        val factory = SubscriberViewModelFactory(respository)

        // 뷰 모델 펙토리를 사용해 뷰모델 생성
        subscriberViewModel = ViewModelProvider(this, factory).get(SubscriberViewModel::class.java)
        binding.myViewModel = subscriberViewModel
        // LiveData를 Databinding이랄ㅇ 사용하기 위해 lifecycleOwner를 현재 activity를 제공
        binding.lifecycleOwner = this

        subscriberViewModel.message.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })

        initRecyclerView()
        displaySubscribersList()
    }

    private fun initRecyclerView(){
        binding.subscriberRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyRecyclerViewAdapter { selectedItem:Subscriber -> listItemClicked(selectedItem)}
        binding.subscriberRecyclerView.adapter =adapter
        displaySubscribersList()
    }

    private fun displaySubscribersList(){
        subscriberViewModel.subscribers.observe(this, Observer {
            Log.i("MYTAG", "about list "+it.toString())
            adapter.setList(it)
            adapter.notifyDataSetChanged()

        })
    }

    private fun listItemClicked(subscriber:Subscriber){
//        Toast.makeText(this, "selected name is ${subscriber.name}", Toast.LENGTH_LONG).show()
        subscriberViewModel.initUpdateAndDelete(subscriber)
    }
}
