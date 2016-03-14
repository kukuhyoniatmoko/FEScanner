package com.foodenak.itpscanner.ui.profile

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.entities.User
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.*
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

/**
 * Created by ITP on 10/12/2015.
 */
class ProfileFragment : Fragment(), ProfileView {

    var viewModel: ProfileViewModel? = null
        @Inject set

    var profileController: ProfileValidationController? = null

    var passwordController: PasswordValidationController? = null

    var profileValidator: Validator? = null

    var passwordValidator: Validator? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
    }

    private fun bindView() {
        nameEditText.addTextChangedListener(viewModel!!.nameTextWatcher)
        usernameEditText.addTextChangedListener(viewModel!!.usernameTextWatcher)
        emailEditText.addTextChangedListener(viewModel!!.emailTextWatcher)
        profileSubmitButton.setOnClickListener(viewModel!!.submitProfileButtonListener)

        passwordEditText.addTextChangedListener(viewModel!!.passwordTextWatcher)
        newPasswordEditText.addTextChangedListener(viewModel!!.newPasswordTextWatcher)
        confirmPasswordEditText.addTextChangedListener(viewModel!!.confirmNewPasswordTextWatcher)
        passwordSubmitButton.setOnClickListener(viewModel!!.submitPasswordButtonListener)

        profileController = ProfileValidationController(nameEditText, usernameEditText, emailEditText, viewModel!!)
        passwordController = PasswordValidationController(passwordEditText, newPasswordEditText, confirmPasswordEditText, viewModel!!)

        profileValidator = Validator(profileController)
        profileValidator!!.setValidationListener(profileController)

        passwordValidator = Validator(passwordController)
        passwordValidator!!.setValidationListener(passwordController)
    }

    override fun hideConfirmNewPasswordErrorMessage() {
        confirmPasswordInputLayout.error = null
    }

    override fun hideNewPasswordErrorMessage() {
        newPasswordInputLayout.error = null
    }

    override fun hidePasswordErrorMessage() {
        passwordInputLayout.error = null
    }

    override fun hideEmailErrorMessage() {
        emailInputLayout.error = null
    }

    override fun hideUsernameErrorMessage() {
        usernameInputLayout.error = null
    }

    override fun hideNameErrorMessage() {
        nameInputLayout.error = null
    }

    override fun validateProfile() {
        profileValidator!!.validate()
    }

    override fun validatePassword() {
        passwordValidator!!.validate()
    }

    override fun initializeUser(user: User) {

    }

    class ProfileValidationController(
            @Order(1)
            @NotEmpty(sequence = 1, messageResId = R.string.required_message)
            val nameEditText: EditText,

            @Order(2)
            @NotEmpty(sequence = 2, messageResId = R.string.required_message)
            @Length(sequence = 3, messageResId = R.string.max60_min5)
            val usernameEditText: EditText,

            @Order(3)
            @NotEmpty(sequence = 4, messageResId = R.string.required_message)
            @Email(sequence = 5, messageResId = R.string.wrong_email)
            val emailEditText: EditText,

            val viewModel: ProfileViewModel
    ) : Validator.ValidationListener {
        override fun onValidationFailed(errors: MutableList<ValidationError>) {
            for (error in errors) {
                if (error.failedRules.isEmpty()) {
                    continue
                }
                val view = error.view
                if (view is EditText) {
                    val parent = view.parent
                    if (parent is TextInputLayout) {
                        parent.error = error.failedRules.get(0).getMessage(view.context)
                    } else {
                        view.error = error.failedRules.get(0).getMessage(view.context)
                    }
                } else {
                    Toast.makeText(view.context, error.failedRules.get(0).getMessage(view.context), Toast.LENGTH_SHORT).show()
                }
                view.requestFocus()
                break
            }
        }

        override fun onValidationSucceeded() {
            viewModel.profileValidationSuccessListener.run()
        }
    }

    class PasswordValidationController(
            @Order(1)
            @NotEmpty(sequence = 1, messageResId = R.string.required_message)
            @Length(sequence = 2, messageResId = R.string.max60_min5)
            val passwordEditText: EditText,

            @Order(2)
            @NotEmpty(sequence = 3, messageResId = R.string.required_message)
            @Length(sequence = 4, messageResId = R.string.max60_min5)
            val newPasswordEditText: EditText,

            @Order(3)
            @ConfirmPassword(sequence = 5)
            val confirmNewPasswordEditText: EditText,

            val viewModel: ProfileViewModel
    ) : Validator.ValidationListener {
        override fun onValidationFailed(errors: MutableList<ValidationError>) {
            for (error in errors) {
                if (error.failedRules.isEmpty()) {
                    continue
                }
                val view = error.view
                if (view is EditText) {
                    val parent = view.parent
                    if (parent is TextInputLayout) {
                        parent.error = error.failedRules.get(0).getMessage(view.context)
                    } else {
                        view.error = error.failedRules.get(0).getMessage(view.context)
                    }
                } else {
                    Toast.makeText(view.context, error.failedRules.get(0).getMessage(view.context), Toast.LENGTH_SHORT).show()
                }
                view.requestFocus()
                break
            }
        }

        override fun onValidationSucceeded() {
            viewModel.passwordValidationSuccessListener.run()
        }
    }
}