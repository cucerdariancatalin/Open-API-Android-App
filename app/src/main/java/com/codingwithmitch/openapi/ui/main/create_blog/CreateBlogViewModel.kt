package com.codingwithmitch.openapi.ui.main.create_blog

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.interactors.blog.PublishBlog
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.util.*
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.SUCCESS_BLOG_CREATED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateBlogViewModel
@Inject
constructor(
    private val publishBlog: PublishBlog,
    private val sessionManager: SessionManager
): ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<CreateBlogState> = MutableLiveData(CreateBlogState())

    fun onTriggerEvent(event: CreateBlogEvents){
        when(event){
            is CreateBlogEvents.OnUpdateTitle -> {
                onUpdateTitle(event.title)
            }
            is CreateBlogEvents.OnUpdateBody -> {
                onUpdateBody(event.body)
            }
            is CreateBlogEvents.OnUpdateUri -> {
                onUpdateUri(event.uri)
            }
            is CreateBlogEvents.PublishBlog -> {
                publishBlog()
            }
            is CreateBlogEvents.OnPublishSuccess -> {
                onPublishSuccess()
            }
            is CreateBlogEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        Log.d(TAG, "appendToMessageQueue: ${stateMessage.response.message}")
        // TODO
    }

    // call after successfully publishing
    private fun clearNewBlogFields(){
        onUpdateTitle("")
        onUpdateBody("")
        onUpdateUri(null)
    }

    private fun onPublishSuccess(){
        clearNewBlogFields()
        state.value?.let { state ->
            this.state.value = state.copy(onPublishSuccess = true)
        }
    }

    private fun onUpdateUri(uri: Uri?){
        state.value?.let { state ->
            this.state.value = state.copy(uri = uri)
        }
    }

    private fun onUpdateTitle(title: String){
        state.value?.let { state ->
            this.state.value = state.copy(title = title)
        }
    }

    private fun onUpdateBody(body: String){
        state.value?.let { state ->
            this.state.value = state.copy(body = body)
        }
    }

    private fun publishBlog(){
        state.value?.let { state ->
            val title = RequestBody.create(
                MediaType.parse("text/plain"),
                state.title
            )
            val body = RequestBody.create(
                MediaType.parse("text/plain"),
                state.body
            )
            if(state.uri == null){
                onTriggerEvent(CreateBlogEvents.Error(
                    stateMessage = StateMessage(
                        response = Response(
                            message = ErrorHandling.ERROR_MUST_SELECT_IMAGE,
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        )
                    )
                ))
            }
            else{
                var multipartBody: MultipartBody.Part? = null
                state.uri.path?.let { filePath ->
                    val imageFile = File(filePath)
                    if(imageFile.exists()){
                        val requestBody =
                            RequestBody.create(
                                MediaType.parse("image/*"),
                                imageFile
                            )
                        multipartBody = MultipartBody.Part.createFormData(
                            "image",
                            imageFile.name,
                            requestBody
                        )
                    }
                }
                if(multipartBody != null){
                    publishBlog.execute(
                        authToken = sessionManager.state.value?.authToken,
                        title = title,
                        body = body,
                        image = multipartBody,
                    ).onEach { dataState ->
                        this.state.value = state.copy(isLoading = dataState.isLoading)

                        dataState.data?.let { response ->
                            if(response.message == SUCCESS_BLOG_CREATED){
                                onTriggerEvent(CreateBlogEvents.OnPublishSuccess)
                            }else{
                                appendToMessageQueue(
                                    stateMessage = StateMessage(response)
                                )
                            }
                        }

                        dataState.stateMessage?.let { stateMessage ->
                            appendToMessageQueue(stateMessage)
                        }
                    }.launchIn(viewModelScope)
                }
            }
        }
    }
}





