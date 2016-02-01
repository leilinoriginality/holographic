package com.zhilianxinke.holographic.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTools {
	public final static String _chineseEnglishNum = "[`~!@#$%^&*()+=|{}\\\\[\\\\].<>/~@#￥%……&*（）——+|{}【】《》]";

	public static boolean ischineseEnglishNum(String values) {
		Pattern p = Pattern.compile(_chineseEnglishNum);
		Matcher m = p.matcher(values);
		return m.find();
	}
	/**邮箱**/
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}
	/**数字**/
	public static boolean isNumber(String number) {
		String str = "^[0-9]*$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(number);
		return m.matches();
	}
	/**
	 * 验证手机格式
	 */
	public static boolean isMobileNO(String mobiles) {
        /*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
        联通：130、131、132、152、155、156、185、186
        电信：133、153、180、189、（1349卫通）
        总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
        */
		String telRegex = "[1][3587]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles)) return false;
		else return mobiles.matches(telRegex);
	}
}
