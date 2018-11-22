package sp.phone.mvp.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import sp.phone.forumoperation.TopicListParam;
import sp.phone.fragment.TopicSearchFragment;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.contract.TopicListContract;
import sp.phone.mvp.model.TopicListModel;
import sp.phone.mvp.model.entity.ThreadPageInfo;
import sp.phone.mvp.model.entity.TopicListInfo;
import sp.phone.util.NLog;

/**
 * Created by Justwen on 2017/6/3.
 */

public class TopicListPresenter extends BasePresenter<TopicSearchFragment, TopicListModel> implements TopicListContract.Presenter {

    protected TopicListInfo twentyFourList = new TopicListInfo();



    private OnHttpCallBack<TopicListInfo> mCallBack = new OnHttpCallBack<TopicListInfo>() {
        @Override
        public void onError(String text) {
            if (isAttached()) {
                mBaseView.setRefreshing(false);
                mBaseView.showToast(text);
                mBaseView.hideLoadingView();
            }
        }

        @Override
        public void onSuccess(TopicListInfo data) {
            if (!isAttached()) {
                return;
            }
            mBaseView.clearData();
            mBaseView.scrollTo(0);
            setData(data);
            mBaseView.hideLoadingView();
        }

    };

    private OnHttpCallBack<TopicListInfo> mNextPageCallBack = new OnHttpCallBack<TopicListInfo>() {
        @Override
        public void onError(String text) {
            if (isAttached()) {
                mBaseView.setRefreshing(false);
                mBaseView.setNextPageEnabled(false);
                mBaseView.showToast(text);
            }
        }

        @Override
        public void onSuccess(TopicListInfo data) {
            if (!isAttached()) {
                return;
            }
            setData(data);
        }
    };

    /* callback for the twenty four hour hot topic list */
    private OnHttpCallBack<TopicListInfo> mTwentyFourCallBack = new OnHttpCallBack<TopicListInfo>() {
        @Override
        public void onError(String text) {
            if (isAttached()) {
                mBaseView.setRefreshing(false);
                mBaseView.setNextPageEnabled(false);
                mBaseView.showToast(text);
            }
        }

        @Override
        public void onSuccess(TopicListInfo data) {
            if (!isAttached()) {
                return;
            }
            /* Concatenate the pages */
            twentyFourList.getThreadPageList().addAll(data.getThreadPageList());
            Collections.sort(twentyFourList.getThreadPageList(), new Comparator<ThreadPageInfo>() {
                public int compare(ThreadPageInfo o1, ThreadPageInfo o2) {
                    return o1.getReplies() < o2.getReplies() ? 1 : -1;
                }
            });
            setData(twentyFourList);
            mBaseView.hideLoadingView();
        }
    };

    private void setData(TopicListInfo result) {
        mBaseView.setRefreshing(false);
        mBaseView.setData(result);
    }

    @Override
    protected TopicListModel onCreateModel() {
        return new TopicListModel();
    }

    @Override
    public void removeTopic(ThreadPageInfo info, final int position) {
        mBaseModel.removeTopic(info, new OnHttpCallBack<String>() {
            @Override
            public void onError(String text) {
                if (isAttached()) {
                    mBaseView.showToast(text);
                }
            }

            @Override
            public void onSuccess(String data) {
                if (isAttached()) {
                    mBaseView.showToast(data);
                    mBaseView.removeTopic(info);
                }
            }
        });
    }

    @Override
    public void loadPage(int page, TopicListParam requestInfo) {
        mBaseView.setRefreshing(true);
        if (requestInfo.twentyfour == 1) {
            /* preload pages */
            twentyFourList.getThreadPageList().clear();
            mBaseView.clearData();
            mBaseView.scrollTo(0);
            mBaseModel.loadTwentyFourList(requestInfo, mTwentyFourCallBack);
        } else {
            mBaseModel.loadTopicList(page, requestInfo, mCallBack);
        }
    }

    @Override
    public void loadNextPage(int page, TopicListParam requestInfo) {
        mBaseView.setRefreshing(true);
        mBaseModel.loadTopicList(page, requestInfo, mNextPageCallBack);
    }

}
