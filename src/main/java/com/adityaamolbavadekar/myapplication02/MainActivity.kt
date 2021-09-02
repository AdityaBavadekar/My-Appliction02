package com.adityaamolbavadekar.myapplication02

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var crashlytics: FirebaseCrashlytics
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        // Set the dimensions of the sign-in button.
        // Set the dimensions of the sign-in button.
        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_WIDE)
        signInButton.setOnClickListener {
            Toast.makeText(baseContext, "Requesting...",Toast.LENGTH_LONG).show()
            googleSignIn()
        }
        logout.setOnClickListener {
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account != null){
                mGoogleSignInClient.signOut()
                Toast.makeText(baseContext, "Logged Out Successfully!",Toast.LENGTH_LONG).show()
                textView_NAME.setText("name")
                textView_EMAIL.setText("email")
                signInButton.visibility = View.VISIBLE
            }
        }
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }


    private fun startForLaunchForGoogleSignIn() =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    crashlytics = Firebase.crashlytics
                    Toast.makeText(baseContext, "App request successfull, Please wait...",Toast.LENGTH_LONG).show()
                    crashlytics.log("SignInWithGoogle() Request Code is OK going ahead...")
                    val accountExecuteTask = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                    val data: Intent? = it.data
                    try {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                        Toast.makeText(baseContext, "Requesting Google Please wait...",Toast.LENGTH_SHORT).show()
                        handleSignInResult(task)
                    } catch (e: Exception) {
                        Toast.makeText(baseContext, "0ops! Something went wrong!!!...",Toast.LENGTH_SHORT).show()
                        crashlytics = Firebase.crashlytics
                        crashlytics.log("startForLaunch():failure!! $e")
                    }

                }
            }

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        try {
            Toast.makeText(baseContext, "Requesting App...",Toast.LENGTH_SHORT).show()
            startForLaunchForGoogleSignIn().launch(Intent(signInIntent))
        }catch (e : Exception){
            Toast.makeText(baseContext, "0ops! Something went wrong while Requesting App!!!...",Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account)
            Toast.makeText(baseContext, "Successsfully Signed In Requesting your data...",Toast.LENGTH_SHORT).show()
        } catch (e: ApiException) {
            Toast.makeText(baseContext, "0ops! Something went wrong while Requesting Google $e!!!...",Toast.LENGTH_SHORT).show()
            crashlytics = Firebase.crashlytics
            crashlytics.log("startForLaunch():failure!! $e")
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
           // Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null){
            sign_in_button.visibility = View.INVISIBLE
            textView_NAME.setText(account.displayName)
            textView_EMAIL.setText(account.email)
        }
    }


    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)

    }

}