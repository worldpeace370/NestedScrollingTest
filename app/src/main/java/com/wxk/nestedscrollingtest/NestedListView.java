package com.wxk.nestedscrollingtest;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by bjwuxiangkun on 2017/1/11.
 * Contact by bjwuxiangkun@corp.netease.com
 */

public class NestedListView extends ListView implements NestedScrollingChild {

  private static final String TAG = "NestedListView";
  private NestedScrollingChildHelper mChildHelper;
  private int mLastY;
  private final int[] mScrollOffset = new int[2];
  private final int[] mScrollConsumed = new int[2];
  private int mNestedOffsetY;

  public NestedListView(Context context) {
    super(context);
    init();
  }

  public NestedListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public NestedListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    mChildHelper = new NestedScrollingChildHelper(this);
    setNestedScrollingEnabled(true); // 设置嵌套滑动是否能用
  }

  /**
   * 设置嵌套滑动是否能用
   *
   * @param enabled true to enable nested scrolling, false to disable
   */
  @Override
  public void setNestedScrollingEnabled(boolean enabled) {
    mChildHelper.setNestedScrollingEnabled(enabled);
  }

  /**
   * 判断嵌套滑动是否可用
   *
   * @return true if nested scrolling is enabled
   */
  @Override
  public boolean isNestedScrollingEnabled() {
    return mChildHelper.isNestedScrollingEnabled();
  }

  /**
   * 开始嵌套滑动
   *
   * @param axes 表示方向轴，有横向和竖向
   */
  @Override
  public boolean startNestedScroll(int axes) {
    return mChildHelper.startNestedScroll(axes);
  }

  /**
   * 停止嵌套滑动
   */
  @Override
  public void stopNestedScroll() {
    mChildHelper.stopNestedScroll();
  }

  /**
   * 判断是否有父View 支持嵌套滑动
   *
   * @return whether this view has a nested scrolling parent
   */
  @Override
  public boolean hasNestedScrollingParent() {
    return mChildHelper.hasNestedScrollingParent();
  }

  /**
   * 子view处理scroll后调用
   *
   * @param dxConsumed     x轴上被消费的距离（横向）
   * @param dyConsumed     y轴上被消费的距离（竖向）
   * @param dxUnconsumed   x轴上未被消费的距离
   * @param dyUnconsumed   y轴上未被消费的距离
   * @param offsetInWindow 子View的窗体偏移量
   * @return true if the event was dispatched, false if it could not be dispatched.
   */
  @Override
  public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
    return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
  }

  /**
   * 在子View的onTouchEvent或者onTouch中，调用该方法通知父View滑动的距离
   *
   * @param dx             x轴上滑动的距离
   * @param dy             y轴上滑动的距离
   * @param consumed       父view消费掉的scroll长度
   * @param offsetInWindow 子View的窗体偏移量
   * @return 支持的嵌套的父View 是否处理了 滑动事件
   */
  @Override
  public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
    return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
  }

  /**
   * 滑行时调用
   *
   * @param velocityX x 轴上的滑动速率
   * @param velocityY y 轴上的滑动速率
   * @param consumed  是否被消费
   * @return true if the nested scrolling parent consumed or otherwise reacted to the fling
   */
  @Override
  public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
    return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
  }

  /**
   * 进行滑行前调用
   *
   * @param velocityX x 轴上的滑动速率
   * @param velocityY y 轴上的滑动速率
   * @return true if a nested scrolling parent consumed the fling
   */
  @Override
  public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
    return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
  }


  @Override
  public boolean onTouchEvent(MotionEvent event) {
    final int action = MotionEventCompat.getActionMasked(event); // 推荐使用,兼容性更好

    int y = (int) event.getY(); //相对于当前View左上角的y坐标
    event.offsetLocation(0, mNestedOffsetY);
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        AppLog.i(TAG, "ACTION_DOWN : " + "mLastY = " + y + ", mNestedOffsetY = " + mNestedOffsetY);
        mLastY = y;             //刚按下时记录y的坐标
        mNestedOffsetY = 0;
        break;
      case MotionEvent.ACTION_MOVE:

        int dy = mLastY - y;  // 下滑为-, 上滑为+
        int oldY = getScrollY();
        AppLog.i(TAG, "ACTION_MOVE : " + "dy = " + dy + ", oldY = " + oldY);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
        if (dispatchNestedPreScroll(0, dy, mScrollConsumed, mScrollOffset)) {
          dy -= mScrollConsumed[1];
          event.offsetLocation(0, -mScrollOffset[1]);
          mNestedOffsetY += mScrollOffset[1];
          AppLog.i(TAG, "ACTION_MOVE : (first if )" + "dy = " + dy + ", mNestedOffsetY = " + mNestedOffsetY);
        }
//        下面这段代码(到第一次出现stopNestedScroll截止)没有影响(保留不保留都行)
        mLastY = y - mScrollOffset[1];
        if (dy < 0) {
          int newScrollY = Math.max(0, oldY + dy);
          dy -= newScrollY - oldY;
          if (dispatchNestedScroll(0, newScrollY - dy, 0, dy, mScrollOffset)) {
            event.offsetLocation(0, mScrollOffset[1]);
            mNestedOffsetY += mScrollOffset[1];
            mLastY -= mScrollOffset[1];
          }
        }
        stopNestedScroll();
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        stopNestedScroll();
        break;
    }
    return super.onTouchEvent(event);
  }
}
