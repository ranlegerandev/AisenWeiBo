package org.aisen.weibo.sina.ui.fragment.timeline;

import android.os.Bundle;
import android.text.TextUtils;

import com.m.network.http.Params;
import com.m.network.task.TaskException;
import com.m.ui.fragment.ABaseFragment;
import com.m.ui.fragment.ARefreshFragment;
import com.m.ui.fragment.AStripTabsFragment;

import org.aisen.weibo.sina.base.AppContext;
import org.aisen.weibo.sina.base.AppSettings;
import org.aisen.weibo.sina.support.bean.TimelineGroupBean;
import org.sina.android.SinaSDK;
import org.sina.android.bean.StatusContents;

/**
 * 分组的微博列表
 * 
 * @author wangdan
 *
 */
public class TimelineGroupsFragment extends ATimelineFragment implements AStripTabsFragment.IStripTabInitData {

    public static ABaseFragment newInstance(TimelineGroupBean groupBean) {
        return newInstance(groupBean, false);
    }

	public static ABaseFragment newInstance(TimelineGroupBean groupBean, boolean offline) {
		TimelineGroupsFragment fragment = new TimelineGroupsFragment();
		
		Bundle args = new Bundle();
		args.putSerializable("bean", groupBean);
        args.putBoolean("offline", offline);
		fragment.setArguments(args);
		
		return fragment;
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStripTabRequestData() {
        // 如果还没有加载过数据，就开始加载
        if (getTaskCount("TimelineTask") == 0) {
            requestData(ARefreshFragment.RefreshMode.reset);
        }
    }

    @Override
    protected void requestData(ARefreshFragment.RefreshMode mode) {
        // 如果还没有加载过数据，切且显示的是当前的页面
        if (getTaskCount("TimelineTask") == 0) {
            if (getPagerCurrentFragment() == this)
                new FriendsGroupTimelineTask(mode).execute();
        }
        else {
            new FriendsGroupTimelineTask(mode).execute();
        }
    }

	// 加载分组好友的task
	class FriendsGroupTimelineTask extends TimelineTask {

		public FriendsGroupTimelineTask(ARefreshFragment.RefreshMode mode) {
			super(mode);
		}

		@Override
		protected StatusContents workInBackground(ARefreshFragment.RefreshMode mode, String previousPage, String nextPage, Void... p) throws TaskException {
			Params params = new Params();

			if (mode == ARefreshFragment.RefreshMode.refresh && !TextUtils.isEmpty(previousPage))
				params.addParameter("since_id", previousPage);

			if (mode == ARefreshFragment.RefreshMode.update && !TextUtils.isEmpty(nextPage))
				params.addParameter("max_id", nextPage);

			params.addParameter("list_id", getGroup().getType());
			
			params.addParameter("count", String.valueOf(AppSettings.getTimelineCount()));

            boolean offline = getArguments() != null ? getArguments().getBoolean("offline") : false;

			StatusContents beans = offline ? SinaSDK.getInstance(AppContext.getToken(), getTaskCacheMode(this)).offlineFriendshipGroupsTimeline(params)
                                           : SinaSDK.getInstance(AppContext.getToken(), getTaskCacheMode(this)).friendshipGroupsTimeline(params);

			return beans;
		}

	}
	
}