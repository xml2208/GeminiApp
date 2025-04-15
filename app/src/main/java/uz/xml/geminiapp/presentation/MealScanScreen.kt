package uz.xml.geminiapp.presentation

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import uz.xml.geminiapp.R
import uz.xml.geminiapp.presentation.navigation.NavRoutes

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MealScanScreen(navController: NavController) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA,
        onPermissionResult = { isGranted ->
            if (isGranted) {
                navController.navigate(NavRoutes.CAMERA)
            } else {
                showCameraNeededToast(context)
            }
        }
    )
    Scaffold(
        bottomBar = {
            MealScreenBottomBarContent(navController = navController)
        }
    ) { paddingValues ->
        WelcomeScreen(
            modifier = Modifier.padding(paddingValues),
            onScanClick = {
                if (cameraPermissionState.status.isGranted) {
                    navController.navigate(NavRoutes.CAMERA)
                } else {
                    if (cameraPermissionState.status.shouldShowRationale) {
                        showCameraNeededToast(context)
                    }
                    cameraPermissionState.launchPermissionRequest()
                }
            })
    }
}

@Composable
fun MealScreenBottomBarContent(
    navController: NavController,
) {
    Column {
        Divider()
        BottomAppBar(
            modifier = Modifier,
            contentColor = Color.Black,
            containerColor = Color.Transparent,
        ) {
            Divider(Modifier.width(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.SpaceEvenly
            ) {
                IconButton(
                    onClick = { navController.navigate(NavRoutes.WELCOME) },
                    content = { Icon(Icons.Default.Home, null) }
                )
                IconButton(
                    onClick = { navController.navigate(NavRoutes.SETTINGS) },
                    content = { Icon(Icons.Default.Settings, null) }
                )
            }
        }
    }
}

@Composable
private fun WelcomeScreen(
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .padding(bottom = 6.dp),
            model = "https://www.watersedgechc.com/wp-content/uploads/2024/04/The-Difference-Between-a-Nutritionist-and-a-Registered-Dietitian.jpg",
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        Text(
            text = stringResource(R.string.ai_powered_analyzer),
            fontWeight = FontWeight.Bold,
            fontSize = 54.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = stringResource(R.string.capturing_img_description),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = stringResource(R.string.scan_meal),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .background(shape = RoundedCornerShape(16.dp), color = Color.Black)
                .padding(12.dp)
                .clickable { onScanClick() }
        )
    }
}

private fun showCameraNeededToast(context: Context) {
    Toast.makeText(
        context,
        context.getString(R.string.camera_permission_required),
        Toast.LENGTH_LONG
    ).show()
}

