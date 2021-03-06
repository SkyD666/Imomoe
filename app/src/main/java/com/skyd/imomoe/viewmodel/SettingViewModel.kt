package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.util.Util.getDirectorySize
import com.skyd.imomoe.util.Util.getFormatSize
import com.skyd.imomoe.util.Util.showToastOnThread
import com.skyd.imomoe.util.glide.GlideUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class SettingViewModel : ViewModel() {
    var mldDeleteAllHistory: MutableLiveData<Boolean> = MutableLiveData()
    var mldClearAllCache: MutableLiveData<Boolean> = MutableLiveData()
    var mldCacheSize: MutableLiveData<String> = MutableLiveData()

    fun deleteAllHistory() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                getAppDataBase().historyDao().deleteAllHistory()
                getAppDataBase().searchHistoryDao().deleteAllSearchHistory()
                mldDeleteAllHistory.postValue(true)
            } catch (e: Exception) {
                mldDeleteAllHistory.postValue(false)
                e.printStackTrace()
                (App.context.getString(R.string.delete_failed) + "\n" + e.message).showToastOnThread()
            }
        }
    }

    // 获取Glide磁盘缓存大小
    fun getCacheSize() {
        GlobalScope.launch(Dispatchers.IO) {
            mldCacheSize.postValue(
                try {
                    getFormatSize(
                        getDirectorySize(File(App.context.cacheDir.path + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR))
                            .toDouble()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    "获取缓存大小失败"
                }
            )
        }
    }


    fun clearAllCache() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Glide
                GlideUtil.clearMemoryDiskCache()
                mldClearAllCache.postValue(true)
            } catch (e: Exception) {
                mldClearAllCache.postValue(false)
                e.printStackTrace()
                (App.context.getString(R.string.delete_failed) + "\n" + e.message).showToastOnThread()
            }
        }
    }

    companion object {
        const val TAG = "SettingViewModel"
    }
}