package ru.megboyzz.devmenu.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import ru.megboyzz.devmenu.launcher.ui.theme.MainBlue
import ru.megboyzz.devmenu.launcher.ui.theme.MainWhite

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Scaffold(
                topBar = { AppBar() },
                content = { MainContent() },
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

@Preview
@Composable
fun MainContent(){
    Column {
        Spacer(Modifier.size(45.dp))
        TextInCenter(
            text = "Включить DevMenu",
            color = Color.Black
        )
        Switch(
            checked = false,
            onCheckedChange = {},
            modifier = Modifier.align(CenterHorizontally)
        )
        Spacer(Modifier.size(45.dp))
        TextInCenter(
            text = "Порт клиентской части DevMenu",
            color = Color.Black
        )
        PortInput()
        Spacer(Modifier.size(20.dp))
        TextButton(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MainBlue)
                .size(80.dp, 30.dp)
                .align(CenterHorizontally),
            onClick = { /*TODO*/ }
        ) {
            Text(
                text = "Установить",
                fontSize = 9.sp,
                color = Color.White
            )
        }
        Spacer(Modifier.size(30.dp))

        TextInCenter(
            text = "События",
            color = Color.Black
        )

        EventLogField()


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

@Preview
@Composable
fun PortInput(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        contentAlignment = Center
    ){
        BasicTextField(
            value = "8080",
            onValueChange = {},
            singleLine = true,
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
        value = "lol",
        onValueChange = {},
        readOnly = true,
        decorationBox = { innerText ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
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