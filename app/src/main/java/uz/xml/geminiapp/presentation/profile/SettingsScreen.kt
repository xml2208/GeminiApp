package uz.xml.geminiapp.presentation.profile

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import uz.xml.geminiapp.R
import uz.xml.geminiapp.presentation.language.AppLanguage
import uz.xml.geminiapp.presentation.language.LanguageManager
import uz.xml.geminiapp.presentation.navigation.NavRoutes

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsScreenViewModel: SettingsScreenViewModel = koinViewModel(),
) {
    var showLanguageDialog by remember { mutableStateOf(false) }
    val selectedLanguage = settingsScreenViewModel.selectedLanguage.collectAsState(AppLanguage.ENGLISH).value

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.profile_title),
            fontSize = 52.sp,
            fontWeight = FontWeight.Bold,
        )
        Divider()
        Row(
            modifier = Modifier
                .clickable { showLanguageDialog = true }
                .padding(vertical = 16.dp)
        ) {
            Icon(
                Icons.Default.Build,
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.language),
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null
            )
        }
        Divider()
        Row(
            modifier = Modifier
                .clickable { navController.navigate(NavRoutes.USER_DAILY_CALORIE) }
                .padding(vertical = 16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.user_calorie),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null
            )
        }
        Divider()
        if (showLanguageDialog) {
            LanguagePickerDialog(
                selectedLanguage = selectedLanguage,
                changeLanguage = settingsScreenViewModel::changeLanguage,
                onDismissRequest = { showLanguageDialog = false }
            )
        }
    }
}


@Composable
fun LanguagePickerDialog(
    selectedLanguage: AppLanguage,
    changeLanguage: (AppLanguage) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    var tempSelection by remember { mutableStateOf(selectedLanguage) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                changeLanguage(tempSelection)
                LanguageManager.setLocale(context, tempSelection)
                (context as? Activity)?.recreate()
                onDismissRequest()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        title = {
            Text(stringResource(R.string.choose_language))
        },
        text = {
            Column {
                AppLanguage.entries.forEach { language ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { tempSelection = language }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (language == tempSelection),
                            onClick = { tempSelection = language }
                        )
                        Text(
                            text = language.name.lowercase().replaceFirstChar { it.uppercase() },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    )
}
