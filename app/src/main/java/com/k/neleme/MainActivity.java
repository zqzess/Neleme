package com.k.neleme;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.k.neleme.Views.AddWidget;
import com.k.neleme.adapters.CarAdapter;
import com.k.neleme.bean.FoodBean;
import com.k.neleme.behaviors.AppBarBehavior;
import com.k.neleme.fragments.FirstFragment;
import com.k.neleme.fragments.SecondFragment;
import com.k.neleme.utils.ViewUtils;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.slidebar.ScrollBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AddWidget.OnAddClick {

	private Context mContext = this;
	private ImageView iv_shop_car;
	private int shopWidth;
	private int[] carLoc;
	private CoordinatorLayout rootview;
	public BottomSheetBehavior behavior;
	public View blackView, scroll_container;
	private FirstFragment firstFragment;
	private CarAdapter carAdapter;
	private TextView car_limit, tv_amount;
	private boolean sheetScrolling;
	private TextView car_badge;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
	}

	private void initViews() {
		rootview = (CoordinatorLayout) findViewById(R.id.rootview);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		initViewpager();
		initRecyclerView();
		initShopCar();
	}

	private void initShopCar() {
		iv_shop_car = (ImageView) findViewById(R.id.iv_shop_car);
		car_badge = (TextView) findViewById(R.id.car_badge);
		car_limit = (TextView) findViewById(R.id.car_limit);
		tv_amount = (TextView) findViewById(R.id.tv_amount);
		RecyclerView carRecView = (RecyclerView) findViewById(R.id.car_recyclerview);
		carRecView.setLayoutManager(new LinearLayoutManager(mContext));
		((DefaultItemAnimator) carRecView.getItemAnimator()).setSupportsChangeAnimations(false);
		carAdapter = new CarAdapter(new ArrayList<FoodBean>(), this);
		carAdapter.bindToRecyclerView(carRecView);
		blackView = findViewById(R.id.blackview);
		behavior = BottomSheetBehavior.from(findViewById(R.id.car_container));
		behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {
				sheetScrolling = false;
				if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
					blackView.setVisibility(View.GONE);
				}
			}

			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {
				sheetScrolling = true;
				blackView.setVisibility(View.VISIBLE);
				ViewCompat.setAlpha(blackView, slideOffset);
			}
		});
	}

	private void initViewpager() {
		scroll_container = findViewById(R.id.scroll_container);
		ScrollIndicatorView mSv = (ScrollIndicatorView) findViewById(R.id.indicator);
		ColorBar colorBar = new ColorBar(mContext, ContextCompat.getColor(mContext, R.color.dicator_line_blue), 6,
				ScrollBar.Gravity.BOTTOM);
		colorBar.setWidth(ViewUtils.dip2px(mContext, 30));
		mSv.setScrollBar(colorBar);
		mSv.setSplitAuto(false);
		mSv.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(mContext, R.color.dicator_line_blue),
				ContextCompat.getColor(mContext, R.color.black)));
		ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
		IndicatorViewPager indicatorViewPager = new IndicatorViewPager(mSv, mViewPager);
		firstFragment = new FirstFragment();
		ViewpagerAdapter myAdapter = new ViewpagerAdapter(getSupportFragmentManager());
		indicatorViewPager.setAdapter(myAdapter);
	}

	private class ViewpagerAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
		private LayoutInflater inflater;
		private int padding;
		private String[] tabs = new String[]{"商品", "评价"};

		ViewpagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
			inflater = LayoutInflater.from(mContext);
			padding = ViewUtils.dip2px(mContext, 20);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public View getViewForTab(int position, View convertView, ViewGroup container) {
			convertView = inflater.inflate(R.layout.item_textview, container, false);
			TextView textView = (TextView) convertView;
			textView.setText(tabs[position]); //名称
			textView.setPadding(padding, 0, padding, 0);
			return convertView;
		}

		@Override
		public Fragment getFragmentForPage(int position) {
			switch (position) {
				case 0:
					return firstFragment;
			}
			return new SecondFragment();
		}
	}

	private void initRecyclerView() {
	}


	@Override
	public void onAddClick(View view, FoodBean fb) {
		dealCar(fb);
		int[] addLoc = new int[2];
		view.getLocationInWindow(addLoc);
		if (shopWidth == 0) {
			carLoc = new int[2];
			shopWidth = iv_shop_car.getWidth() / 2;
			iv_shop_car.getLocationInWindow(carLoc);
			carLoc[0] = carLoc[0] + shopWidth - view.getWidth() / 2;
		}

		Path path = new Path();
		path.moveTo(addLoc[0], addLoc[1]);
		path.quadTo(addLoc[0] - 500, addLoc[1] - 200, carLoc[0], carLoc[1]);

		final TextView textView = new TextView(mContext);
		textView.setBackgroundResource(R.drawable.circle_blue);
		textView.setText("1");
		textView.setTextColor(Color.WHITE);
		textView.setGravity(Gravity.CENTER);
		CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(view.getWidth(), view.getHeight());
		rootview.addView(textView, lp);
		ViewAnimator.animate(textView).path(path).accelerate().duration(400).onStop(new AnimationListener.Stop() {
			@Override
			public void onStop() {
				rootview.removeView(textView);
			}
		}).start();
	}

	@Override
	public void onSubClick(FoodBean fb) {
		dealCar(fb);
	}

	private void dealCar(FoodBean foodBean) {
		HashMap<String, Long> typeSelect = new HashMap<>();
		BigDecimal amount = new BigDecimal(0.0);
		int total = 0;
		boolean hasFood = false;
		if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
			firstFragment.getFoodAdapter().notifyDataSetChanged();
		}
		List<FoodBean> flist = carAdapter.getData();
		int p = -1;
		for (int i = 0; i < flist.size(); i++) {
			FoodBean fb = flist.get(i);
			total += fb.getSelectCount();
			if (typeSelect.containsKey(fb.getType())) {
				typeSelect.put(fb.getType(), typeSelect.get(fb.getType()) + fb.getSelectCount());
			} else {
				typeSelect.put(fb.getType(), fb.getSelectCount());
			}
			amount = amount.add(fb.getPrice().multiply(BigDecimal.valueOf(fb.getSelectCount())));
			if (fb.getId() == foodBean.getId()) {
				hasFood = true;
				if (foodBean.getSelectCount() == 0) {
					p = i;
				} else {
					carAdapter.setData(i, foodBean);
				}
			}
		}
		if (p >= 0) {
			carAdapter.remove(p);
		} else if (!hasFood) {
			carAdapter.addData(foodBean);
			if (typeSelect.containsKey(foodBean.getType())) {
				typeSelect.put(foodBean.getType(), typeSelect.get(foodBean.getType()) + foodBean.getSelectCount());
			} else {
				typeSelect.put(foodBean.getType(), foodBean.getSelectCount());
			}
			amount = amount.add(foodBean.getPrice().multiply(BigDecimal.valueOf(foodBean.getSelectCount())));
			total += foodBean.getSelectCount();
		}
		if (total > 0) {
			car_badge.setVisibility(View.VISIBLE);
			car_badge.setText(total + "");
		} else {
			car_badge.setVisibility(View.INVISIBLE);
		}
		firstFragment.getTypeAdapter().updateBadge(typeSelect);
		updateAmount(amount);
	}

	private void updateAmount(BigDecimal amount) {
		if (amount.compareTo(new BigDecimal(0.0)) == 0) {
			car_limit.setText("¥20 起送");
			car_limit.setTextColor(Color.parseColor("#a8a8a8"));
			car_limit.setBackgroundColor(Color.parseColor("#535353"));
			findViewById(R.id.car_nonselect).setVisibility(View.VISIBLE);
			findViewById(R.id.amount_container).setVisibility(View.GONE);
			iv_shop_car.setImageResource(R.drawable.shop_car_empty);
			behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		} else if (amount.compareTo(new BigDecimal(20.0)) < 0) {
			car_limit.setText("还差 ¥" + amount + " 起送");
			car_limit.setTextColor(Color.parseColor("#a8a8a8"));
			car_limit.setBackgroundColor(Color.parseColor("#535353"));
			findViewById(R.id.car_nonselect).setVisibility(View.GONE);
			findViewById(R.id.amount_container).setVisibility(View.VISIBLE);
			iv_shop_car.setImageResource(R.drawable.shop_car);
		} else {
			car_limit.setText("     去结算     ");
			car_limit.setTextColor(Color.WHITE);
			car_limit.setBackgroundColor(Color.parseColor("#59d178"));
			findViewById(R.id.car_nonselect).setVisibility(View.GONE);
			findViewById(R.id.amount_container).setVisibility(View.VISIBLE);
			iv_shop_car.setImageResource(R.drawable.shop_car);
		}
		tv_amount.setText("¥" + amount);
	}

	public void expendCut(View view) {
		float cty = scroll_container.getTranslationY();
		if (!ViewUtils.isFastClick()) {
			ViewAnimator.animate(scroll_container)
					.translationY(cty, cty == 0 ? AppBarBehavior.cutExpHeight : 0)
					.decelerate()
					.duration(100)
					.start();
		}
	}

	public void toggleCar(View view) {
		if (sheetScrolling) {
			return;
		}
		if (carAdapter.getItemCount() == 0) {
			return;
		}
		if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
			behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		} else {
			behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
		}
	}

	public void clearCar(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		TextView tv = new TextView(mContext);
		tv.setText("清空购物车?");
		tv.setTextSize(14);
		tv.setPadding(ViewUtils.dip2px(mContext, 16), ViewUtils.dip2px(mContext, 16), 0, 0);
		tv.setTextColor(Color.parseColor("#757575"));
		AlertDialog alertDialog = builder
				.setNegativeButton("取消", null)
				.setCustomTitle(tv)
				.setPositiveButton("清空", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						List<FoodBean> flist = carAdapter.getData();
						for (int i = 0; i < flist.size(); i++) {
							FoodBean fb = flist.get(i);
							fb.setSelectCount(0);
						}
						carAdapter.setNewData(new ArrayList<FoodBean>());
						firstFragment.getFoodAdapter().notifyDataSetChanged();
						car_badge.setVisibility(View.INVISIBLE);
						firstFragment.getTypeAdapter().updateBadge(new HashMap<String, Long>());
						updateAmount(new BigDecimal(0.0));
					}
				})
				.show();
		Button nButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		nButton.setTextColor(ContextCompat.getColor(mContext, R.color.dodgerblue));
		nButton.setTypeface(Typeface.DEFAULT_BOLD);
		Button pButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		pButton.setTextColor(ContextCompat.getColor(mContext, R.color.dodgerblue));
		pButton.setTypeface(Typeface.DEFAULT_BOLD);
	}

	public void goAccount(View view) {
	}
}
