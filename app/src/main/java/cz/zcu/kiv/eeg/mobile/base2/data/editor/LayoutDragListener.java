package cz.zcu.kiv.eeg.mobile.base2.data.editor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.model.ViewNode;

public class LayoutDragListener implements OnDragListener {
	static int count = 0; // TODO
	Drawable enterShape;
	Drawable normalShape;
	Context ctx; //TODO zatim zbytecne
	private SparseArray<ViewNode> nodes;//TODO zatim zbytecne

	public LayoutDragListener(Context ctx, SparseArray<ViewNode> nodes) {
		this.enterShape = ctx.getResources().getDrawable(R.drawable.shape_droptarget); // červený rámeček
		this.normalShape = ctx.getResources().getDrawable(R.drawable.shape);
		this.nodes = nodes;
		this.ctx = ctx;
		count++;
	}

	// TODO viewB = target
	@Override
	public boolean onDrag(View viewB, DragEvent event) {
		int action = event.getAction();
//		System.out.println(viewB.getTag(Values.NODE_ID) + "_all " + count);
		ViewGroup wrapLayoutB = (ViewGroup) viewB.getParent();

		switch (event.getAction()) {
		case DragEvent.ACTION_DRAG_STARTED: // Do nothing
			wrapLayoutB.setBackgroundDrawable(normalShape);
			break;
		case DragEvent.ACTION_DRAG_ENTERED:
			wrapLayoutB = (ViewGroup) viewB.getParent();
			wrapLayoutB.setBackgroundDrawable(enterShape);
			break;
		case DragEvent.ACTION_DRAG_EXITED:
			wrapLayoutB = (ViewGroup) viewB.getParent();
			wrapLayoutB.setBackgroundDrawable(normalShape);
			break;
		case DragEvent.ACTION_DROP:
			ViewGroup wrapLayoutA = (ViewGroup) event.getLocalState(); // wrapLayoutA nastaven v LayoutTouchListener

			ViewGroup rowLayoutA = (ViewGroup) wrapLayoutA.getParent();
			ViewGroup rowLayoutB = (ViewGroup) wrapLayoutB.getParent();

			int wrapLayoutAIndex = rowLayoutA.indexOfChild(wrapLayoutA);
			int wrapLayoutBIndex = rowLayoutB.indexOfChild(wrapLayoutB);

			ViewGroup formLayout = (ViewGroup) rowLayoutB.getParent();
			int rowLayoutAIndex = formLayout.indexOfChild(rowLayoutA);
			int rowLayoutBIndex = formLayout.indexOfChild(rowLayoutB);

			// vytvoření nového sloupce
			if (((Integer) viewB.getTag(R.id.NODE_ID)) == -1) {
				// přidání pole na řádku
				if (rowLayoutAIndex != rowLayoutBIndex) {
					// začátek řádky
					if (wrapLayoutBIndex == 0) {
						rowLayoutA.removeView(wrapLayoutA);
						rowLayoutB.addView(wrapLayoutA, wrapLayoutBIndex + 1);
					}
					// konec řádky
					else {
						rowLayoutA.removeView(wrapLayoutA);
						rowLayoutB.addView(wrapLayoutA, wrapLayoutBIndex);
					}
					// přepočet váhy pro cílový řádek
					recalculateWeight(rowLayoutB);
					// přepočet váhy pro zdrojový řádek
					if (rowLayoutA.getChildCount() == 2) {
						formLayout.removeView(rowLayoutA); // TODO sem přidat prostor pro přesun
					} else {
						recalculateWeight(rowLayoutA);
					}
				}
			}
			// prohození dvou polí
			else {
				if (wrapLayoutA != wrapLayoutB) {
					rowLayoutA.removeView(wrapLayoutA);
					rowLayoutB.removeView(wrapLayoutB);

					rowLayoutA.addView(wrapLayoutB, wrapLayoutAIndex);
					rowLayoutB.addView(wrapLayoutA, wrapLayoutBIndex);

					recalculateWeight(rowLayoutA);
					recalculateWeight(rowLayoutB);
				}
				wrapLayoutB.setBackgroundDrawable(normalShape);
			}
			break;
		case DragEvent.ACTION_DRAG_ENDED:
			wrapLayoutB = (ViewGroup) viewB.getParent();
			wrapLayoutB.setBackgroundDrawable(normalShape);
		default:
			break;
		}
		return true;
	}

	private void recalculateWeight(ViewGroup rowLayout) {
		int count = rowLayout.getChildCount() - 2;
		float weight = 100 / count;

		for (int i = 1; i <= count; i++) {
			View item = rowLayout.getChildAt(i);
			if (i != 1 && i != count) {
				((LinearLayout.LayoutParams) item.getLayoutParams()).weight = weight;
				nodes.get((Integer)item.getTag(R.id.NODE_ID)).setWeight((int)weight);
			} else {
				((LinearLayout.LayoutParams) item.getLayoutParams()).weight = weight - 10;
				nodes.get((Integer)item.getTag(R.id.NODE_ID)).setWeight((int)weight);
			}
		}
	}
}