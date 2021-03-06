package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.util.html.JsoupUtil
import com.skyd.imomoe.util.html.ParseHtmlUtil.parseLpic
import com.skyd.imomoe.util.Util.showToastOnThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.select.Elements
import java.lang.Exception
import java.util.*


class MonthAnimeViewModel : ViewModel() {
    var monthAnimeList: MutableList<AnimeCoverBean> = ArrayList()
    var mldMonthAnimeList: MutableLiveData<Boolean> = MutableLiveData()

    fun getMonthAnimeData(partUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = Api.MAIN_URL + partUrl
                val document = JsoupUtil.getDocument(url)
                val areaElements: Elements = document.getElementsByClass("area")
                monthAnimeList.clear()
                for (i in areaElements.indices) {
                    val areaChildren: Elements = areaElements[i].children()
                    for (j in areaChildren.indices) {
                        when (areaChildren[j].className()) {
                            "lpic" -> {
                                monthAnimeList.addAll(parseLpic(areaChildren[j], url))
                            }
                        }
                    }
                }
                mldMonthAnimeList.postValue(true)
            } catch (e: Exception) {
                monthAnimeList.clear()
                mldMonthAnimeList.postValue(false)
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }
    }

    companion object {
        const val TAG = "MonthAnimeViewModel"
    }
}