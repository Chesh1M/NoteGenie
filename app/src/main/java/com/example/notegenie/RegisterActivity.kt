package com.example.notegenie

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notegenie.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener,
    View.OnKeyListener, TextWatcher {

    private lateinit var mBinding: ActivityRegisterBinding

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        mBinding.fullNameEt.onFocusChangeListener = this
        mBinding.emailEt.onFocusChangeListener = this
        mBinding.passwordEt.onFocusChangeListener = this
        mBinding.cPasswordEt.onFocusChangeListener = this
        mBinding.cPasswordEt.setOnKeyListener(this)
        mBinding.cPasswordEt.addTextChangedListener(this)
        mBinding.registerBtn.setOnClickListener(this)
        mBinding.backToSignInBtn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        // Firebase
        firebaseAuth = FirebaseAuth.getInstance()
    }

    /* REGISTRATION FIELDS VALIDATION */
    // Validate full name
    private fun validateFullName(): Boolean {
        var errorMessage: String? = null
        val value: String = mBinding.fullNameEt.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Full name is required"
        }

        // if validation fails
        if (errorMessage != null) {
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
        if (value.isEmpty()) {
            errorMessage = "Email is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value)
                .matches()
        ) { // if email entered is a valid email address
            errorMessage = "Email address is invalid"
        }

        // if validation fails
        if (errorMessage != null) {
            mBinding.emailTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    // Validate password
    private fun validatePassword(shouldUpdateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        val value = mBinding.passwordEt.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Password is required"
        } else if (value.length < 6) { // if email entered is a valid email address
            errorMessage = "Password must be at least 6 characters long"
        }

        // if validation fails
        if (errorMessage != null && shouldUpdateView) {
            mBinding.passwordTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    // Validate confirm password
    private fun validateConfirmPassword(shouldUpdateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        val value = mBinding.cPasswordEt.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Confirm Password is required"
        } else if (value.length < 6) { // if email entered is a valid email address
            errorMessage = "Confirm Password must be at least 6 characters long"
        }

        // if validation fails
        if (errorMessage != null && shouldUpdateView) {
            mBinding.cPasswordTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    // Validate password and confirm password
    private fun validatePasswordAndConfirmPassword(shouldUpdateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        val password = mBinding.passwordEt.text.toString()
        val confirmPassword = mBinding.cPasswordEt.text.toString()
        if (password != confirmPassword) {
            errorMessage = "Confirm password and password do not match!"
        }

        // if validation fails
        if (errorMessage != null && shouldUpdateView) {
            mBinding.cPasswordTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }
    /* // REGISTRATION FIELDS VALIDATION */

    override fun onClick(view: View?) {
        if (view != null && view.id == R.id.registerBtn)
            onSubmit()
    }

    /* EFFECTS FOR CORRECTLY/INCORRECTLY FILLED FIELDS */
    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                // Full name field
                R.id.fullNameEt -> {
                    if (hasFocus) { // remove error if field on focus
                        if (mBinding.fullNameTil.isErrorEnabled) {
                            mBinding.fullNameTil.isErrorEnabled = false
                        }
                    } else {
                        validateFullName()
                    }
                }

                // Email field
                R.id.emailEt -> {
                    if (hasFocus) { // remove error if field on focus
                        if (mBinding.emailTil.isErrorEnabled) {
                            mBinding.emailTil.isErrorEnabled = false
                        }
                    } else { // else validate email keyed in when not in focus
                        if (validateEmail()) {
                            // TODO do validation for uniqueness of email
                        }
                    }
                }

                // Password field
                R.id.passwordEt -> {
                    if (hasFocus) { // remove error if field on focus
                        if (mBinding.passwordTil.isErrorEnabled) {
                            mBinding.passwordTil.isErrorEnabled = false
                        }
                    } else { // else apply style based on correctly/incorrectly filled field
                        if (validatePassword() && mBinding.cPasswordEt.text!!.isNotEmpty() && validateConfirmPassword() && validatePasswordAndConfirmPassword()) {
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
                        if (mBinding.cPasswordTil.isErrorEnabled) {
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

    override fun onKey(view: View?, keyCode: Int, keyEvent: KeyEvent?): Boolean {
        if (KeyEvent.KEYCODE_ENTER == keyCode && keyEvent!!.action == KeyEvent.ACTION_UP) {
            // do registration
        }
        return false
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        {}
    }

    // On text listener to provide green checkmark when user enters confirm password correctly for each keystroke
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (validatePassword(shouldUpdateView = false) && validateConfirmPassword(shouldUpdateView = false) && validatePasswordAndConfirmPassword(
                shouldUpdateView = false
            )
        ) {
            mBinding.cPasswordTil.apply {
                if (isErrorEnabled) isErrorEnabled = false
                setStartIconDrawable(R.drawable.check_circle_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        } else {
            if (mBinding.cPasswordTil.startIconDrawable != null)
                mBinding.cPasswordTil.startIconDrawable = null
        }
    }

    override fun afterTextChanged(p0: Editable?) {
        {}
    }

    private fun onSubmit() {
        if (validate()) {
            // make api request


            val email = mBinding.emailEt.text.toString()
            val password = mBinding.passwordEt.text.toString()

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    // Redirect to Sign Up screen
                    startActivity(Intent(this, SignInActivity::class.java))
                    Toast.makeText(this, "Account successfully created!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true

        if (!validateFullName()) isValid = false
        if (!validateEmail()) isValid = false
        if (!validatePassword()) isValid = false
        if (!validateConfirmPassword()) isValid = false
        if (isValid && !validatePasswordAndConfirmPassword()) isValid = false
        return isValid
    }
}