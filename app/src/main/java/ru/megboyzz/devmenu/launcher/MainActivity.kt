package ru.megboyzz.devmenu.launcher

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.widget.ToggleButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import ru.megboyzz.devmenu.launcher.ui.theme.MainBlue
import ru.megboyzz.devmenu.launcher.ui.theme.MainWhite
import java.net.NetworkInterface
import java.net.SocketException
import kotlin.concurrent.thread


class MainActivity : ComponentActivity() {

    private val devMenuPackageName = "com.devmenu.server.AppService"
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pm = packageManager
        val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA or PackageManager.GET_SERVICES)
        val devMenuAppsList = mutableListOf<PackageInfo>()

        //Шерстим телефон на наличие приложухи с интегрированным сервисом
        thread {
            Thread.sleep(500)
            for (packageInfo in packages) {
                val services = packageInfo.services
                if (services != null)
                    for (service in services)
                        devMenuAppsList += packageInfo

            }
        }

        setContent {

            Scaffold(
                topBar = { AppBar() },
                content = { MainContent(devMenuAppsList) },
                backgroundColor = MainWhite
            )

        }
    }
}

@Composable
fun TextInCenter(
    text: String,
    size: Int = 16,
    color: Color = MainWhite
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 5.dp, 0.dp, 5.dp),
        contentAlignment = Center

    ) {
        TextLabel(text, size, color)
    }
}

fun isHotSpotEnabled(context: Context): Boolean {
    val wifiManager =
        context
            .applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager
    val method = wifiManager.javaClass.getMethod("getWifiApState")
    method.isAccessible = true
    /*
    AP_STATE_DISABLING = 10;
    AP_STATE_DISABLED = 11;
    AP_STATE_ENABLING = 12;
    AP_STATE_ENABLED = 13;
    AP_STATE_FAILED = 14;
     */
    return (method.invoke(wifiManager) as Int) == 13
}

//TODO говнокод переписать
fun getWifiApIpAddress(): String? {
    try {
        val en = NetworkInterface.getNetworkInterfaces()
        while (en.hasMoreElements()) {
            val intf = en.nextElement()
            if (intf.name.contains("wlan")) {
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress.address.size == 4) {
                        return inetAddress.hostAddress
                    }
                }
                continue
            }
        }
    } catch (ex: SocketException) {
        Log.e("ex", ex.toString())
    }
    return null
}


@Composable
fun MainContent(list: List<PackageInfo>){
    Column {

        val rememberList = remember {
            mutableStateOf(list)
        }

        val text = remember {
            mutableStateOf("")
        }
        var ip = getWifiApIpAddress()
        if(ip != null)
            text.value = "IP адрес текущей сети: $ip"
        else
            text.value = "Для использования DevMenu подключитесь к WiFi или включите точку доступа"

        thread {
            Thread.sleep(1000)
            while(true){
                ip = getWifiApIpAddress()
                if(ip != null)
                    text.value = "IP адрес текущей сети: $ip"
                else
                    text.value = "Для использования DevMenu подключитесь к WiFi или включите точку доступа"
            }
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            textAlign = TextAlign.Center,
            text = Snapshot.current.enter { text.value }
        )
        rememberList.value.forEach { devMenuAppCard(it) }

    }


}

@Composable
fun devMenuAppCard(info: PackageInfo){
    val current = LocalContext.current
    val Icon = info.applicationInfo.loadIcon(current.packageManager)
    val name = info.applicationInfo.loadLabel(current.packageManager).toString()
    Box(Modifier.padding(10.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, MainBlue),
            elevation = 0.dp
        ) {

            Box(Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){

                    //
                    val portText = remember {mutableStateOf("")}
                    Row{
                        Image(rememberDrawablePainter(Icon), "")
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(text = name)
                            PortInput(port = portText)
                        }
                    }
                    //TODO реализовать проверку работы сервиса и запись результата в состояние
                    val devMenuEnabled = remember { mutableStateOf(false)}
                    Switch(
                        checked = devMenuEnabled.value,
                        onCheckedChange = {
                            devMenuEnabled.value = it
                        }
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun AppBar(){
    TopAppBar(
        backgroundColor = MainBlue
    ) {
        Spacer(Modifier.size(10.dp))

        TextLabel(R.string.app_name.instance())

        Spacer(Modifier.weight(1f, true))
        IconButton(
            onClick = {TODO("Реализовать вывод инфы")},
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "info",
                tint = MainWhite
            )
        }
    }
}


/**
 * Основная текстовая метка
 */
@Composable
fun TextLabel(
    text: String,
    size: Int = 16,
    color: Color = MainWhite
){
    Text(
        text = text,
        fontWeight = FontWeight.W400,
        fontFamily = FontFamily.Default,
        fontSize = size.sp,
        color = color
    )
}

@Composable
fun PortInput(port: MutableState<String>){
    Box(
        modifier = Modifier
            .padding(0.dp, 5.dp, 0.dp, 0.dp)
    ){
        BasicTextField(
            value = port.value,
            onValueChange = {
                if(it.length <= 4)
                port.value = it
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            textStyle = LocalTextStyle.current
                .copy(fontFamily = FontFamily.Default)
                .copy(fontWeight = FontWeight.W400)
                .copy(fontSize = 16.sp)
                .copy(textAlign = TextAlign.Center),
            decorationBox = { innerText ->
                Box(
                    modifier = Modifier
                        .size(80.dp, 30.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .border(1.dp, MainBlue, RoundedCornerShape(10.dp)),
                    contentAlignment = Center
                ){ innerText() }
            }
        )
    }
}


@Preview
@Composable
fun EventLogField(){

    BasicTextField(
        value = "",
        onValueChange = {

        },
        readOnly = true,
        decorationBox = { innerText ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(10.dp, 0.dp, 10.dp, 30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(1.dp, MainBlue, RoundedCornerShape(10.dp))
            ){
                //Что то типа марджина
                Box(Modifier.padding(10.dp, 20.dp, 10.dp, 10.dp)) {
                    innerText()
                }
            }
        }
    )
}