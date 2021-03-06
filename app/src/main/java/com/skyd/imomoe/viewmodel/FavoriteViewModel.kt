package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.FavoriteAnimeBean
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.util.Util.showToastOnThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.collections.ArrayList


class FavoriteViewModel : ViewModel() {
    var favoriteList: MutableList<FavoriteAnimeBean> = ArrayList()
    var mldFavoriteList: MutableLiveData<Boolean> = MutableLiveData()

    fun getFavoriteData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                favoriteList.clear()
                favoriteList.addAll(getAppDataBase().favoriteAnimeDao().getFavoriteAnimeList())
                favoriteList.sortWith(Comparator { o1, o2 ->
                    // 负数表示按时间戳从大到小排列
                    -o1.time.compareTo(o2.time)
                })
                mldFavoriteList.postValue(true)
            } catch (e: Exception) {
                favoriteList.clear()
                mldFavoriteList.postValue(false)
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }
    }

    companion object {
        const val TAG = "FavoriteViewModel"
    }
}