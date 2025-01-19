package com.danrmzn.financiallyfit.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.danrmzn.financiallyfit.GoogleAuthClient
import com.danrmzn.financiallyfit.LoginActivity

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val googleAuthClient = GoogleAuthClient(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Picture
                Image(
                    painter = rememberAsyncImagePainter(CURRENTUSER.PROFILE_PICTURE),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                // User Info
                Column {
                    Text(
                        text = CURRENTUSER.NAME ?: "User Name",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = CURRENTUSER.EMAIL ?: "Email Address",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

//        // Divider
        HorizontalDivider(thickness = 1.dp)

        // List of OptionsExitToApp
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
        ) {
            // Add reusable items


//            item { SettingsItem(icon = Icons.Default.Person, text = "Account") }
            item {
                SettingsItem(
                    icon = Icons.Filled.Language,
                    text = "Visit my Website!",
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://danialramzan.github.io/")
                        )
                        context.startActivity(intent)
                    }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Description,
                    text = "Terms of Service",
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://danialramzan.github.io/FinanciallyFit/#terms-of-service")
                        )
                        context.startActivity(intent)
                    }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Lock,
                    text = "Privacy Policy",
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://danialramzan.github.io/FinanciallyFit/#privacy-policy")
                        )
                        context.startActivity(intent)
                    }
                )
            }

//            item { SettingsItem(icon = Icons.Default.Favorite, text = "Storage and Data") }
            item {
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    text = "Log Out",
                    onClick = {
                        googleAuthClient.signOut()
                        Toast.makeText(context, "Successfully Logged Out!", Toast.LENGTH_SHORT)
                            .show()

                        // Optionally navigate to a login screen
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}