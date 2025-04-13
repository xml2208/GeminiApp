package uz.xml.geminiapp.presentation.daily_calorie

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import uz.xml.geminiapp.R
import uz.xml.geminiapp.presentation.composable.DropdownSelector
import uz.xml.geminiapp.presentation.composable.NumericInputField

@Composable
fun DailyCaloriesEstimationScreen(
    viewModel: DailyCaloriesViewModel = koinViewModel()
) {
    val result by viewModel.uiState
    val options = calorieScreenOptions()

    var formState by remember {
        mutableStateOf(
            CalorieFormState(
                gender = options.genderOptions.firstOrNull().orEmpty(),
                activity = options.activityOptions.getOrNull(2).orEmpty(),
                goal = options.goalOptions.firstOrNull().orEmpty()
            )
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CalorieInputForm(
            formState = formState,
            options = options,
            onFormStateChange = { newState -> formState = newState }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.estimateCalories(
                    gender = formState.gender,
                    age = formState.age.toIntOrNull() ?: 0,
                    height = formState.height.toIntOrNull() ?: 0,
                    weight = formState.weight.toIntOrNull() ?: 0,
                    activityLevel = formState.activity,
                    goal = formState.goal,
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.calculate_button))
        }
        Spacer(modifier = Modifier.height(16.dp))
        result.takeIf { it.isNotBlank() }?.let {
            CalorieResultCard(result = it)
        }
    }
}

@Composable
fun CalorieInputForm(
    formState: CalorieFormState,
    options: CalorieScreenOptions,
    onFormStateChange: (CalorieFormState) -> Unit
) {
    DropdownSelector(
        label = stringResource(R.string.gender_label),
        selectedOption = formState.gender,
        options = options.genderOptions,
        onOptionSelected = { onFormStateChange(formState.copy(gender = it)) }
    )

    NumericInputField(
        value = formState.age,
        label = stringResource(R.string.age_label),
        onValueChange = { onFormStateChange(formState.copy(age = it)) }
    )

    NumericInputField(
        value = formState.height,
        label = stringResource(R.string.height_label),
        onValueChange = { onFormStateChange(formState.copy(height = it)) }
    )

    NumericInputField(
        value = formState.weight,
        label = stringResource(R.string.weight_label),
        onValueChange = { onFormStateChange(formState.copy(weight = it)) }
    )
    DropdownSelector(
        label = stringResource(R.string.activity_label),
        selectedOption = formState.activity,
        options = options.activityOptions,
        onOptionSelected = { onFormStateChange(formState.copy(activity = it)) }
    )
    DropdownSelector(
        label = stringResource(R.string.goal_label),
        selectedOption = formState.goal,
        options = options.goalOptions,
        onOptionSelected = { onFormStateChange(formState.copy(goal = it)) }
    )
}

@Composable
fun CalorieResultCard(result: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.result_title),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(result)
        }
    }
}

@Composable
fun calorieScreenOptions(): CalorieScreenOptions {
    val genderOptions = listOf(
        stringResource(R.string.gender_female),
        stringResource(R.string.gender_male)
    )

    val activityOptions = listOf(
        stringResource(R.string.activity_sedentary),
        stringResource(R.string.activity_light),
        stringResource(R.string.activity_moderate),
        stringResource(R.string.activity_very),
        stringResource(R.string.activity_super)
    )

    val goalOptions = listOf(
        stringResource(R.string.goal_maintain),
        stringResource(R.string.goal_lose),
        stringResource(R.string.goal_gain)
    )

    return remember(
        key1 = genderOptions,
        key2 = activityOptions,
        key3 = goalOptions
    ) {
        CalorieScreenOptions(
            genderOptions = genderOptions,
            activityOptions = activityOptions,
            goalOptions = goalOptions
        )
    }
}