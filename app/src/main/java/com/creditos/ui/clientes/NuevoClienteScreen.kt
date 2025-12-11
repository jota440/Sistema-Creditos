//NuevoClienteScreen.kt
package com.creditos.ui.clientes

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.creditos.data.entities.*
import com.creditos.viewmodels.NuevoClienteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoClienteScreen(
    onNavigateBack: () -> Unit,
    viewModel: NuevoClienteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tiposDocumento by viewModel.tiposDocumento.collectAsState()
    val tiposDocumentoState by viewModel.tiposDocumentoState.collectAsState()
    val paises by viewModel.paises.collectAsState()
    val provincias by viewModel.provincias.collectAsState()
    val codigosPostales by viewModel.codigosPostales.collectAsState()

    // Estados del formulario - Información Personal
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellido by rememberSaveable { mutableStateOf("") }
    var tipoDocumentoExpanded by rememberSaveable { mutableStateOf(false) }
    var tipoDocumentoSeleccionado by remember { mutableStateOf<TipoDocumento?>(null) }
    var numeroDocumento by rememberSaveable { mutableStateOf("") }
    var numeroDocumentoError by rememberSaveable { mutableStateOf<String?>(null) }
    var telefono by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }

    // Estados de Dirección
    var paisExpanded by rememberSaveable { mutableStateOf(false) }
    var paisSeleccionado by remember { mutableStateOf<Pais?>(null) }
    var calle by rememberSaveable { mutableStateOf("") }
    var numeroCalle by rememberSaveable { mutableStateOf("") }
    var piso by rememberSaveable { mutableStateOf("") }
    var puerta by rememberSaveable { mutableStateOf("") }

    var codigoPostalExpanded by rememberSaveable { mutableStateOf(false) }
    var codigoPostalText by rememberSaveable { mutableStateOf("") }
    var codigoPostalSeleccionado by remember { mutableStateOf<CodigoPostal?>(null) }

    var ciudadExpanded by rememberSaveable { mutableStateOf(false) }
    var ciudadText by rememberSaveable { mutableStateOf("") }

    var provinciaExpanded by rememberSaveable { mutableStateOf(false) }
    var provinciaText by rememberSaveable { mutableStateOf("") }
    var provinciaSeleccionada by remember { mutableStateOf<Provincia?>(null) }

    // Setear España por defecto
    LaunchedEffect(paises) {
        if (paisSeleccionado == null && paises.isNotEmpty()) {
            paisSeleccionado = paises.find { it.codigo == "ESP" }
        }
    }

    // Filtrar CPs cuando se selecciona provincia
    LaunchedEffect(provinciaSeleccionada) {
        provinciaSeleccionada?.let { provincia ->
            viewModel.buscarCodigosPostalesPorProvincia(provincia.idCodigoPostal)
        }
    }

    // Validar documento cuando cambia (solo si requiere validación)
    LaunchedEffect(numeroDocumento, tipoDocumentoSeleccionado) {
        if (numeroDocumento.isNotBlank() && tipoDocumentoSeleccionado != null) {
            if (tipoDocumentoSeleccionado?.requiereValidacion == 1) {
                numeroDocumentoError = when (tipoDocumentoSeleccionado?.codigo) {
                    "DNI" -> if (viewModel.validarDNI(numeroDocumento)) null else "DNI inválido (formato: 12345678A)"
                    "NIE" -> if (viewModel.validarNIE(numeroDocumento)) null else "NIE inválido (formato: X1234567A)"
                    "CIF" -> if (viewModel.validarCIF(numeroDocumento)) null else "CIF inválido (formato: A12345678)"
                    else -> null
                }
            } else {
                numeroDocumentoError = null
            }
        }
    }

    // Autocompletar ciudad y provincia cuando se selecciona CP
    LaunchedEffect(codigoPostalSeleccionado) {
        codigoPostalSeleccionado?.let { cp ->
            ciudadText = cp.ciudad
            val codigoProvincia = cp.getCodigoProvincia()
            provinciaSeleccionada = provincias.find { it.idCodigoPostal == codigoProvincia }
            provinciaText = provinciaSeleccionada?.provincia ?: ""
        }
    }

    // Manejar éxito
    LaunchedEffect(uiState) {
        if (uiState is NuevoClienteViewModel.UIState.Success) {
            kotlinx.coroutines.delay(500)
            onNavigateBack()
            viewModel.resetState()
        }
    }

    // Filtrar CPs por búsqueda
    val codigosPostalesFiltrados = remember(codigoPostalText, codigosPostales) {
        if (codigoPostalText.isBlank()) codigosPostales
        else codigosPostales.filter { it.codigoPostal.startsWith(codigoPostalText) }
    }

    // Filtrar ciudades únicas
    val ciudadesFiltradas = remember(ciudadText, codigosPostales) {
        if (ciudadText.isBlank()) codigosPostales.map { it.ciudad }.distinct()
        else codigosPostales.filter { it.ciudad.contains(ciudadText, ignoreCase = true) }
            .map { it.ciudad }.distinct()
    }

    // Filtrar provincias por búsqueda
    val provinciasFiltradas = remember(provinciaText, provincias) {
        if (provinciaText.isBlank()) provincias
        else provincias.filter { it.provincia.contains(provinciaText, ignoreCase = true) }
    }

    // ✅ Calcular una sola vez por recomposición
    val formularioValido = validarFormulario(
        nombre = nombre,
        apellido = apellido,
        tipoDocumento = tipoDocumentoSeleccionado,
        numeroDocumento = numeroDocumento,
        numeroDocumentoError = numeroDocumentoError,
        telefono = telefono,
        calle = calle,
        ciudad = ciudadText,
        provincia = provinciaText,
        email = email
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Cliente") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (formularioValido) {
                                viewModel.insertarCliente(
                                    nombre = nombre,
                                    apellido = apellido,
                                    // ⚠️ Aquí sigues enviando el ID. Si quieres guardar el CÓDIGO,
                                    // cambia esto a tipoDocumentoSeleccionado!!.codigo
                                    tipoDocumentoId = tipoDocumentoSeleccionado!!.id,
                                    numeroDocumento = numeroDocumento,
                                    telefonoPrincipal = telefono,
                                    email = if (email.isNotBlank()) email else null,
                                    calle = calle,
                                    numeroCalle = numeroCalle,
                                    piso = piso,
                                    puerta = puerta,
                                    codigoPostal = codigoPostalText,
                                    ciudad = ciudadText,
                                    provincia = provinciaText,
                                    pais = paisSeleccionado?.nombre ?: "España"
                                )
                            }
                        },
                        enabled = formularioValido && uiState !is NuevoClienteViewModel.UIState.Loading
                    ) {
                        if (uiState is NuevoClienteViewModel.UIState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Save, "Guardar")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // ========== INFORMACIÓN PERSONAL ==========
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Información Personal",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Nombre
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = nombre.isBlank() && uiState is NuevoClienteViewModel.UIState.Error
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Apellido
                    OutlinedTextField(
                        value = apellido,
                        onValueChange = { apellido = it },
                        label = { Text("Apellido *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = apellido.isBlank() && uiState is NuevoClienteViewModel.UIState.Error
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ✅ TIPO DOC (30%) + NÚM DOC (70%) EN MISMA FILA - OPTIMIZADO
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Tipo de Documento
                        if (tiposDocumentoState is NuevoClienteViewModel.TiposDocumentoState.Loading) {
                            Box(
                                modifier = Modifier
                                    .weight(0.35f)
                                    .height(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            }
                        } else {
                            ExposedDropdownMenuBox(
                                expanded = tipoDocumentoExpanded,
                                onExpandedChange = { tipoDocumentoExpanded = !tipoDocumentoExpanded },
                                modifier = Modifier.weight(0.35f)
                            ) {
                                OutlinedTextField(
                                    // ✅ Mostrar la DESCRIPCIÓN seleccionada
                                    value = tipoDocumentoSeleccionado?.descripcion ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Tipo *", style = MaterialTheme.typography.bodySmall) },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoDocumentoExpanded)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    isError = tipoDocumentoSeleccionado == null && uiState is NuevoClienteViewModel.UIState.Error,
                                    textStyle = MaterialTheme.typography.bodyLarge
                                )

                                ExposedDropdownMenu(
                                    expanded = tipoDocumentoExpanded,
                                    onDismissRequest = { tipoDocumentoExpanded = false }
                                ) {
                                    tiposDocumento.forEach { tipo ->
                                        DropdownMenuItem(
                                            // ✅ En la lista solo se muestra la DESCRIPCIÓN
                                            text = { Text(tipo.descripcion) },
                                            onClick = {
                                                tipoDocumentoSeleccionado = tipo
                                                tipoDocumentoExpanded = false
                                                numeroDocumento = ""
                                                numeroDocumentoError = null
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Número de Documento
                        OutlinedTextField(
                            value = numeroDocumento,
                            onValueChange = { numeroDocumento = it.uppercase() },
                            label = { Text("Nº Documento *") },
                            modifier = Modifier.weight(0.65f),
                            singleLine = true,
                            isError = numeroDocumentoError != null,
                            // ✅ Siempre habilitado para que el borde se vea igual que el resto
                            supportingText = {
                                if (numeroDocumentoError != null) {
                                    Text(numeroDocumentoError!!)
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Teléfono
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '+' }) telefono = it },
                        label = { Text("Teléfono *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = telefono.isBlank() && uiState is NuevoClienteViewModel.UIState.Error
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email (opcional) con validación básica
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = email.isNotBlank() && !isValidEmail(email),
                        supportingText = {
                            if (email.isNotBlank() && !isValidEmail(email)) {
                                Text("Email inválido")
                            }
                        }
                    )
                }
            }

            // ========== DIRECCIÓN PRINCIPAL ==========
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Dirección Principal",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // ✅ DROPDOWN PAÍS
                    ExposedDropdownMenuBox(
                        expanded = paisExpanded,
                        onExpandedChange = { paisExpanded = !paisExpanded }
                    ) {
                        OutlinedTextField(
                            value = paisSeleccionado?.nombre ?: "Seleccione país",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("País *") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = paisExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = paisExpanded,
                            onDismissRequest = { paisExpanded = false }
                        ) {
                            paises.forEach { pais ->
                                DropdownMenuItem(
                                    text = { Text(pais.nombre) },
                                    onClick = {
                                        paisSeleccionado = pais
                                        paisExpanded = false
                                        // Limpiar datos de ubicación si cambia país
                                        if (pais.codigo != "ESP") {
                                            codigoPostalText = ""
                                            codigoPostalSeleccionado = null
                                            provinciaSeleccionada = null
                                            provinciaText = ""
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calle
                    OutlinedTextField(
                        value = calle,
                        onValueChange = { calle = it },
                        label = { Text("Calle *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = calle.isBlank() && uiState is NuevoClienteViewModel.UIState.Error
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ✅ NÚMERO + PISO + PUERTA EN MISMA FILA
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = numeroCalle,
                            onValueChange = { numeroCalle = it },
                            label = { Text("Número") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = piso,
                            onValueChange = { piso = it },
                            label = { Text("Piso") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = puerta,
                            onValueChange = { puerta = it },
                            label = { Text("Puerta") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ✅ CÓDIGO POSTAL CON LÓGICA INTELIGENTE
                    if (paisSeleccionado?.codigo == "ESP") {
                        // España: Dropdown con búsqueda
                        ExposedDropdownMenuBox(
                            expanded = codigoPostalExpanded,
                            onExpandedChange = { codigoPostalExpanded = !codigoPostalExpanded }
                        ) {
                            OutlinedTextField(
                                value = codigoPostalText,
                                onValueChange = {
                                    if (it.all { c -> c.isDigit() } && it.length <= 5) {
                                        codigoPostalText = it
                                        codigoPostalExpanded = true
                                    }
                                },
                                label = { Text("Código Postal") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = codigoPostalExpanded)
                                }
                            )

                            if (codigosPostalesFiltrados.isNotEmpty()) {
                                ExposedDropdownMenu(
                                    expanded = codigoPostalExpanded,
                                    onDismissRequest = { codigoPostalExpanded = false }
                                ) {
                                    codigosPostalesFiltrados.take(10).forEach { cp ->
                                        DropdownMenuItem(
                                            text = { Text("${cp.codigoPostal} - ${cp.ciudad}") },
                                            onClick = {
                                                codigoPostalSeleccionado = cp
                                                codigoPostalText = cp.codigoPostal
                                                codigoPostalExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Otros países: Campo libre (solo números)
                        OutlinedTextField(
                            value = codigoPostalText,
                            onValueChange = { if (it.all { c -> c.isDigit() }) codigoPostalText = it },
                            label = { Text("Código Postal") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ✅ CIUDAD CON DROPDOWN Y BÚSQUEDA
                    if (paisSeleccionado?.codigo == "ESP") {
                        ExposedDropdownMenuBox(
                            expanded = ciudadExpanded,
                            onExpandedChange = { ciudadExpanded = !ciudadExpanded }
                        ) {
                            OutlinedTextField(
                                value = ciudadText,
                                onValueChange = {
                                    ciudadText = it
                                    ciudadExpanded = true
                                },
                                label = { Text("Ciudad *") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                singleLine = true,
                                isError = ciudadText.isBlank() && uiState is NuevoClienteViewModel.UIState.Error,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = ciudadExpanded)
                                }
                            )

                            if (ciudadesFiltradas.isNotEmpty()) {
                                ExposedDropdownMenu(
                                    expanded = ciudadExpanded,
                                    onDismissRequest = { ciudadExpanded = false }
                                ) {
                                    ciudadesFiltradas.take(10).forEach { ciudad ->
                                        DropdownMenuItem(
                                            text = { Text(ciudad) },
                                            onClick = {
                                                ciudadText = ciudad
                                                ciudadExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = ciudadText,
                            onValueChange = { ciudadText = it },
                            label = { Text("Ciudad *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = ciudadText.isBlank() && uiState is NuevoClienteViewModel.UIState.Error
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ✅ PROVINCIA CON DROPDOWN Y BÚSQUEDA
                    if (paisSeleccionado?.codigo == "ESP") {
                        ExposedDropdownMenuBox(
                            expanded = provinciaExpanded,
                            onExpandedChange = { provinciaExpanded = !provinciaExpanded }
                        ) {
                            OutlinedTextField(
                                value = provinciaText,
                                onValueChange = {
                                    provinciaText = it
                                    provinciaExpanded = true
                                },
                                label = { Text("Provincia *") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                singleLine = true,
                                isError = provinciaText.isBlank() && uiState is NuevoClienteViewModel.UIState.Error,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = provinciaExpanded)
                                }
                            )

                            if (provinciasFiltradas.isNotEmpty()) {
                                ExposedDropdownMenu(
                                    expanded = provinciaExpanded,
                                    onDismissRequest = { provinciaExpanded = false }
                                ) {
                                    provinciasFiltradas.forEach { provincia ->
                                        DropdownMenuItem(
                                            text = { Text(provincia.provincia) },
                                            onClick = {
                                                provinciaSeleccionada = provincia
                                                provinciaText = provincia.provincia
                                                provinciaExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = provinciaText,
                            onValueChange = { provinciaText = it },
                            label = { Text("Provincia *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = provinciaText.isBlank() && uiState is NuevoClienteViewModel.UIState.Error
                        )
                    }
                }
            }

            // Error general
            if (uiState is NuevoClienteViewModel.UIState.Error) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = (uiState as NuevoClienteViewModel.UIState.Error).message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Botón Guardar
            Button(
                onClick = {
                    if (formularioValido) {
                        viewModel.insertarCliente(
                            nombre = nombre,
                            apellido = apellido,
                            // Igual que arriba: aquí puedes cambiar a código si ajustas el ViewModel
                            tipoDocumentoId = tipoDocumentoSeleccionado!!.id,
                            numeroDocumento = numeroDocumento,
                            telefonoPrincipal = telefono,
                            email = if (email.isNotBlank()) email else null,
                            calle = calle,
                            numeroCalle = numeroCalle,
                            piso = piso,
                            puerta = puerta,
                            codigoPostal = codigoPostalText,
                            ciudad = ciudadText,
                            provincia = provinciaText,
                            pais = paisSeleccionado?.nombre ?: "España"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = formularioValido && uiState !is NuevoClienteViewModel.UIState.Loading
            ) {
                if (uiState is NuevoClienteViewModel.UIState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Guardar Cliente")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun validarFormulario(
    nombre: String,
    apellido: String,
    tipoDocumento: TipoDocumento?,
    numeroDocumento: String,
    numeroDocumentoError: String?,
    telefono: String,
    calle: String,
    ciudad: String,
    provincia: String,
    email: String
): Boolean {
    return nombre.isNotBlank() &&
            apellido.isNotBlank() &&
            tipoDocumento != null &&
            numeroDocumento.isNotBlank() &&
            numeroDocumentoError == null &&
            telefono.isNotBlank() &&
            calle.isNotBlank() &&
            ciudad.isNotBlank() &&
            provincia.isNotBlank() &&
            (email.isBlank() || isValidEmail(email))
}

private fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
