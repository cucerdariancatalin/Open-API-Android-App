package com.codingwithmitch.openapi.interactors.account

import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.util.*
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.SUCCESS_PASSWORD_UPDATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class UpdatePassword(
    private val service: OpenApiMainService,
) {
    fun execute(
        authToken: AuthToken?,
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String,
    ): Flow<DataState<Response>> = flow {
        emit(DataState.loading<Response>())
        if(authToken == null){
            throw Exception("Authentication token is invalid. Log out and log back in.")
        }
        // Update network
        val response = service.updatePassword(
            authorization = "Token ${authToken.token!!}",
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmNewPassword = confirmNewPassword
        )

        if(response.response != SUCCESS_PASSWORD_UPDATED){
            throw Exception("Unable to update password. Try logging out and logging back in.")
        }

        // Tell the UI it was successful
        emit(DataState.data<Response>(
            data = Response(
                message = SUCCESS_PASSWORD_UPDATED,
                uiComponentType = UIComponentType.Toast(),
                messageType = MessageType.Success()
            ),
            response = null
        ))
    }.catch { e ->
        e.printStackTrace()
        emit(DataState.error<Response>(
            response = Response(
                message = e.message,
                uiComponentType = UIComponentType.Dialog(),
                messageType = MessageType.Error()
            )
        ))
    }
}
