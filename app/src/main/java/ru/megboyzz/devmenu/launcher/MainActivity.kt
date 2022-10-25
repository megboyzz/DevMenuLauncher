package ru.megboyzz.devmenu.launcher

import android.app.Application
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import ru.megboyzz.devmenu.launcher.ui.theme.MainBlue
import ru.megboyzz.devmenu.launcher.ui.theme.MainWhite
import java.net.ServerSocket


class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"
    lateinit var mViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = MainViewModel(this.application)

        setContent {

            Scaffold(
                topBar = { AppBar() },
                content = { MainContent() },
                backgroundColor = MainWhite
            )

        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.loadDevMenuAppsList()
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


@Composable
fun MainContent(){
    Column {

        val context = LocalContext.current
        val mViewModel: MainViewModel =
            viewModel(factory = MainViewModelFactory(context.applicationContext as Application))
        val observeAsState = mViewModel.ip.observeAsState()
        observeAsState.value?.let {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                textAlign = TextAlign.Center,
                text = it
            )
        }

        val devMenuList by mViewModel.devMenuAppsList.observeAsState()

        if(devMenuList?.isEmpty() == true) {
            CircularProgressIndicator()
        }
        else {
            devMenuList?.forEach {
                devMenuAppCard(it)
            }
        }


    }


}


@Preview(showBackground = true)
@Composable
fun cardPrev(){
    val drawable = LocalContext
        .current
        .getDrawable(R.drawable.ic_launcher_background)
    if(drawable != null) {
        val devMenuApp = DevMenuApp(
            "NFSMW",
            drawable,
            ""
        )
        devMenuAppCard(devMenuApp)
    }
}

@Composable
fun devMenuAppCard(app: DevMenuApp){

    val portText = remember {mutableStateOf("")}
    val context = LocalContext.current

    Box(Modifier.padding(10.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, MainBlue),
            elevation = 0.dp
        ) {

            Box(Modifier.padding(10.dp)){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Center),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    Image(rememberDrawablePainter(app.icon), "")
                    Column(Modifier.padding(5.dp)) {
                        Text(
                            text = app.name,
                            //modifier = Modifier.fillMaxWidth()
                        )
                        PortInput(portText)
                        Button(
                            onClick = {
                                val serverSocket = ServerSocket(0)
                                portText.value = serverSocket.localPort.toString()
                            },
                            shape = RoundedCornerShape(10.dp),
                        ) {
                            Text(
                                text = "Сгенерировать",
                                fontWeight = FontWeight.Light,
                                fontSize = 10.sp
                            )
                        }
                    }
                    val devMenuEnabled = remember { mutableStateOf(false)}
                    Switch(
                        checked = devMenuEnabled.value,
                        onCheckedChange = {
                            if(it)
                                app.start(portText.value.toInt(), context)
                            else
                                app.stop(context)
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