package uz.xml.geminiapp.presentation.meal_plan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import uz.xml.geminiapp.R
import uz.xml.geminiapp.presentation.navigation.NavRoutes
import uz.xml.geminiapp.presentation.theme.AccentGradientEnd
import uz.xml.geminiapp.presentation.theme.AccentGradientStart
import uz.xml.geminiapp.presentation.theme.DarkBackground
import uz.xml.geminiapp.presentation.theme.InputBorder
import uz.xml.geminiapp.presentation.theme.InputContainer
import uz.xml.geminiapp.presentation.theme.InputFocused
import uz.xml.geminiapp.presentation.theme.TextPrimary
import uz.xml.geminiapp.presentation.theme.TextSecondary

@Composable
fun MealPlanScreen(
    navController: NavController,
    viewModel: MealPlanViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        when (uiState) {
            is MealPlanUiState.Input -> {
                InputForm(
                    viewModel = viewModel,
                    navController = navController,
                )
            }

            is MealPlanUiState.Loading -> {
                LoadingView()
            }

            is MealPlanUiState.Success -> {
                ResultView(
                    mealPlan = (uiState as MealPlanUiState.Success).mealPlan,
                    onBack = { viewModel.resetToInput() }
                )
            }

            is MealPlanUiState.Error -> {
                ErrorView(
                    message = (uiState as MealPlanUiState.Error).message,
                    onRetry = { viewModel.resetToInput() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputForm(
    viewModel: MealPlanViewModel,
    navController: NavController,
) {
    val scrollState = rememberScrollState()

    val effectiveUseStoredCalories by viewModel.effectiveUseStoredCalories.collectAsState()
    val storedCalories by viewModel.storedCalories.collectAsState()
    val hasStoredCalories by viewModel.hasStoredCalories.collectAsState()
    val calorieInput by viewModel.calorieInput.collectAsState()
    val hasCalorieError by viewModel.hasCalorieError.collectAsState()
    val mealsPerDay by viewModel.mealsPerDay.collectAsState()
    val allergies by viewModel.allergies.collectAsState()
    val hasAllergiesError by viewModel.hasAllergiesError.collectAsState()
    val likesDislikes by viewModel.likesDislikes.collectAsState()
    val hasLikesDislikesError by viewModel.hasLikesDislikesError.collectAsState()
    val dietType by viewModel.dietType.collectAsState()
    val goal by viewModel.goal.collectAsState()
    val activityLevel by viewModel.activityLevel.collectAsState()
    val cuisineType by viewModel.cuisineType.collectAsState()
    val hasCuisineTypeError by viewModel.hasCuisineTypeError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(AccentGradientStart, AccentGradientEnd)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.meal_plan_title),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = DarkBackground
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = effectiveUseStoredCalories,
                        onClick = {
                            if (hasStoredCalories) {
                                viewModel.toggleCalorieSource(true)
                            }
                        },
                        enabled = hasStoredCalories,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.White,
                            unselectedColor = Color.Gray,
                            disabledSelectedColor = Color.Gray,
                            disabledUnselectedColor = Color.Gray
                        )
                    )


                    Text(
                        text = if (hasStoredCalories) {
                            stringResource(R.string.daily_calories, storedCalories)
                        } else {
                            stringResource(R.string.no_calories_warning)
                        },
                        color = if (hasStoredCalories) Color.White else Color.Red,
                        modifier = Modifier
                            .clickable(enabled = hasStoredCalories) {
                                if (hasStoredCalories) {
                                    viewModel.toggleCalorieSource(true)
                                }
                            }
                            .weight(1f)
                            .padding(start = 8.dp)
                    )


                    Button(
                        onClick = { navController.navigate(NavRoutes.USER_DAILY_CALORIE) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = if (!hasStoredCalories) stringResource(R.string.calculate_calories) else stringResource(R.string.recalculate_calories),
                            color = Color.Black,
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !effectiveUseStoredCalories, // Changed from !useStoredCalories
                        onClick = { viewModel.toggleCalorieSource(false) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.White,
                            unselectedColor = Color.Gray
                        )
                    )

                    Text(
                        text = stringResource(R.string.enter_calories),
                        color = Color.White,
                        modifier = Modifier
                            .clickable { viewModel.toggleCalorieSource(false) }
                            .padding(start = 8.dp)
                    )
                }

                AnimatedVisibility(
                    visible = !effectiveUseStoredCalories,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        OutlinedTextField(
                            value = calorieInput,
                            onValueChange = { viewModel.updateCalorieInput(it) },
                            placeholder = { Text(stringResource(R.string.calories_placeholder)) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedPlaceholderColor = Color.Gray,
                                unfocusedPlaceholderColor = Color.Gray
                            ),
                            isError = hasCalorieError,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )

                        if (hasCalorieError) {
                            Text(
                                text = stringResource(R.string.calories_input_error),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = stringResource(R.string.meals_per_day),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(3, 4, 5).forEach { count ->
                MealCountOption(
                    count = count,
                    selected = mealsPerDay == count,
                    onSelect = { viewModel.updateMealsPerDay(count) }
                )
            }
        }

        OutlinedTextField(
            value = allergies,
            onValueChange = { viewModel.updateAllergies(it) },
            label = { Text(stringResource(R.string.allergies)) },
            placeholder = { Text(stringResource(R.string.allergies_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = InputContainer,
                unfocusedContainerColor = InputContainer,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                unfocusedPlaceholderColor = TextSecondary,
                focusedPlaceholderColor = TextSecondary
            ),
            isError = hasAllergiesError
        )

        if (hasAllergiesError) {
            Text(
                text = stringResource(R.string.required_field),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        OutlinedTextField(
            value = likesDislikes,
            onValueChange = { viewModel.updateLikesDislikes(it) },
            label = { Text(stringResource(R.string.likes_dislikes)) },
            placeholder = { Text(stringResource(R.string.likes_dislikes_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = InputContainer,
                focusedBorderColor = InputFocused,
                unfocusedBorderColor = InputBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = AccentGradientEnd,
                unfocusedPlaceholderColor = TextSecondary,
                focusedPlaceholderColor = TextSecondary,
            ),
            isError = hasLikesDislikesError
        )

        if (hasLikesDislikesError) {
            Text(
                text = stringResource(R.string.required_field),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Text(
            text = stringResource(R.string.diet_type),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )

        val dietOptions = listOf(
            DietType.NONE.value to stringResource(R.string.diet_none),
            DietType.VEGETARIAN.value to stringResource(R.string.diet_vegetarian),
            DietType.VEGAN.value to stringResource(R.string.diet_vegan),
            DietType.KETO.value to stringResource(R.string.diet_keto),
            DietType.HEALTHY.value to stringResource(R.string.diet_healthy)
        )

        DietTypeSelector(
            options = dietOptions,
            selectedOption = dietType,
            onOptionSelected = { viewModel.updateDietType(it) }
        )

        Text(
            text = "Goal",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            fontWeight = FontWeight.Medium,
        )

        val goalOptions = listOf(
            Goal.MAINTAIN.value to stringResource(R.string.goal_maintain),
            Goal.LOSE_WEIGHT.value to stringResource(R.string.goal_lose),
            Goal.GAIN_WEIGHT.value to stringResource(R.string.goal_gain),
        )

        DietTypeSelector(
            options = goalOptions,
            selectedOption = goal,
            onOptionSelected = viewModel::updateGoal
        )

        Text(
            text = stringResource(R.string.activity_level),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )

        val activityOptions = listOf(
            ActivityLevel.LOW.value to stringResource(R.string.activity_low),
            ActivityLevel.MEDIUM.value to stringResource(R.string.activity_medium),
            ActivityLevel.HIGH.value to stringResource(R.string.activity_high)
        )

        ActivityLevelSelector(
            options = activityOptions,
            selectedOption = activityLevel,
            onOptionSelected = viewModel::updateActivityLevel
        )

        OutlinedTextField(
            value = cuisineType,
            onValueChange = viewModel::updateCuisineType,
            label = { Text(stringResource(R.string.cuisine_type)) },
            placeholder = { Text(stringResource(R.string.cuisine_type_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = InputContainer,
                focusedBorderColor = InputFocused,
                unfocusedBorderColor = InputBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = AccentGradientEnd,
                unfocusedPlaceholderColor = TextSecondary,
                focusedPlaceholderColor = TextSecondary,
            ),
            isError = hasCuisineTypeError
        )

        if (hasCuisineTypeError) {
            Text(
                text = stringResource(R.string.required_field),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Button(
            onClick = { viewModel.generateMealPlan() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(AccentGradientStart, AccentGradientEnd)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            )
        ) {
            Text(
                text = stringResource(R.string.generate_meal_plan),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}