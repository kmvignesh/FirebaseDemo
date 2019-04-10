package `in`.codeandroid.firebasedemo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.*


class DashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var TAG = "DashboardActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()

        if (auth.uid != null) {


            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference(auth.uid!!)
            val productRef = myRef.child("products")
            val product = Product()
            product.productName = "product"
            product.price = 10.0
            product.createdAt = Date().time
            product.description = "description"
            // For creating list under "products"
            myRef.child("products").push().setValue(product)
            // For storing values directly under uid
//            myRef.setValue(product)

            productRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    for (postSnapshot in dataSnapshot.children) {
                        val value = postSnapshot.getValue(Product::class.java)
                        Log.d(TAG, "Value is: $value")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })


//            // Access a Cloud Firestore instance from your Activity
            val db = FirebaseFirestore.getInstance()
//            // Create a new user with a first and last name

//// Add a new document with a generated ID
//            db.collection(auth.uid!!)
//                .addSnapshotListener { snapshots, e ->
//                    if (e != null) {
//                        Log.w(TAG, "listen:error", e)
//                    }
//
//                    for (dc in snapshots!!.documentChanges) {
//                        when (dc.type) {
//                            DocumentChange.Type.ADDED -> Log.d(TAG, "New city: " + dc.document.data)
//                            DocumentChange.Type.MODIFIED -> Log.d(TAG, "Modified city: " + dc.document.data)
//                            DocumentChange.Type.REMOVED -> Log.d(TAG, "Removed city: " + dc.document.data)
//                        }
//                    }
//                }

//            db.collection(auth.uid!!)
//                .add(product)
//                .addOnSuccessListener { documentReference ->
//                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//                }
//                .addOnFailureListener { e ->
//                    Log.w(TAG, "Error adding document", e)
//                }
//            db.collection(auth.uid!!)
//                .orderBy("createdAt", Query.Direction.DESCENDING)
//                .get()
//                .addOnSuccessListener { result ->
//                    for (document in result) {
//                        Log.d(TAG, "${document.id} => ${document.data}")
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    Log.d(TAG, "Error getting documents: ", exception)
//                }
        }


        btn_change_password.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {

        if (et_current_password.text.isNotEmpty() &&
            et_new_password.text.isNotEmpty() &&
            et_confirm_password.text.isNotEmpty()
        ) {

            if (et_new_password.text.toString().equals(et_confirm_password.text.toString())) {

                val user = auth.currentUser
                if (user != null && user.email != null) {
                    val credential = EmailAuthProvider
                        .getCredential(user.email!!, et_current_password.text.toString())

// Prompt the user to re-provide their sign-in credentials
                    user?.reauthenticate(credential)
                        ?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "Re-Authentication success.", Toast.LENGTH_SHORT).show()
                                user?.updatePassword(et_new_password.text.toString())
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this, "Password changed successfully.", Toast.LENGTH_SHORT)
                                                .show()
                                            auth.signOut()
                                            startActivity(Intent(this, LoginActivity::class.java))
                                            finish()
                                        }
                                    }

                            } else {
                                Toast.makeText(this, "Re-Authentication failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }

            } else {
                Toast.makeText(this, "Password mismatching.", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Please enter all the fields.", Toast.LENGTH_SHORT).show()
        }

    }
}
