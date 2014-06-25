package cz.zcu.kiv.eeg.mobile.base2.data.editor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnDragListener;
import android.widget.Button;
import android.widget.LinearLayout;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.model.ViewNode;

public class LayoutDragListener implements OnDragListener {
	static int count = 0; // TODO
	Drawable enterShape;
	Drawable normalShape;
	Context ctx; // TODO zatim zbytecne
	private SparseArray<ViewNode> nodes;// TODO zatim zbytecne

	public LayoutDragListener(Context ctx, SparseArray<ViewNode> nodes) {
		this.enterShape = ctx.getResources().getDrawable(R.drawable.shape_droptarget); // červený rámeček
		this.normalShape = ctx.getResources().getDrawable(R.drawable.shape);
		this.nodes = nodes;
		this.ctx = ctx;
		count++;
	}

	// TODO viewB, wrapLayoutB = target
	@Override
	public boolean onDrag(View viewB, DragEvent event) {
		int action = event.getAction();
		// System.out.println(viewB.getTag(Values.NODE_ID) + "_all " + count);
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
						rowLayoutB.addView(wrapLayoutA, wrapLayoutBIndex - 1);
					}
					// přepočet váhy pro cílový řádek
					recalculateWeight(rowLayoutB);

					 //poslední řádek (1 přidávací sloupec)
					if(rowLayoutBIndex == formLayout.getChildCount() -1){
						rowLayoutB.getChildAt(2).setVisibility(View.VISIBLE);
						formLayout.addView(addLastRow());
					}
					
					if (rowLayoutA.getChildCount() == 2) {
						formLayout.removeView(rowLayoutA);
					}
					// přepočet váhy pro zdrojový řádek
					else {
						recalculateWeight(rowLayoutA);
					}					
				}
			}
			// prohození dvou polí
			else {
				if (wrapLayoutA != wrapLayoutB) {
					rowLayoutA.removeView(wrapLayoutA);
					rowLayoutB.removeView(wrapLayoutB);

					if (wrapLayoutAIndex < wrapLayoutBIndex) {
						rowLayoutA.addView(wrapLayoutB, wrapLayoutAIndex);
						rowLayoutB.addView(wrapLayoutA, wrapLayoutBIndex);
					} else {
						rowLayoutB.addView(wrapLayoutA, wrapLayoutBIndex);
						rowLayoutA.addView(wrapLayoutB, wrapLayoutAIndex);
					}

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
				nodes.get((Integer) item.getTag(R.id.NODE_ID)).setWeight((int) weight);
			} else {
				((LinearLayout.LayoutParams) item.getLayoutParams()).weight = weight - 10;
				nodes.get((Integer) item.getTag(R.id.NODE_ID)).setWeight((int) weight);
			}
		}
	}

	private LinearLayout addLastRow() {
		LinearLayout rowLayout = new LinearLayout(ctx);
		rowLayout.setOrientation(LinearLayout.HORIZONTAL);
		rowLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		rowLayout.addView(getNewColumnButton(true));
		rowLayout.addView(getNewColumnButton(false));
		return rowLayout;
	}

	private LinearLayout getNewColumnButton(boolean isVisible) {
		Drawable editShape = ctx.getResources().getDrawable(R.drawable.shape);
		LinearLayout layout = new LinearLayout(ctx);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 10));
		layout.setBackgroundDrawable(editShape);
		layout.setTag(R.id.NODE_ID, -1);
		Button button = new Button(ctx);
		button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		button.setOnDragListener(new LayoutDragListener(ctx, nodes));
		button.setText(" ");
		button.setTag(R.id.NODE_ID, -1);
		layout.addView(button);
		if (isVisible) {
			layout.setVisibility(View.VISIBLE);
		} else {
			layout.setVisibility(View.GONE);
		}
		return layout;
	}

}