package com.github.jsbxyyx.teamcity.notification.cn;

import jetbrains.buildServer.Build;
import jetbrains.buildServer.notification.NotificatorAdapter;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.UserPropertyInfo;
import jetbrains.buildServer.users.SUser;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * wework notification
 *
 * @author jsbxyyx
 * @since
 */
public class CnNotifier extends NotificatorAdapter {

    private static final Logger log = LoggerFactory.getLogger(CnNotifier.class);

    public CnNotifier(NotificatorRegistry notificatorRegistry) {
        log.info("WeworkNotifier init...");
        List<UserPropertyInfo> list = new ArrayList<UserPropertyInfo>();
        notificatorRegistry.register(this, list);
    }

    public void notifyBuildStarted(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> set) {

    }

    public void notifyBuildSuccessful(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> set) {
        try {
            Iterator<SUser> iter = set.iterator();
            while (iter.hasNext()) {
                SUser user = iter.next();
                doNotifications(sRunningBuild, user, "执行成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyBuildFailed(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> set) {
        try {
            Iterator<SUser> iter = set.iterator();
            while (iter.hasNext()) {
                SUser user = iter.next();
                doNotifications(sRunningBuild, user, "执行失败，请检查");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NotNull
    public String getNotificatorType() {
        return "cn notifier";
    }

    @NotNull
    public String getDisplayName() {
        return "cn notifier";
    }

    private void doNotifications(Build build, SUser user, String msg) {
        String projectName = build.getFullName();
        String buildNumber = build.getBuildNumber();
        // env.WEBHOOK_URL为TeamCity中需要配置的参数
        String url = build.getBuildType().getBuildParameter("env.WEBHOOK_URL");
        String atMobileText = build.getBuildType().getBuildParameter("env.AT_MOBILE");
        StringBuilder atMobile = new StringBuilder("[");
        if (atMobileText != null && atMobileText.trim().length() != 0) {
            String[] split = atMobileText.split(",");
            for (int i = 0; i < split.length; i++) {
                if (i > 0) {
                    atMobile.append(",");
                }
                atMobile.append("\"").append(split[i]).append("\"");
            }
        }
        atMobile.append("]");

        StringBuilder extParam = new StringBuilder();
        String extParamText = build.getBuildType().getBuildParameter("env.EXT_PARAM");
        if (extParamText != null && extParamText.trim().length() != 0) {
            String[] split = extParamText.split(",");
            for (int i = 0; i < split.length; i++) {
                if (i > 0) {
                    extParam.append("\n");
                }
                String key = split[i];
                String value = build.getBuildType().getBuildParameter(key);
                extParam.append(key).append(":").append(value);
            }
        }

        String content = "※TeamCity提醒※\n项目：" + projectName + "，版本号：" + buildNumber + "\n"
                + "参数：\n" +  extParam + "\n" + msg;

        String body = "{\"text\":{"
                + "\"content\":"
                + "\"" + Escape.escapeJson(content) + "\""
                + ",\"mentioned_mobile_list\":" + atMobile
                + "}"
                + ",\"msgtype\":\"text\"}";

        sendPost(url, body);
    }

    public static String sendPost(String url, String param) {
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
            // post设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            out.print(param);
            out.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append("\n").append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

}
