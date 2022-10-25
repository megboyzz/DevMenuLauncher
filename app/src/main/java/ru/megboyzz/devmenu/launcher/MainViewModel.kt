package ru.megboyzz.devmenu.launcher

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
import java.net.NetworkInterface
import java.net.SocketException

class MainViewModel (application: Application) : AndroidViewModel(application) {

    val ip: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val devMenuAppsList: MutableLiveData<List<DevMenuApp>> by lazy {
        MutableLiveData<List<DevMenuApp>>(mutableListOf())
    }


    init{
        loadDevMenuAppsList()
        updateWirelessState()
    }

    fun updateWirelessState(){
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                ip.postValue(getWifiApIpAddress())
            }
        }
    }

    // Эту штуку можно дергать из lifecycle-методов активности
    fun loadDevMenuAppsList() {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>().applicationContext
            val pm = context.packageManager
            val packages =
                pm.getInstalledPackages(PackageManager.GET_META_DATA or PackageManager.GET_SERVICES)

            for (packageInfo in packages) {
                val services = packageInfo.services
                if (services != null)
                    for (service in services)
                        if (service.name == devMenuServiceClassName) {
                            val icon = packageInfo.applicationInfo.loadIcon(context.packageManager)
                            val name = packageInfo.applicationInfo.loadLabel(context.packageManager)
                                .toString()

                            val app = DevMenuApp(
                                name,
                                icon,
                                packageInfo.packageName
                            )

                            devMenuAppsList.postValue(devMenuAppsList.value?.plus(app))
                        }
            }
        }
    }

    //TODO говнокод переписать
    private fun getWifiApIpAddress(): String? {
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

}

class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java))
            return MainViewModel(application = application) as T

        throw  IllegalArgumentException("Unknown ViewModel Class")
    }

}