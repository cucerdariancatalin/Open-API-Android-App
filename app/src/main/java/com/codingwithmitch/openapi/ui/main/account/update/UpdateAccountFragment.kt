package com.codingwithmitch.openapi.ui.main.account.update

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.Account
import com.codingwithmitch.openapi.ui.main.account.BaseAccountFragment
import kotlinx.android.synthetic.main.fragment_update_account.*

class UpdateAccountFragment : BaseAccountFragment(R.layout.fragment_update_account) {

    private val viewModel: UpdateAccountViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)
            state.account?.let { account ->
                setAccountDataFields(state.account)
            }
        })

        // TODO("Listen for when successfully updated")
    }

    private fun setAccountDataFields(account: Account){
        input_email.setText(account.email)
        input_username.setText(account.username)
    }

    private fun saveChanges(){
        viewModel.onTriggerEvent(UpdateAccountEvents.Update(
            email = input_email.text.toString(),
            username = input_username.text.toString()
        ))
        uiCommunicationListener.hideSoftKeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save -> {
                cacheState()
                saveChanges()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun cacheState(){
        val email = input_email.text.toString()
        val username = input_username.text.toString()
        viewModel.onTriggerEvent(UpdateAccountEvents.OnUpdateEmail(email))
        viewModel.onTriggerEvent(UpdateAccountEvents.OnUpdateUsername(username))
    }

    override fun onPause() {
        super.onPause()
        cacheState()
    }
}






