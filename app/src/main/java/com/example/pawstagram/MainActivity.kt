package com.example.pawstagram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.width
import androidx.compose.material3.Button
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pawstagram.ui.FeedViewModel
import com.example.pawstagram.ui.AuthViewModel
import com.example.pawstagram.ui.screens.FeedScreen
import com.example.pawstagram.ui.screens.UploadScreen
import com.example.pawstagram.ui.screens.SignInScreen
import com.example.pawstagram.ui.theme.PawstagramTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkMode by rememberSaveable { mutableStateOf(false) }
            PawstagramTheme(darkTheme = darkMode) {
                val authVm: AuthViewModel = viewModel()
                val user by authVm.user.collectAsState()
                val currentUser = user

                if (currentUser == null) {
                    val loading by authVm.loading.collectAsState()
                    val error by authVm.error.collectAsState()
                        SignInScreen(
                        onSignIn = { email, password -> authVm.signInWithEmailPassword(email, password) },
                        onSignUp = { email, password, username -> authVm.signUpWithEmailPassword(email, password, username) },
                        onResetPassword = { email -> authVm.resetPassword(email) },
                        onToggleDarkMode = { darkMode = !darkMode },
                        isDarkMode = darkMode,
                        isLoading = loading,
                        errorMessage = error
                    )
                } else {
                    val navController = rememberNavController()
                    val vm: FeedViewModel = viewModel()
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()
                    val configuration = LocalConfiguration.current
                    val drawerWidth = (configuration.screenWidthDp * 0.55f).dp

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(
                                drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
                                modifier = Modifier.width(drawerWidth)
                            ) {
                                DrawerContent(
                                    user = currentUser,
                                    onLogout = {
                                        scope.launch { drawerState.close() }
                                        authVm.signOut()
                                    },
                                    onCloseDrawer = {
                                        scope.launch { drawerState.close() }
                                    }
                                )
                            }
                        }
                    ) {
                        Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            androidx.compose.material3.TopAppBar(
                                title = { 
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.logo),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .height(32.dp)
                                                .width(32.dp)
                                                .padding(end = 8.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                        Text(
                                            "Pawstagram",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                        )
                                    }
                                },
                                navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { darkMode = !darkMode }) {
                                        Icon(
                                            imageVector = if (darkMode) Icons.Filled.DarkMode else Icons.Outlined.DarkMode,
                                            contentDescription = null
                                        )
                                    }
                                }
                        )
                        },
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route
                        NavigationBar(
                            containerColor = if (darkMode) {
                                // Darker yellow/gold for dark mode
                                Color(0xFFF5B700).copy(alpha = 0.9f)
                            } else {
                                com.example.pawstagram.ui.theme.SecondaryLightColor
                            },
                            modifier = Modifier.shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                                spotColor = Color.Black.copy(alpha = if (darkMode) 0.4f else 0.2f)
                            ),
                            tonalElevation = 4.dp
                        ) {
                            NavigationBarItem(
                                selected = currentRoute == "feed",
                                onClick = {
                                    navController.navigate("feed") {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { 
                                    Icon(
                                        Icons.Filled.Home, 
                                        contentDescription = null,
                                        modifier = Modifier
                                    )
                                },
                                label = { 
                                    Text(
                                        "Feed",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (currentRoute == "feed") androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                    ) 
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = if (darkMode) Color.White.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.2f),
                                    unselectedIconColor = if (darkMode) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.6f),
                                    unselectedTextColor = if (darkMode) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.6f)
                                ),
                                alwaysShowLabel = true
                            )
                            NavigationBarItem(
                                selected = currentRoute == "upload",
                                onClick = {
                                    navController.navigate("upload") {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { 
                                    Icon(
                                        Icons.Filled.AddCircle, 
                                        contentDescription = null,
                                        modifier = Modifier
                                    )
                                },
                                label = { 
                                    Text(
                                        "Upload",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (currentRoute == "upload") androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                    ) 
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = if (darkMode) Color.White.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.2f),
                                    unselectedIconColor = if (darkMode) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.6f),
                                    unselectedTextColor = if (darkMode) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.6f)
                                ),
                                alwaysShowLabel = true
                            )
                        }
                    }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = "feed",
                                modifier = Modifier.padding(innerPadding)
                            ) {
                            composable("feed") {
                                val posts by vm.posts.collectAsState()
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val scrollTrigger = navBackStackEntry?.savedStateHandle?.get<Int>("scrollTrigger") ?: 0
                                
                                FeedScreen(
                                    posts = posts,
                                    onToggleLike = vm::toggleLike,
                                    scrollToTop = scrollTrigger
                                )
                            }
                            composable("upload") {
                                var showUploadSuccess by remember { mutableStateOf(false) }
                                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                                UploadScreen(
                                    onSubmit = { imageUri, caption, hashtags ->
                                        vm.addPost(
                                            imageUri = imageUri,
                                            caption = caption,
                                            hashtags = hashtags,
                                            username = currentUser.displayName ?: ("User-" + currentUser.uid.take(6))
                                        )
                                        showUploadSuccess = true
                                    }
                                )
                                
                                if (showUploadSuccess) {
                                    ModalBottomSheet(
                                        onDismissRequest = { 
                                            showUploadSuccess = false
                                            val trigger = System.currentTimeMillis().toInt()
                                            navController.navigate("feed") {
                                                popUpTo("feed") { inclusive = true }
                                                launchSingleTop = true
                                            }
                                            // Set trigger after navigation completes
                                            scope.launch {
                                                kotlinx.coroutines.delay(100)
                                                try {
                                                    navController.getBackStackEntry("feed").savedStateHandle
                                                        .set("scrollTrigger", trigger)
                                                } catch (e: Exception) {
                                                    // Ignore
                                                }
                                            }
                                        },
                                        sheetState = sheetState,
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.CheckCircle,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(bottom = 16.dp)
                                            )
                                            Text(
                                                text = "Upload Successful!",
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                            Text(
                                                text = "Your post has been uploaded successfully.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(24.dp))
                                            Button(
                                                onClick = {
                                                    showUploadSuccess = false
                                                    val trigger = System.currentTimeMillis().toInt()
                                                    navController.navigate("feed") {
                                                        popUpTo("feed") { inclusive = true }
                                                        launchSingleTop = true
                                                    }
                                                    // Set trigger after navigation completes
                                                    scope.launch {
                                                        kotlinx.coroutines.delay(100)
                                                        try {
                                                            navController.getBackStackEntry("feed").savedStateHandle
                                                                .set("scrollTrigger", trigger)
                                                        } catch (e: Exception) {
                                                            // Ignore
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    "View Feed",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            }
                        }
                    }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DrawerContent(
    user: com.google.firebase.auth.FirebaseUser?,
    onLogout: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 24.dp, horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .height(48.dp)
                        .width(48.dp)
                        .padding(bottom = 12.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = user?.displayName ?: "User",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                if (!user?.email.isNullOrBlank()) {
                    Text(
                        text = user?.email ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Menu Items
        NavigationDrawerItem(
            icon = { Icon(Icons.Filled.Logout, contentDescription = null) },
            label = { 
                Text(
                    "Logout",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                ) 
            },
            selected = false,
            onClick = {
                onCloseDrawer()
                onLogout()
            },
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}
