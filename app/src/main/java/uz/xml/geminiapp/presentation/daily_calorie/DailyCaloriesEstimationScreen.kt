package uz.xml.geminiapp.presentation.daily_calorie

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import uz.xml.geminiapp.R
import uz.xml.geminiapp.presentation.composable.DropdownSelector
import uz.xml.geminiapp.presentation.composable.NumericInputField
import uz.xml.geminiapp.presentation.theme.DarkBackground
import uz.xml.geminiapp.presentation.theme.PurpleAccent
import uz.xml.geminiapp.presentation.theme.SecondaryAccent

@Composable
fun DailyCaloriesEstimationScreen(
    viewModel: DailyCaloriesViewModel = koinViewModel()
) {
    val result by viewModel.uiState
    val formState by viewModel.formState
    val options = calorieScreenOptions()

    LaunchedEffect(options) {
        viewModel.initializeOptions(options)
    }

    val showAge by viewModel.showAge
    val showHeight by viewModel.showHeight
    val showWeight by viewModel.showWeight
    val showActivity by viewModel.showActivity
    val showGoal by viewModel.showGoal
    val showCalculateButton by viewModel.showCalculateButton

    val totalSteps = 7
    var completedSteps = 1
    if (showAge) completedSteps++
    if (showHeight) completedSteps++
    if (showWeight) completedSteps++
    if (showActivity) completedSteps++
    if (showGoal) completedSteps++
    if (result.isNotBlank()) completedSteps++

    val progress = completedSteps.toFloat() / totalSteps.toFloat()
    val enterAnimation =
        fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300))

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                PurpleAccent,
                                SecondaryAccent
                            )
                        )
                    )
            ) {
                Column(
                    Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.daily_calorie_calculator),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Step $completedSteps of $totalSteps",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            CalorieInputForm(
                formState = formState,
                options = options,
                onFormStateChange = { newState -> viewModel.updateFormState(newState) },
                showAge = showAge,
                showHeight = showHeight,
                showWeight = showWeight,
                showActivity = showActivity,
                showGoal = showGoal,
                enterAnimation = enterAnimation,
            )

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedVisibility(
                visible = showCalculateButton,
                enter = enterAnimation,
            ) {
                GradientButton(
                    text = stringResource(R.string.calculate_button),
                    onClick = {
                        viewModel.estimateCalories(
                            gender = formState.gender,
                            age = formState.age.toIntOrNull() ?: 0,
                            height = formState.height.toIntOrNull() ?: 0,
                            weight = formState.weight.toIntOrNull() ?: 0,
                            activityLevel = formState.activity,
                            goal = formState.goal,
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = result.isNotBlank(),
                enter = enterAnimation,
                content = { CalorieResultCard(result = result) }
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = ButtonDefaults.ContentPadding
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(colors = listOf(PurpleAccent, SecondaryAccent))
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun CalorieInputForm(
    formState: CalorieFormState,
    options: CalorieScreenOptions,
    onFormStateChange: (CalorieFormState) -> Unit,
    showAge: Boolean,
    showHeight: Boolean,
    showWeight: Boolean,
    showActivity: Boolean,
    showGoal: Boolean,
    enterAnimation: EnterTransition,
) {
    AnimatedVisibility(
        visible = showAge,
        enter = enterAnimation
    ) {
        InputFieldCard(
            label = stringResource(R.string.age_label),
            highlight = !showHeight
        ) {
            NumericInputField(
                value = formState.age,
                label = stringResource(R.string.enter_your_age),
                onValueChange = { onFormStateChange(formState.copy(age = it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    AnimatedVisibility(
        visible = showHeight,
        enter = enterAnimation
    ) {
        InputFieldCard(
            label = stringResource(R.string.height_label),
            highlight = !showWeight
        ) {
            NumericInputField(
                value = formState.height,
                label = stringResource(R.string.enter_height),
                onValueChange = { onFormStateChange(formState.copy(height = it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    AnimatedVisibility(
        visible = showWeight,
        enter = enterAnimation
    ) {
        InputFieldCard(
            label = stringResource(R.string.weight_label),
            highlight = !showActivity
        ) {
            NumericInputField(
                value = formState.weight,
                label = stringResource(R.string.enter_weight),
                onValueChange = { onFormStateChange(formState.copy(weight = it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    AnimatedVisibility(
        visible = showActivity,
        enter = enterAnimation,
    ) {
        InputFieldCard(
            label = stringResource(R.string.activity_label),
            highlight = !showGoal
        ) {
            DropdownSelector(
                label = "",
                selectedOption = formState.activity,
                options = options.activityOptions,
                onOptionSelected = { onFormStateChange(formState.copy(activity = it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    AnimatedVisibility(
        visible = showGoal,
        enter = enterAnimation,
    ) {
        InputFieldCard(
            label = stringResource(R.string.goal_label),
            highlight = true
        ) {
            DropdownSelector(
                label = "",
                selectedOption = formState.goal,
                options = options.goalOptions,
                onOptionSelected = { onFormStateChange(formState.copy(goal = it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CalorieResultCard(result: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(colors = listOf(SecondaryAccent, PurpleAccent))
                )
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .width(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White)
                    )
                    Text(
                        text = stringResource(R.string.result_title),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = result,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun calorieScreenOptions(): CalorieScreenOptions {
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