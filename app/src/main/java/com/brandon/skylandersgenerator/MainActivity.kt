package com.brandon.skylandersgenerator

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.core.view.WindowCompat

private val Forest = Color(0xFF12352A)
private val ForestDeep = Color(0xFF0D2B21)
private val ForestSurface = Color(0xFF1A4436)
private val ForestRaised = Color(0xFF244C3D)
private val Leaf = Color(0xFFD4F45B)
private val Ember = Color(0xFFF07C42)
private val Sky = Color(0xFF73CDE0)
private val Ink = Color(0xFFF5F2E8)
private val Muted = Color(0xFFC5D4C9)

private val SkylandersColors = darkColorScheme(
    primary = Leaf,
    onPrimary = ForestDeep,
    secondary = Ember,
    onSecondary = ForestDeep,
    tertiary = Sky,
    onTertiary = ForestDeep,
    background = Forest,
    onBackground = Ink,
    surface = ForestSurface,
    onSurface = Ink,
    surfaceVariant = ForestRaised,
    onSurfaceVariant = Muted,
    outline = Color(0xFF6F8B7A)
)

private data class Skylander(
    val name: String,
    val element: String,
    val image: Int
)

private val giantsRoster = listOf(
    Skylander("Tree Rex", "Vida", R.drawable.tree_rex),
    Skylander("Ninjini", "Magia", R.drawable.ninjini),
    Skylander("Bouncer", "Tecnología", R.drawable.bouncer),
    Skylander("Hot Head", "Fuego", R.drawable.hot_head)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MaterialTheme(colorScheme = SkylandersColors) {
                SkylandersApp()
            }
        }
    }
}

