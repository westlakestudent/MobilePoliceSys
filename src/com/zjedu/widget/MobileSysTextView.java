package com.zjedu.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 
 * @author westlakeboy
 *
 */
public class MobileSysTextView extends TextView {


    public MobileSysTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

	@Override
	public boolean isFocused() {
		return true;
	}

    
}