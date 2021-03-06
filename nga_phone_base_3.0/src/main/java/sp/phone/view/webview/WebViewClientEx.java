package sp.phone.view.webview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.android.arouter.launcher.ARouter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.arouter.ARouterConstants;
import gov.anzong.androidnga.gallery.ImageZoomActivity;
import sp.phone.common.PhoneConfiguration;
import sp.phone.util.StringUtils;

public class WebViewClientEx extends WebViewClient {

    private static final String NGA_CN_BOARD_PREFIX = "http://bbs.ngacn.cc/thread.php?";

    private static final String NGA_178_BOARD_PREFIX = "http://nga.178.com/thread.php?";

    private static final String NGA_CN_THREAD_PREFIX = "http://bbs.ngacn.cc/read.php?";

    private static final String NGA_178_THREAD_PREFIX = "http://nga.178.com/read.php?";

    private static final String NGA_CN_BOARD_PREFIX_NO_HTTP = "bbs.ngacn.cc/thread.php?";

    private static final String NGA_178_BOARD_PREFIX_NO_HTTP = "nga.178.com/thread.php?";

    private static final String NGA_CN_THREAD_PREFIX_NO_HTTP = "bbs.ngacn.cc/read.php?";

    private static final String NGA_178_THREAD_PREFIX_NO_HTTP = "nga.178.com/read.php?";

    private List<String> mImgUrlList;

    private static final String[] NGA_USER_PROFILE_START = {
            "http://bbs.ngacn.cc/nuke.php?func=ucp&username=",
            "http://bbs.nga.cn/nuke.php?func=ucp&username=",
    };

    private static final String NGA_USER_PROFILE_END = "&";

    public WebViewClientEx(Context context) {
        super();
    }

    public WebViewClientEx() {
        super();
    }

    public void setImgUrls(List<String> list) {
        mImgUrlList = list;
    }

    private boolean overrideProfileUrlLoading(Context context, String url) {
        for (String profileStart : NGA_USER_PROFILE_START)
            if (url.startsWith(profileStart)) {
                String data = StringUtils.getStringBetween(url, 0,
                        profileStart, NGA_USER_PROFILE_END).result;
                try {
                    data = URLDecoder.decode(data, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (!StringUtils.isEmpty(data)) {
                    ARouter.getInstance()
                            .build(ARouterConstants.ACTIVITY_PROFILE)
                            .withString("mode", "username")
                            .withString("username", data)
                            .navigation(context);
                }
                return true;
            }
        return false;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Context context = view.getContext();
        PhoneConfiguration conf = PhoneConfiguration.getInstance();
        if (!url.startsWith("http") && !url.startsWith("market")) {
            Intent intent = new Intent();
            if (url.startsWith(NGA_CN_BOARD_PREFIX_NO_HTTP)
                    || url.startsWith(NGA_178_BOARD_PREFIX_NO_HTTP)) {
                intent.setData(Uri.parse("http://" + url));
                intent.setClass(context, conf.topicActivityClass);
                context.startActivity(intent);
                return true;
            } else if (url.startsWith(NGA_CN_THREAD_PREFIX_NO_HTTP)
                    || url.startsWith(NGA_178_THREAD_PREFIX_NO_HTTP)) {
                intent.setData(Uri.parse("http://" + url));
                intent.putExtra("fromreplyactivity", 1);
                intent.setClass(context, conf.articleActivityClass);
                context.startActivity(intent);
                return true;
            } else if (url.endsWith(".gif") || url.endsWith(".jpg")
                    || url.endsWith(".png") || url.endsWith(".jpeg")
                    || url.endsWith(".bmp")) {
                String imgUrl = "http://" + url;
                if (mImgUrlList == null) {
                    mImgUrlList = new ArrayList<>();
                    mImgUrlList.add(imgUrl);
                }
                String[] urls = new String[mImgUrlList.size()];
                mImgUrlList.toArray(urls);
                intent.putExtra(ImageZoomActivity.KEY_GALLERY_URLS, /*new String[]{imgUrl*/ mImgUrlList.toArray());
                intent.putExtra(ImageZoomActivity.KEY_GALLERY_CUR_URL, imgUrl);
                intent.setClass(context, ImageZoomActivity.class);
                context.startActivity(intent);
                return true;
            }
        }
        if (url.startsWith(NGA_CN_BOARD_PREFIX)
                || url.startsWith(NGA_178_BOARD_PREFIX)
                || url.startsWith("http://bbs.nga.cn/thread.php?")) {
            Intent intent = new Intent();
            intent.setData(Uri.parse(url));
            intent.setClass(context, conf.topicActivityClass);
            context.startActivity(intent);

        } else if (url.startsWith(NGA_CN_THREAD_PREFIX)
                || url.startsWith(NGA_178_THREAD_PREFIX)
                || url.startsWith("http://bbs.nga.cn/read.php?")) {
            Intent intent = new Intent();
            intent.setData(Uri.parse(url));
            intent.putExtra("fromreplyactivity", 1);
            intent.setClass(context, conf.articleActivityClass);
            context.startActivity(intent);
        } else if (url.endsWith(".gif") || url.endsWith(".jpg")
                || url.endsWith(".png") || url.endsWith(".jpeg")
                || url.endsWith(".bmp")) {
            Intent intent = new Intent();
            if (mImgUrlList == null) {
                mImgUrlList = new ArrayList<>();
                mImgUrlList.add(url);
            }
            String[] urls = new String[mImgUrlList.size()];
            mImgUrlList.toArray(urls);
            intent.putExtra(ImageZoomActivity.KEY_GALLERY_URLS, urls);
            intent.putExtra(ImageZoomActivity.KEY_GALLERY_CUR_URL, url);
            intent.setClass(context, ImageZoomActivity.class);
            context.startActivity(intent);
        } else if (overrideProfileUrlLoading(context, url)) {

        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            boolean isSafeIntent = context.getPackageManager().queryIntentActivities(intent, 0).size() > 0;
            if (isSafeIntent) {
                context.startActivity(intent);
            }
        }
        return true;
    }

}
