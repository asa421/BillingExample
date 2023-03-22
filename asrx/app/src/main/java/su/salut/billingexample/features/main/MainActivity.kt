package su.salut.billingexample.features.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import su.salut.billingexample.features.main.viewmodel.MainViewModel
import su.salut.billingexample.R
import su.salut.billingexample.databinding.ActivityMainBinding
import su.salut.billingexample.extensions.lib.manager.BillingManager

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels { MainViewModel.factory }

    private lateinit var binding: ActivityMainBinding
    private val topLevelDestinationIds = setOf(
        R.id.navigation_settings, R.id.navigation_products, R.id.navigation_purchases
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            BillingManager.onNewIntent(intent) // For a successful return to the application!
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(topLevelDestinationIds)
        setupActionBarWithNavController(navHostFragment.navController, appBarConfiguration)
        navView.setupWithNavController(navHostFragment.navController)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        BillingManager.onNewIntent(intent) // For a successful return to the application!
    }

    override fun onResume() {
        super.onResume()
        // Perhaps the purchase was confirmed while the application was turned off!
        mainViewModel.onUpdateActivePurchases()
    }
}