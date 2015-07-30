package net.oilchem.communication.sms.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.activity.OilActivityBase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class OilUtil {
	private static Toast toast;
	private static DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void showMessage(OilActivityBase activity, String message) {
		LayoutInflater inflater = (LayoutInflater) ((Context) activity).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View layout = inflater.inflate(R.layout.custom, (ViewGroup) findViewById(R.id.llToast));
//		ImageView image = (ImageView) layout.findViewById(R.id.tvImageToast);
//   image.setImageResource(R.drawable.icon);
//   TextView title = (TextView) layout.findViewById(R.id.tvTitleToast);
//   title.setText("Attention");
//   TextView text = (TextView) layout.findViewById(R.id.tvTextToast);
//   text.setText("完全自定义Toast");
//   toast = new Toast(getApplicationContext());
//   toast.setGravity(Gravity.RIGHT | Gravity.TOP, 12, 40);
//   toast.setDuration(Toast.LENGTH_LONG);
//   toast.setView(layout);
//   toast.show();
	}

	public static void showToast(int resId) {
		try {
			if (toast != null) {
				toast.cancel();
				toast = null;
			}
			toast = Toast.makeText(OilchemApplication.getContextFromApplication(), resId, Toast.LENGTH_SHORT);
			toast.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void showToast(String msg) {
		try {
			if (toast != null) {
				toast.cancel();
				toast = null;
			}
			toast = Toast.makeText(OilchemApplication.getContextFromApplication(), msg, Toast.LENGTH_SHORT);
			toast.show();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	public static String getNativeFile(String fileName) {
		StringBuffer json = new StringBuffer();
		InputStream in = null;
		InputStreamReader isr = null;
		BufferedReader reader = null;
		try {
			in = OilchemApplication.getContextFromApplication().getAssets().open("emoticons/json/" + fileName);
			isr = new InputStreamReader(in, "utf8");
            reader = new BufferedReader(isr);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
            	json.append(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            if (isr != null) {
                try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            if (in != null) {
            	try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
        return json.toString();
	}

	public static String getFormatString(String dateTimestamp) {
		return dateFormater.format(new Timestamp(getLongTimemillis(dateTimestamp)));
	}

	public static String getFormatString(String dateTimestamp, DateFormat formater) {
		return formater.format(new Timestamp(getLongTimemillis(dateTimestamp)));
	}

	public static long getLongTimemillis(String dateTimestamp) {
		try {
			if (!TextUtils.isEmpty(dateTimestamp) && dateTimestamp.length() == 10) {
				return Long.parseLong(dateTimestamp) * 1000;
			} else if (!TextUtils.isEmpty(dateTimestamp) && dateTimestamp.length() == 13) {
				return Long.parseLong(dateTimestamp);
			}
		} catch(Exception e) {
		}
		return System.currentTimeMillis();
	}

	public static String getMD5(String origStr) {
		byte[] source = origStr.getBytes(); 
		String s = null;
		char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
										// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
											// 所以表示成 16 进制需要 32 个字符
			int k = 0; // 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
											// 转换成 16 进制字符的转换
				byte byte0 = tmp[i]; // 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
															// >>>
															// 为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
			}
			s = new String(str); // 换后的结果转换为字符串

		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}


	public static String getWeekDayByIndex(int weekdayInt) {
		String weekDayString = null;
		switch(weekdayInt) {
		case 0:
			weekDayString = OilchemApplication.getResourceString(R.string.weekday_monday);
			break;
		case 1:
			weekDayString = OilchemApplication.getResourceString(R.string.weekday_tuesday);
			break;
		case 2:
			weekDayString = OilchemApplication.getResourceString(R.string.weekday_wednesday);
			break;
		case 3:
			weekDayString = OilchemApplication.getResourceString(R.string.weekday_thursday);
			break;
		case 4:
			weekDayString = OilchemApplication.getResourceString(R.string.weekday_friday);
			break;
		case 5:
			weekDayString = OilchemApplication.getResourceString(R.string.weekday_saturday);
			break;
		case 6:
			weekDayString = OilchemApplication.getResourceString(R.string.weekday_sunday);
			break;
		default:
			break;
		}
		return weekDayString;
	}
	
    public static void setHideSoftInputFromWindow(Activity activity){
    	if(activity == null){
    		return;
    	}
    	try {
    		InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
