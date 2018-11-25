package skku.alticastvux.util;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.util.List;

import skku.alticastvux.baedalfragment.BaseBaedalFragment;

public class FragmentStackV4 {

    private static final String TAG = FragmentStackV4.class.getSimpleName();
    public static int count = 0;
    /**
     * add fragment to containerViewId and add to stack. commit. for pressing
     * back key
     *
     * be careful using "add" because onTouch() goes to parent fragment
     *
     * @param fragmentManager
     * @param containerViewId
     * @param fragment
     */
    // public static void addStackAndCommit(FragmentManager fragmentManager, int
    // containerViewId, Fragment fragment) {
    // fragmentManager.beginTransaction().add(containerViewId,
    // fragment).addToBackStack(null).commit();
    // }

    /**
     * clear all fragments from stack
     */
    public static void clear(FragmentManager fragmentManager) {
        count = 0;
        if (fragmentManager == null) {
            Log.w(TAG, "clear : fragment manager null");
            return;
        }
        // while (fragmentManager.popBackStackImmediate());
        try {
            fragmentManager.popBackStack(null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (IllegalStateException e) {
            Log.w(TAG, "illegar state. maybe activity already saved");
        }
    }

    /**
     * init fragment of containerViewId for the first time. commit argument
     * fragment will not be added to backstack.
     *
     * @param fragmentManager
     * @param containerViewId
     * @param fragment
     */
    public static void init(FragmentManager fragmentManager,
                            int containerViewId, Fragment fragment) {
        if (fragmentManager == null) {
            Log.w(TAG, "init : fragment manager null");
            return;
        }

        Log.d(TAG, "init : " + fragment.getClass().getSimpleName());
        // fragmentManager.beginTransaction().replace(containerViewId,
        // fragment).commit();
        fragmentManager.beginTransaction().replace(containerViewId, fragment)
                .commitAllowingStateLoss();
    }

    /**
     * replace fragment of containerViewId and add to stack. commit. for
     * pressing back key
     *
     * @param fragmentManager
     * @param containerViewId
     * @param fragment
     */
    public static void add(FragmentManager fragmentManager,
                           int containerViewId, Fragment fragment) {
        count++;
        Log.d(TAG, "add : " + fragment.getClass().getSimpleName());
        fragment.setRetainInstance(false);
        fragmentManager.beginTransaction().replace(containerViewId, fragment)
                .addToBackStack(fragment.toString()).commitAllowingStateLoss();
    }

    public static void back(FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            count--;
            fragmentManager.popBackStack();
            Fragment topFragment = getTopFragment(fragmentManager);
            if(topFragment instanceof BaseBaedalFragment) {
                ((BaseBaedalFragment) topFragment).popBacked();
            }
            topFragment.getView().setFocusable(true);
            topFragment.getView().setFocusableInTouchMode(true);
            topFragment.getView().requestFocus();
        }
    }

    public static int getCount(FragmentManager fragmentManager) {
        return fragmentManager.getFragments().size();
    }

    public static Fragment getTopFragment(FragmentManager fragmentManager) {
        List<Fragment> fragentList = fragmentManager.getFragments();
        Fragment top = null;
        for (int i = fragentList.size() -1; i>=0 ; i--) {
            top = (Fragment) fragentList.get(i);
            if (top != null) {
                return top;
            }
        }
        return top;
    }

}
