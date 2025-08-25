package test.gencidev.ui.jetpack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import test.gencidev.common.states.UiState
import test.gencidev.extensions.isLog
import test.gencidev.extensions.toast
import test.gencidev.model.response.area.DayOffModel
import test.gencidev.network.connection.NetworkConnectionLiveData
import test.gencidev.viewmodel.DayOffViewModel
import java.util.*
import test.gencidev.R

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<DayOffViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val customColorScheme = lightColorScheme(
                primary = colorResource(id = R.color.primary),
            )
            MaterialTheme(customColorScheme) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.systemBars.asPaddingValues())
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: DayOffViewModel) {
    val context = LocalContext.current

    // State variables
    var searchText by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }

    var showDayOffDialog by remember { mutableStateOf(false) }
    var selectedDayOff by remember { mutableStateOf<DayOffModel?>(null) }
    var showYearPicker by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(false) }

    // State variables for LiveData
    var networkState by remember { mutableStateOf<Boolean?>(null) }
    var dataState by remember { mutableStateOf<UiState?>(null) }
    var dayOffList by remember { mutableStateOf<List<DayOffModel>?>(null) }

    // Observe network connection
    val networkConnection = remember { NetworkConnectionLiveData(context) }

    // Setup LiveData observers
    DisposableEffect(context) {
        val networkObserver = Observer<Boolean> { connected ->
            networkState = connected
        }
        val dataStateObserver = Observer<UiState> { state ->
            dataState = state
        }
        val dayOffObserver = Observer<List<DayOffModel>> { list ->
            dayOffList = list
        }

        networkConnection.observe(context as LifecycleOwner, networkObserver)
        viewModel.dataState.observe(context as LifecycleOwner, dataStateObserver)
        viewModel.dayOffResponse.observe(context as LifecycleOwner, dayOffObserver)

        onDispose {
            networkConnection.removeObserver(networkObserver)
            viewModel.dataState.removeObserver(dataStateObserver)
            viewModel.dayOffResponse.removeObserver(dayOffObserver)
        }
    }

    // Filter data based on search
    val filteredData = remember(dayOffList, searchText) {
        if (searchText.isNotEmpty()) {
            dayOffList?.filter {
                it.keterangan.lowercase(Locale.getDefault())
                    .contains(searchText.lowercase(Locale.getDefault()))
            } ?: emptyList()
        } else {
            dayOffList ?: emptyList()
        }
    }

    // Handle network state changes
    LaunchedEffect(networkState) {
        networkState?.let { connected ->
            isConnected = connected
            if (connected) {
                isLog("network connected")
            } else {
                isLog("network disconnect")
                viewModel.checkDatabaseAndLoadData()
            }
        }
    }

    // Handle data state changes
    LaunchedEffect(dataState) {
        val currentState = dataState
        when (currentState) {
            is UiState.Error -> {
                context.toast(currentState.message)
                viewModel.checkDatabaseAndLoadData()
            }

            else -> {

            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search and Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Search TextField
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Cari data") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )

            // Filter Button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray.copy(alpha = 0.1f))
                    .clickable {
                        if (isConnected) {
                            showYearPicker = true
                        } else {
                            context.toast("No internet")
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = "Filter",
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content based on state
        when (dataState) {
            UiState.Loading -> {
                LoadingContent()
            }

            UiState.Success -> {
                if (filteredData.isEmpty()) {
                    EmptyContent(
                        message = if (searchText.isNotEmpty()) "Pencarian Kosong" else "Data kosong"
                    )
                } else {
                    DayOffList(
                        dayOffList = filteredData,
                        onItemClick = { data ->
                            selectedDayOff = data
                            showDayOffDialog = true
                        }
                    )
                }
            }

            is UiState.Error -> {
                if (filteredData.isEmpty()) {
                    EmptyContent(message = "Data kosong")
                } else {
                    DayOffList(
                        dayOffList = filteredData,
                        onItemClick = { data ->
                            selectedDayOff = data
                            showDayOffDialog = true
                        }
                    )
                }
            }

            else -> {
                FetchDataContent(
                    onFetchClick = {
                        if (isConnected) {
                            val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
                            viewModel.dayOff(currentYear)
                        } else {
                            context.toast("No internet")
                        }
                    }
                )
            }
        }
    }

    // Day Off Dialog
    if (showDayOffDialog && selectedDayOff != null) {
        DayOffDialog(
            dayOff = selectedDayOff!!,
            onDismiss = {
                showDayOffDialog = false
                selectedDayOff = null
            }
        )
    }

    // Year Picker Dialog
    if (showYearPicker) {
        YearPickerDialog(
            selectedYear = selectedYear,
            onYearSelected = { year ->
                selectedYear = year
                showYearPicker = false
                viewModel.dayOff(year.toString())
            },
            onDismiss = { showYearPicker = false }
        )
    }
}

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(35.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun EmptyContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FetchDataContent(onFetchClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onFetchClick,
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Ambil Data")
        }
    }
}

@Composable
fun DayOffList(
    dayOffList: List<DayOffModel>,
    onItemClick: (DayOffModel) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(dayOffList) { dayOff ->
            DayOffItem(
                dayOff = dayOff,
                onClick = { onItemClick(dayOff) }
            )
        }
    }
}

@Composable
fun DayOffItem(
    dayOff: DayOffModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = dayOff.keterangan,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun YearPickerDialog(
    selectedYear: Int,
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = remember {
        (2020..(currentYear)).toList()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Pilih Tahun",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn {
                    items(years) { year ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onYearSelected(year) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = year == selectedYear,
                                onClick = { onYearSelected(year) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = year.toString(),
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayOffDialog(
    dayOff: DayOffModel,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Date
                Text(
                    text = dayOff.tanggal_display ?: dayOff.tanggal ?: "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Description
                Text(
                    text = dayOff.keterangan,
                    fontSize = 16.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Cuti status
                Text(
                    text = if (dayOff.is_cuti) "Cuti" else "Tidak Cuti",
                    fontSize = 14.sp,
                    color = if (dayOff.is_cuti) Color(0xFF4CAF50) else Color(0xFFFF5722),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Close button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tutup")
                }
            }
        }
    }
}