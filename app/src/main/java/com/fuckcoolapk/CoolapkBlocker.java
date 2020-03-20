package com.fuckcoolapk;

import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by w568w on 18-5-6.
 *
 * @author w568w
 */

public class CoolapkBlocker extends AbstractViewBlocker {
    private static CoolapkBlocker instance;
    private static int sFromWhereViewId = -1;
    private static int sTextViewId = -1;
    private static ArrayList<BlockModel> mData;

    public static CoolapkBlocker getInstance() {
        if (instance == null) {

            instance = new CoolapkBlocker();
        }
        return instance;
    }

    public void setList(ArrayList<BlockModel> data) {
        mData = data;
    }

    @Override
    public boolean shouldBlock(View view, String id, String className) {
        if (sFromWhereViewId == -1) {
            sFromWhereViewId = ViewUtils.getId("from_where_view", view.getContext());

        }
        if (sTextViewId == -1) {
            sTextViewId = ViewUtils.getId("text_view", view.getContext());

        }
        if (!"com.coolapk.market".equals(view.getContext().getPackageName())) {
            return false;
        }
        if ("Banner".equals(className) &&
                id.contains("banner_view")) {
            return true;
        }
        if ("LinearLayout".equals(className) && id.contains("card_view")) {
            try {
                //获取动态来源
                TextView from = (TextView) view.findViewById(sFromWhereViewId);
                TextView content = (TextView) view.findViewById(sTextViewId);
                String froms = null;
                String contents = null;
                if (from != null) {
                    froms = from.getText().toString();
                }
                if (content != null) {
                    contents = content.getText().toString();
                }
                final int len = mData.size();
                for (int i = 0; i < len; i++) {
                    BlockModel model = mData.get(i);
                    if (model instanceof CoolApkHeadlineModel) {
                        switch (((CoolApkHeadlineModel) model).getType()) {
                            case FROM:
                                if (froms != null && froms.contains(model.text)) {
                                    return true;
                                }
                                break;
                            case CONTENT:
                                if (contents != null && contents.contains(model.text)) {
                                    return true;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }

        }
        //todo more rules
        return false;
    }
}