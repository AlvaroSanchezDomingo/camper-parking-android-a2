package ie.wit.parking.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ie.wit.parking.R
import ie.wit.parking.ui.auth.Login


class SplashActivity : AppCompatActivity() {

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()

        handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }, 2000) // set delay 3sec before opening DIYActivity
    }
}