@Composable
private fun SkylandersApp() {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val adapter = remember(context) { NfcAdapter.getDefaultAdapter(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    var selectedTab by remember { mutableStateOf(0) }
    var nfcEnabled by remember { mutableStateOf(adapter?.isEnabled == true) }
    var tagMessage by remember { mutableStateOf<String?>(null) }
    var tagTechnologies by remember { mutableStateOf<List<String>>(emptyList()) }

    DisposableEffect(lifecycleOwner, adapter) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                nfcEnabled = adapter?.isEnabled == true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    DisposableEffect(activity, adapter, selectedTab, nfcEnabled) {
        if (selectedTab == 1 && nfcEnabled && activity != null && adapter != null) {
            adapter.enableReaderMode(
                activity,
                { tag: Tag ->
                    val technologies = tag.techList.map { it.substringAfterLast('.') }
                    val message = when {
                        "MifareClassic" in technologies -> "Etiqueta MIFARE Classic detectada"
                        "NfcA" in technologies -> "Etiqueta NFC-A detectada"
                        else -> "Etiqueta NFC detectada"
                    }
                    Handler(Looper.getMainLooper()).post {
                        tagMessage = message
                        tagTechnologies = technologies
                    }
                },
                NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or
                    NfcAdapter.FLAG_READER_NFC_V,
                null
            )
        }

        onDispose {
            if (selectedTab == 1 && activity != null && adapter != null) {
                adapter.disableReaderMode(activity)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = ForestDeep, contentColor = Ink) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { ElementIcon(IconKind.Collection, if (selectedTab == 0) Leaf else Muted) },
                    label = { Text("Colección") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        tagMessage = null
                        tagTechnologies = emptyList()
                    },
                    icon = { ElementIcon(IconKind.Nfc, if (selectedTab == 1) Leaf else Muted) },
                    label = { Text("Probar NFC") }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> CollectionScreen(
                nfcEnabled = nfcEnabled,
                onTryNfc = { selectedTab = 1 },
                modifier = Modifier.padding(innerPadding)
            )
            else -> NfcScreen(
                hasNfc = adapter != null,
                nfcEnabled = nfcEnabled,
                tagMessage = tagMessage,
                tagTechnologies = tagTechnologies,
                onOpenSettings = {
                    context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun CollectionScreen(
    nfcEnabled: Boolean,
    onTryNfc: () -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var selectedElement by remember { mutableStateOf("Todos") }
    val elements = listOf("Todos", "Vida", "Magia", "Tecnología", "Fuego")
    val filtered = giantsRoster.filter { skylander ->
        (selectedElement == "Todos" || skylander.element == selectedElement) &&
            skylander.name.contains(query, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SKYLANDERS", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
            NfcStatusPill(enabled = nfcEnabled)
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Elige un Skylander", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("GIANTS  ·  XBOX 360", style = MaterialTheme.typography.labelMedium, color = Muted, letterSpacing = 1.sp)
        }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Buscar personaje") },
            shape = RoundedCornerShape(14.dp)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(elements) { element ->
                FilterChip(
                    selected = selectedElement == element,
                    onClick = { selectedElement = element },
                    label = { Text(element) }
                )
            }
        }

        Text("Guardianes de Giants", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (filtered.isEmpty()) {
            Text("No hay personajes con ese nombre o elemento.", color = Muted, modifier = Modifier.padding(vertical = 20.dp))
        } else {
            filtered.forEachIndexed { index, skylander ->
                SkylanderRow(skylander = skylander, featured = index == 0)
            }
        }

        Surface(
            color = ForestSurface,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF527263))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElementIcon(IconKind.Nfc, Leaf)
                Column(modifier = Modifier.weight(1f)) {
                    Text(if (nfcEnabled) "NFC activo" else "NFC no disponible", fontWeight = FontWeight.SemiBold)
                    Text("Comprueba si el teléfono detecta una etiqueta.", style = MaterialTheme.typography.bodySmall, color = Muted)
                }
                Button(onClick = onTryNfc) { Text("Probar") }
            }
        }
    }
}

@Composable
private fun SkylanderRow(skylander: Skylander, featured: Boolean) {
    val shape = RoundedCornerShape(if (featured) 20.dp else 14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (featured) ForestSurface else ForestRaised)
            .border(1.dp, Color(0xFF527263), shape)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Image(
            painter = painterResource(skylander.image),
            contentDescription = "Arte de ${skylander.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = if (featured) 102.dp else 60.dp, height = if (featured) 150.dp else 82.dp)
                .clip(RoundedCornerShape(11.dp))
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(skylander.element.uppercase(), style = MaterialTheme.typography.labelSmall, color = Leaf, letterSpacing = 1.sp)
            Text(skylander.name, style = if (featured) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Skylanders Giants", style = MaterialTheme.typography.bodySmall, color = Muted)
            if (featured) {
                Spacer(Modifier.height(3.dp))
                Text("Personaje del catálogo local", style = MaterialTheme.typography.bodySmall, color = Muted)
            }
        }
    }
}

@Composable
private fun NfcScreen(
    hasNfc: Boolean,
    nfcEnabled: Boolean,
    tagMessage: String?,
    tagTechnologies: List<String>,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text("Probar NFC", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Diagnóstico de lectura", style = MaterialTheme.typography.titleMedium, color = Leaf)

        Surface(
            color = ForestSurface,
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, Color(0xFF527263))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(76.dp).clip(CircleShape).background(ForestRaised),
                    contentAlignment = Alignment.Center
                ) {
                    ElementIcon(IconKind.Nfc, Leaf, modifier = Modifier.size(38.dp))
                }
                Text(
                    when {
                        !hasNfc -> "Este teléfono no informa NFC"
                        !nfcEnabled -> "Activa NFC para continuar"
                        tagMessage != null -> tagMessage
                        else -> "Acerca una etiqueta NFC"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    when {
                        !hasNfc -> "La lectura NFC no está disponible en este dispositivo."
                        !nfcEnabled -> "Activa NFC en ajustes y vuelve a esta pantalla."
                        tagMessage != null -> "La app solo detecta la tecnología; no lee ni modifica los datos de la etiqueta."
                        else -> "Mantén la etiqueta cerca de la parte trasera del teléfono."
                    },
                    color = Muted,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (tagTechnologies.isNotEmpty()) {
                    Text(
                        tagTechnologies.distinct().joinToString(" · "),
                        style = MaterialTheme.typography.labelMedium,
                        color = Sky
                    )
                }
                if (hasNfc && !nfcEnabled) {
                    Button(onClick = onOpenSettings) { Text("Abrir ajustes NFC") }
                }
            }
        }

        Text(
            "La compatibilidad con MIFARE Classic depende del teléfono y de la etiqueta; esta prueba solo informa lo que Android detecta.",
            style = MaterialTheme.typography.bodySmall,
            color = Muted
        )
    }
}

@Composable
private fun NfcStatusPill(enabled: Boolean) {
    Row(
        modifier = Modifier.clip(CircleShape).background(ForestSurface).padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(if (enabled) Leaf else Ember))
        Text(if (enabled) "NFC activo" else "NFC apagado", style = MaterialTheme.typography.labelSmall)
    }
}

private enum class IconKind { Collection, Nfc }

@Composable
private fun ElementIcon(kind: IconKind, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(22.dp)) {
        val stroke = Stroke(width = 1.8.dp.toPx())
        if (kind == IconKind.Collection) {
            val gap = size.width * .16f
            val cell = (size.width - gap * 3) / 2
            for (row in 0..1) for (column in 0..1) {
                drawRoundRect(
                    color = color,
                    topLeft = androidx.compose.ui.geometry.Offset(gap + column * (cell + gap), gap + row * (cell + gap)),
                    size = androidx.compose.ui.geometry.Size(cell, cell),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
                    style = stroke
                )
            }
        } else {
            val centerY = size.height / 2
            for (index in 0..2) {
                val inset = index * size.width * .2f
                drawArc(
                    color = color,
                    startAngle = -58f,
                    sweepAngle = 116f,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(inset, size.height * .08f + inset * .3f),
                    size = androidx.compose.ui.geometry.Size(size.width - inset * 2, size.height * .84f - inset * .6f),
                    style = stroke
                )
            }
            drawCircle(color, radius = 1.5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(size.width * .84f, centerY))
        }
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
