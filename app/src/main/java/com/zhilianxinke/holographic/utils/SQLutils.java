package com.zhilianxinke.holographic.utils;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.zhilianxinke.holographic.entity.AppCommerical;
import com.zhilianxinke.holographic.entity.AppVideoInfo;
import com.zhilianxinke.holographic.entity.SearchHistory;
import com.zhilianxinke.holographic.entity.VideoFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ldb on 2016/1/25.
 */
public class SQLutils {
    /**
     * 保存搜索记录
     **/
    public static void saveSearchHostory(SearchHistory sh) {
        if (sh != null) {
            List<SearchHistory> list = findSearchHistoryByName(sh);
            if (list.size() <= 0) {
                sh.save();
            } else {
                for (SearchHistory sHistory : list) {
                    sHistory.setSearchTime(sh.getSearchTime());
                    sHistory.save();
                }
            }

        }
    }

    /**
     * 查找之前是否有搜索过
     **/
    public static List<SearchHistory> findSearchHistoryByName(SearchHistory sh) {
        return new Select().from(SearchHistory.class).where("searchName=?", sh.getSearchName()).execute();
    }

    /**
     * 查找所有的搜素记录
     **/
    public static List<SearchHistory> getAllSearchHistory() {
        return new Select().from(SearchHistory.class).orderBy("searchTime desc").execute();
    }

    /**
     * 删除所有的搜素记录
     **/
    public static void deleteAllSearchHistory() {
        new Delete().from(SearchHistory.class).execute();
    }


    /**
     * 保存首页Video
     **/
    public static void saveVideo(List<AppVideoInfo> af, int isFind) {
        if (af != null && af.size() > 0) {
            ActiveAndroid.beginTransaction();
            try {
                for (AppVideoInfo appVideoInfo : af) {
                    List<AppVideoInfo> list = findVideo(appVideoInfo);
                    if (list.size() <= 0) {
                        appVideoInfo.setIsFind(isFind);
                        appVideoInfo.save();
                    } else {
                        for (AppVideoInfo exitsInfo : list) {
                            exitsInfo.delete();
                        }
                        List<VideoFile> video = SDCardUtils.getAllDownLoadVideo();
                        if (video != null && video.size() > 0) {
                            for (VideoFile v : video) {
                                if (v.getFileName().contains(appVideoInfo.getName())) {
                                    appVideoInfo.setIsDownload(true);
                                    appVideoInfo.setLoacPath(v.getFilePath());
                                }
                            }
                        }
                        appVideoInfo.setIsFind(isFind);
                        appVideoInfo.save();
                    }
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }

        }
    }

    /**
     * 搜索到的视频与本地进行比对
     **/
    public static List<AppVideoInfo> findVideo(List<AppVideoInfo> af) {
        List<AppVideoInfo> list = new ArrayList<>();
        if (af != null && af.size() > 0) {
            for (AppVideoInfo appVideoInfo : af) {
                List<AppVideoInfo> video = new Select().from(AppVideoInfo.class).where("uuid=?", appVideoInfo.getUuid()).execute();
                list.addAll(video);
            }
            return list;
        } else {
            return af;
        }
    }

    /**
     * 查找之前是否有保存视频
     **/
    public static List<AppVideoInfo> findVideo(AppVideoInfo af) {
        return new Select().from(AppVideoInfo.class).where("uuid=?", af.getUuid()).execute();
    }

    /**
     * 找到发现 首页所有视频
     **/
    public static List<AppVideoInfo> getAlllVideo(int isFind) {
        return new Select().from(AppVideoInfo.class).where("isFind=?", isFind).orderBy("insertTime desc").execute();
    }

    /**
     * update 视频下载状态
     **/
    public static void updateVideo(AppVideoInfo af) {
        List<AppVideoInfo> list = new Select().from(AppVideoInfo.class).where("uuid=?", af.getUuid()).execute();
        if (list != null && list.size() > 0) {
            for (AppVideoInfo appVideoInfo : list) {
                List<VideoFile> video = SDCardUtils.getAllDownLoadVideo();
                if (video != null && video.size() > 0) {
                    for (VideoFile v : video) {
                        String name = v.getFileName();
                        if (name.contains(".")) {
                            name = name.substring(0, name.lastIndexOf("."));
                        }
                        if (name.equals(appVideoInfo.getName())) {
                            appVideoInfo.setIsDownload(true);
                            appVideoInfo.setLoacPath(v.getFilePath());
                        }
                    }
                }
                appVideoInfo.save();
            }
        }
    }


    public  static  void cancleDownlaod(AppVideoInfo af){
        List<AppVideoInfo> list = new Select().from(AppVideoInfo.class).where("uuid=?", af.getUuid()).execute();
        for (int i = 0; i < list.size(); i++) {
            AppVideoInfo appVideoInfo=  list.get(i);
            appVideoInfo.setIsDownload(false);
            appVideoInfo.setLoacPath("");
        }
    }

    /**
     * 保存广告
     **/
    public static void saveAppCommerical(List<AppCommerical> list) {
        if (list != null && list.size() > 0) {
            new Delete().from(AppCommerical.class).execute();
            for (AppCommerical ac : list
                    ) {
                ac.save();
            }
        }
    }

    /**
     * 获取广告
     **/
    public static List<AppCommerical> getAppCommerical() {
        return new Select().from(AppCommerical.class).execute();
    }
}
