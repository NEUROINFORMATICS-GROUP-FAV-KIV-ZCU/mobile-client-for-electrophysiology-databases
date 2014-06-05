package cz.zcu.kiv.eeg.mobile.base2.data.editor;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

public class LayoutTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				ClipData data = ClipData.newPlainText("", "");
				
				ViewGroup wrapLayout = (ViewGroup) view.getParent();
				
				DragShadowBuilder shadowBuilder = new DragShadowBuilder(wrapLayout);
				view.startDrag(data, shadowBuilder, wrapLayout, 0);
				// view.setVisibility(View.INVISIBLE);
				return true;
			} else {
				return false;
			}
		}
	}