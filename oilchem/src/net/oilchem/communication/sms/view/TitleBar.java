package net.oilchem.communication.sms.view;

import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.util.UIUtil;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleBar extends FrameLayout{
	
	/** 返回*/
	public final static int ACTION_BACK = 1;
	/** 白色 返回*/
	public final static int ACTION_WHITE_BACK = 3;
	
	/** 显示LOGO*/
	public final static int IMAGE_LOGO = 1;
	/** 消息列表  带table的title*/
	public final static int IMAGE_TABLEVIEW_MESSAGE = 4;
	/** 专题分类*/
	public final static int IMAGE_TOPIC_CLASSIFY = 5;
	/** 评论页面*/
	public final static int IMAGE_COMMENT_CLASSIFY = 6;
	
	private Button left_btn;
	private Button right_btn;
	private TextView navtop_right_tv;
	private TextView title_tv_left;
	private TextView title_tv_right;
	private ImageView title_tv_image,title_bg_iv;
	private LinearLayout middle_ll;
	private RelativeLayout parent_rl;
	 
	
	public TitleBar(final Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.title_bar,TitleBar.this); 
		parent_rl=(RelativeLayout)this.findViewById(R.id.navtop_parent_rl);
		title_bg_iv=(ImageView)this.findViewById(R.id.navtop_background_iv);
		left_btn = (Button)this.findViewById(R.id.navtop_left_btn);
		right_btn = (Button)this.findViewById(R.id.navtop_right_btn);
		middle_ll = (LinearLayout)this.findViewById(R.id.navtop_title_middle_ll);
		navtop_right_tv = (TextView)this.findViewById(R.id.navtop_right_tv);
		title_tv_left = (TextView)this.findViewById(R.id.navtop_title_left);
		title_tv_right = (TextView)this.findViewById(R.id.navtop_title_right);
		title_tv_image = (ImageView)this.findViewById(R.id.navtop_title_image);
	}
	
	 public void setTitleBarTitleColor(int color){
		if (color == 0) {
			return;
		}
		title_tv_left.setTextColor(color);
	 }
	
	/**
	 * 设置左侧按钮功能（自定义）
	 * 
	 * @param context 当前上下文
	 * @param isVisibility 是否显示
	 * @param drawble 背景图片
	 * @param listener 单击事件
	 */
	public void setLeftListener(Context context,int drawble,OnClickListener listener) {
		if(this.left_btn == null){
			return;
		}
		this.left_btn.setVisibility(View.VISIBLE);
		this.left_btn.setOnClickListener(listener);
		this.left_btn.setBackgroundResource(drawble);
	}
	
	/**
	 * 设置左侧按钮是否显示
	 * 
	 * @param context 当前上下文
	 * @param isVisibility 是否显示
	 */
	public void setLeftVisibility(boolean isVisibility) {
		if(isVisibility){
			this.left_btn.setVisibility(View.VISIBLE);
		}else{
			this.left_btn.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * 设置左侧按钮功能
	 * 
	 * @param context 当前上下文
	 * @param action 功能Code
	 * @param 要传的参数
	 */
	public void setLeft(Context context, int action,String[] params) {
		this.setAction(context, this.left_btn, action,params);
	}
	
	/**
	 * 设置左侧按钮功能
	 * 
	 * @param context 当前上下文
	 * @param action 功能Code
	 */
	public void setLeft(Context context, int action) {
		this.setAction(context, this.left_btn, action,null);
	}
	
	/**
	 * 设置右侧按钮功能（自定义）
	 * 
	 * @param context 当前上下文
	 * @param isVisibility 是否显示
	 * @param drawble 背景图片
	 * @param listener 单击事件
	 */
	public void setRightListener(Context context,int drawble,OnClickListener listener) {
		if(this.right_btn == null){
			return;
		}
		this.right_btn.setVisibility(View.VISIBLE);
		this.navtop_right_tv.setVisibility(View.GONE);
		this.right_btn.setOnClickListener(listener);
		this.right_btn.setBackgroundResource(drawble);
	}
	
	/**
	 * 设置右侧按钮是否显示
	 * 
	 * @param context 当前上下文
	 * @param isVisibility 是否显示
	 */
	public void setRightVisibility(Context context,boolean isVisibility) {
		if(isVisibility){
			this.right_btn.setVisibility(View.VISIBLE);
		}else{
			this.right_btn.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * 设置右侧按钮功能
	 * @param context 当前上下文
	 * @param action 功能Code
	 */
	public void setRight(Context context, int action) {
		this.right_btn.setVisibility(View.VISIBLE);
		this.navtop_right_tv.setVisibility(View.GONE);
		this.setAction(context, right_btn, action,null);
	}
	
	/**
	 * 设置右侧按钮功能
	 * @param context 当前上下文
	 * @param action 功能Code
	 */
	public void setRight(Context context, int action,String[] params) {
		this.right_btn.setVisibility(View.VISIBLE);
		this.navtop_right_tv.setVisibility(View.GONE);
		this.setAction(context, right_btn, action,params);
	}

	/**
	 * 设置右侧文字
	 * @param context 当前上下文
	 * @param itemInstance 功能Code
	 */
	public void setRightText(Context context,String text,OnClickListener listener) {
		this.right_btn.setVisibility(View.INVISIBLE);
		this.navtop_right_tv.setVisibility(View.VISIBLE);
		this.navtop_right_tv.setText(text);
		this.navtop_right_tv.setOnClickListener(listener);
	}
	
	/**
	 * 设置标题（图片）
	 * @param context 当前上下文
	 * @param drawable 图片ID
	 */
	public void setTitle(Context context,int drawable) {
		if(this.title_tv_left == null){
			return;
		}
		this.title_tv_image.setVisibility(View.VISIBLE);
		if(drawable == IMAGE_LOGO){ 
			this.title_tv_left.setVisibility(View.GONE);
			this.title_tv_right.setVisibility(View.GONE);
			this.title_tv_image.setBackgroundResource(R.drawable.index_logo);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UIUtil.getAdaptedPixel_Int(R.dimen.titlebar_logo_width, 1.0f)
					,UIUtil.getAdaptedPixel_Int(R.dimen.titlebar_logo_height, 1.0f));
			this.title_tv_image.setLayoutParams(params);
		}
	}
	
	/**
	 * 设置标题（文字）
	 * @param context 当前上下文
	 * @param title 标题内容
	 */
	public void setTitle(Context context,String title) {
		if(this.title_tv_left == null){
			return;
		}else{
			this.title_tv_left.setVisibility(View.VISIBLE);
			this.title_tv_image.setVisibility(View.GONE);
			this.title_tv_right.setVisibility(View.GONE);
		}
		this.title_tv_left.setBackgroundColor(0x00FFFFFF);
		this.title_tv_left.setText(title);
	}

    public void setTwoTitle(Context context,String leftTitle,String rightTitle) {
        if(this.title_tv_left == null){
            return;
        }else{
            this.title_tv_left.setVisibility(View.VISIBLE);
            this.title_tv_image.setVisibility(View.GONE);
            this.title_tv_right.setVisibility(View.VISIBLE);
        }
        this.title_tv_left.setBackgroundColor(0x00FFFFFF);
        this.title_tv_left.setText(leftTitle);
        this.title_tv_right.setBackgroundColor(0x00FFFFFF);
        this.title_tv_right.setText(rightTitle);
    }

	public void setAction(final Context context, final Button btn, int action,final String[] params){
		if (btn == null) {
			return;
		}else if(btn.getVisibility()==View.GONE){
			btn.setVisibility(View.VISIBLE);
		}
		
		if(action == ACTION_BACK){
			try {
				btn.setBackgroundResource(R.drawable.selector_back);
			} catch (Exception e) {
				e.printStackTrace();
			}
			btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					((Activity) v.getContext()).finish();
					((Activity) v.getContext()).overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
				}
			});
		}
		
	}

}
