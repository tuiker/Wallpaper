package com.myretrofit2.api

class AppUrl {
    companion object {
        const val API = "http://192.168.0.27:8080/"
//        const val API = "http://wallpaper.miss9u.xyz:8080/"

        const val userLogin = "mobile/consumer/consumerLogin" //用户登录
        const val userUpdate = "mobile/consumer/update" //修改用户信息
        const val getUserInfo = "mobile/consumer/getUserInfo" //获取用户信息
        const val sendSms = "mobile/consumer/sendRegisterOrLoginSms" //发送验证码
        const val wallpaperPageList = "mobile/wallpaper/pageList" //分页查询壁纸列表
        const val getWallpaperDetailsID = "mobile/wallpaper/getWallpaperDetailsInfo" //根据ID获取壁纸详细
        const val wallpaperHomeListAll = "mobile/wallpaper/getHomeData" //首页壁纸
        const val wallpaperListAll = "mobile/category/listAll" //查询所有壁纸分类
        const val getWallpaperAdv = "mobile/adv/getDownloadBeforeAdv" //广告信息
        const val imageFile = "comm/fileUpload/uploadFile" //图片上传
        const val userFeedBack = "mobile/feedback/addFeedback" //用户反馈
        const val historyList = "mobile/downloadRecord/pageList" //壁纸历史记录
        const val addCollect = "mobile/favorites/addCollect" //收藏壁纸
        const val collectList = "mobile/favorites/getMyCollect" //分页查询收藏壁纸
        const val getMyFavoritesList = "mobile/favorites/getMyFavorites" //收藏夹
        const val setWallpaperRecord = "mobile/downloadRecord/addDownloadRecord"//设置壁纸记录埋点
        const val clickAdRecord = "/mobile/advTriggerRecord/addAdvTriggerRecord"//点击广告记录埋点
        const val favoritesWallpaper = "mobile/wallpaper/pageWallpaperInFavorites"//根据ID查询收藏夹里的壁纸
        const val unFavorite = "mobile/favorites/cancelCollect" //取消收藏
        const val saveSearchKeyword = "mobile/hotSearch/saveSearchKeyword" //保存热搜关键词
        const val findTopHotSearchList = "mobile/hotSearch/findTopHotSearch" //热搜关键词列表
    }
}