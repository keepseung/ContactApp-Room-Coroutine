package com.keepseung.roomdemo

import android.util.Patterns
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keepseung.roomdemo.db.Subscriber
import com.keepseung.roomdemo.db.SubscriberRepository
import kotlinx.coroutines.launch

class SubscriberViewModel(private val repository:SubscriberRepository) :ViewModel(), Observable{

    val subscribers = repository.subscribers
    private var isUpdateOrDelete = false

    private lateinit var subscriberToUpdateOrDelete: Subscriber
    // 사용자가 입력한 내용
    // 양방향 바인딩을 위한 Bindable 사용
    @Bindable
    val inputName = MutableLiveData<String>()
    @Bindable
    val inputEmail= MutableLiveData<String>()

    // 버튼에 보일 텍스트
    // 양방향 바인딩을 위한 Bindable 사용
    @Bindable
    val saveOrUpdateButtonText = MutableLiveData<String>()
    @Bindable
    val clearAllOrButtonText = MutableLiveData<String>()

    // 외부에서 접근 못하게
    private val statusMessage = MutableLiveData<Event<String>>()

    val message: LiveData<Event<String>>
        get() = statusMessage

    init{
        // 버튼 텍스트의 초기값을 설정해줌
        saveOrUpdateButtonText.value = "저장"
        clearAllOrButtonText.value = "모두 삭제"
    }

    fun saveOrUpadte(){

        if (inputName.value ==null){
            statusMessage.value = Event("Please enter subscriber's name")
        }else if (inputEmail.value == null){
            statusMessage.value = Event("Please enter subscriber's email")
        }else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()){
            statusMessage.value = Event("Please enter a correct email")
        }else {
            if (isUpdateOrDelete){
                // 객체의 속성 값을 수정하려면 해당 객체의 변수를 var로 해야 함
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!
                update(subscriberToUpdateOrDelete)
            }else{
                val name = inputName.value!!
                val email = inputEmail.value!!
                // id의 값이 자동으로 증가하도록 primary key로 설정했다.
                // Room은 0 값을 무시하고 자동으로 아이디 값을 증가시킬 것이다.
                insert(Subscriber(0, name, email))
                inputName.value = null
                inputEmail.value = null
            }
        }

    }

    fun clearAllOrDelete(){
        clearAll()
    }

    fun initUpdateAndDelete(subscriber: Subscriber){
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email

        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateButtonText.value = "수정"
        clearAllOrButtonText.value = "삭제"
    }

    fun insert(subscriber: Subscriber) = viewModelScope.launch {
        val newRowId = repository.insert(subscriber)

        if (newRowId > -1){
            statusMessage.value = Event("Subscriber Inserted Successfully $newRowId")
        }else{
            statusMessage.value = Event("Error Occured")
        }

    }

    fun update(subscriber: Subscriber)= viewModelScope.launch {
        val noOfRows = repository.update(subscriber)
        if (noOfRows>0){
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = "Save"
            clearAllOrButtonText.value = "Clear All"

            statusMessage.value = Event("$noOfRows Row Updated Successfully")
        }else{
            statusMessage.value = Event("Error Occured")
        }


    }

    fun delete(subscriber: Subscriber)= viewModelScope.launch {

        val noOfRowsDeleted = repository.delete(subscriber)

        if (noOfRowsDeleted >0){
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = "Save"
            clearAllOrButtonText.value = "Clear All"

            statusMessage.value = Event("$noOfRowsDeleted Rows Deleted Successfully")
        }else{
            statusMessage.value = Event("Error Occured")
        }

    }

    fun clearAll() = viewModelScope.launch {

        if(isUpdateOrDelete){
            delete(subscriberToUpdateOrDelete)
        }else{
            val noOfRowsDeleted =repository.deleteAll()
            if (noOfRowsDeleted >0){
                statusMessage.value = Event("$noOfRowsDeleted Subscribers Deleted Successfully")
            }else{
                statusMessage.value = Event("Error Occured")
            }

        }
    }


    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}