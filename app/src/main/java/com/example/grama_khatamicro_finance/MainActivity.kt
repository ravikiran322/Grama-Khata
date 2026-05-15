package com.example.grama_khatamicro_finance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.grama_khatamicro_finance.data.AppDatabase
import com.example.grama_khatamicro_finance.data.AppRepository
import com.example.grama_khatamicro_finance.data.PreferenceManager
import com.example.grama_khatamicro_finance.ui.*
import com.example.grama_khatamicro_finance.ui.theme.GramaKhataMicroFinanceTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = AppRepository(database.appDao())
        val preferenceManager = PreferenceManager(applicationContext)
        val viewModelFactory = MainViewModelFactory(repository, preferenceManager)

        setContent {
            val viewModel: MainViewModel = viewModel(factory = viewModelFactory)
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            
            GramaKhataMicroFinanceTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val securityPin by viewModel.securityPin.collectAsState()
                var isUnlocked by remember { mutableStateOf(false) }
                
                // Track Firebase Auth state
                var currentUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }

                if (currentUser == null) {
                    LoginScreen(
                        onLoginSuccess = { 
                            currentUser = FirebaseAuth.getInstance().currentUser
                            // Data sync is handled in ViewModel init once UID is available
                        }
                    )
                } else if (securityPin != null && !isUnlocked) {
                    PinLockScreen(
                        correctPin = securityPin!!,
                        onUnlock = { isUnlocked = true }
                    )
                } else {
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("dashboard") {
                            DashboardScreen(
                                viewModel = viewModel,
                                onCustomerClick = { id -> navController.navigate("detail/$id") },
                                onAddCustomerClick = { navController.navigate("add_customer") },
                                onSettingsClick = { navController.navigate("settings") }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                onLogout = {
                                    FirebaseAuth.getInstance().signOut()
                                    currentUser = null
                                }
                            )
                        }
                        composable(
                            "detail/{customerId}",
                            arguments = listOf(navArgument("customerId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val customerId = backStackEntry.arguments?.getInt("customerId") ?: return@composable
                            CustomerDetailScreen(
                                customerId = customerId,
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("add_customer") {
                            AddCustomerScreen(
                                viewModel = viewModel,
                                onFinish = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
