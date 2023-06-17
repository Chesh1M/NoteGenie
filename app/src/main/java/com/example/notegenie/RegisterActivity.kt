package com.example.notegenie

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import com.example.notegenie.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {

    private lateinit var mBinding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        mBinding.fullNameEt.onFocusChangeListener = this
        mBinding.emailEt.onFocusChangeListener = this
        mBinding.passwordEt.onFocusChangeListener = this
        mBinding.cPasswordEt.onFocusChangeListener = this
    }

    /* REGISTRATION FIELDS VALIDATION */
    // Validate full name
    private fun validateFullName(): Boolean{
        var errorMessage: String? = null
        val value: String = mBinding.fullNameEt.text.toString()
        if (value.isEmpty()){
            errorMessage = "Full name is required"
        }

        // if validation fails
        if (errorMessage != null){
            mBinding.fullNameTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    // Validate email
    private fun validateEmail(): Boolean {
        var errorMessage: String? = null
        val value = mBinding.emailEt.text.toString()
        if (value.isEmpty()){
            errorMessage  = "Email is required"
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) { // if email entered is a valid email address
            errorMessage = "Email address is invalid"
        }

        // if validation fails
        if (errorMessage != null){
            mBinding.emailTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    // Validate password
    private fun validatePassword(): Boolean {
        var errorMessage: String? = null
        val value = mBinding.passwordEt.text.toString()
        if (value.isEmpty()){
            errorMessage  = "Password is required"
        }
        else if (value.length < 6) { // if email entered is a valid email address
            errorMessage = "Password must be at least 6 characters long"
        }

        // if validation fails
        if (errorMessage != null){
            mBinding.passwordTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    // Validate confirm password
    private fun validateConfirmPassword(): Boolean {
        var errorMessage: String? = null
        val value = mBinding.cPasswordEt.text.toString()
        if (value.isEmpty()){
            errorMessage  = "Confirm Password is required"
        }
        else if (value.length < 6) { // if email entered is a valid email address
            errorMessage = "Confirm Password must be at least 6 characters long"
        }

        // if validation fails
        if (errorMessage != null){
            mBinding.cPasswordTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    // Validate password and confirm password
    private fun validatePasswordAndConfirmPassword(): Boolean {
        var errorMessage: String? = null
        val password = mBinding.passwordEt.text.toString()
        val confirmPassword = mBinding.cPasswordEt.text.toString()
        if (password != confirmPassword){
            errorMessage = "Confirm password and password do not match!"
        }

        // if validation fails
        if (errorMessage != null){
            mBinding.cPasswordTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }
    /* // REGISTRATION FIELDS VALIDATION */

    override fun onClick(view: View?) {
        TODO("Haven't done")
    }

    /* EFFECTS FOR CORRECTLY/INCORRECTLY FILLED FIELDS */
    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when(view.id) {
                // Full name field
                R.id.fullNameEt -> {
                    if (hasFocus) { // remove error if field on focus
                        if (mBinding.fullNameTil.isErrorEnabled){
                            mBinding.fullNameTil.isErrorEnabled = false
                        }
                    } else {
                        validateFullName()
                    }
                }

                // Email field
                R.id.emailEt -> {
                    if (hasFocus) { // remove error if field on focus
                        if (mBinding.emailTil.isErrorEnabled){
                            mBinding.emailTil.isErrorEnabled = false
                        }
                    } else { // else validate email keyed in when not in focus
                        if (validateEmail()) {
                            // do validation for its uniqueness
                        }
                    }
                }

                // Password field
                R.id.passwordEt -> {
                    if (hasFocus) { // remove error if field on focus
                        if (mBinding.passwordTil.isErrorEnabled){
                            mBinding.passwordTil.isErrorEnabled = false
                        }
                    } else { // else apply style based on correctly/incorrectly filled field
                        if (validatePassword() && mBinding.cPasswordEt.text!!.isNotEmpty() && validateConfirmPassword() && validatePasswordAndConfirmPassword()){
                            if (mBinding.cPasswordTil.isErrorEnabled) {
                                mBinding.cPasswordTil.isErrorEnabled = false
                            }
                            mBinding.cPasswordTil.apply {
                                setStartIconDrawable(R.drawable.check_circle_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }
                        }
                    }
                }

                // Confirm password field
                R.id.cPasswordEt -> {
                    if (hasFocus) { // remove error if field on focus
                        if (mBinding.cPasswordTil.isErrorEnabled){
                            mBinding.cPasswordTil.isErrorEnabled = false
                        }
                    } else { // else apply style based on correctly/incorrectly filled field
                        if (validateConfirmPassword() && validatePassword() && validatePasswordAndConfirmPassword()) {
                            if (mBinding.passwordTil.isErrorEnabled) {
                                mBinding.passwordTil.isErrorEnabled = false
                            }
                            mBinding.cPasswordTil.apply {
                                setStartIconDrawable(R.drawable.check_circle_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }
                        }
                    }
                }
            }
        }
    }
    /* // EFFECTS FOR CORRECTLY/INCORRECTLY FILLED FIELDS */

    override fun onKey(view: View?, event: Int, keyEvent: KeyEvent?): Boolean {
        return false
    }
}