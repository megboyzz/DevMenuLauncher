package ru.megboyzz.devmenu.launcher

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun Int.instance(): String{
    return stringResource(id = this)
}
