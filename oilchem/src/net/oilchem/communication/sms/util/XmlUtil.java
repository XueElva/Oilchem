package net.oilchem.communication.sms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import net.oilchem.communication.sms.OilchemApplication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.os.Environment;
import android.util.Xml;

public class XmlUtil {

	/**
	 * 根据groupId列表创建XML文件
	 * @param collectedId
	 */
	public static void creatXmlFile(ArrayList<String> collectedIdList){

			File file=new File(OilchemApplication.COLLECTION_PATH, "collection"+OilchemApplication.getUser().getUsername()+".xml");
			if(!file.exists()){
				file.getParentFile().mkdirs();
			}

  //生成xml格式的字符串
			StringWriter stringWriter = new StringWriter();
			try {
				
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				XmlSerializer xmlSerializer = (XmlSerializer) factory.newSerializer();
				// 设置输出流对象
				xmlSerializer.setOutput(stringWriter);
				// 获取XmlSerializer对象
				xmlSerializer.startDocument("utf-8", true);
				xmlSerializer.startTag(null, "collection");
				for (int i = 0; i < collectedIdList.size(); i++) {
					xmlSerializer.startTag(null, "groupId");
					xmlSerializer.text(collectedIdList.get(i));
					xmlSerializer.endTag(null, "groupId");
				}
			
				xmlSerializer.endTag(null, "collection");
				xmlSerializer.endDocument();

			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(file);
				fos.write(stringWriter.toString().getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	
	/**
	 * 获取已收藏列表
	 * @return
	 */
	public static ArrayList<String> getCollectedList(){
		ArrayList<String> collectedIdList = new ArrayList<String>();
		
		if(null !=OilchemApplication.getUser()){
			File file=new File(OilchemApplication.COLLECTION_PATH, "collection"+OilchemApplication.getUser().getUsername()+".xml");
			if (file.exists()) {
				try {
					InputStream inputStream = new FileInputStream(file);

	               //解析xml文件
					XmlPullParser parser = Xml.newPullParser();
					try {
						parser.setInput(inputStream, "UTF-8");
						int eventType = parser.getEventType();
						// 4是本实验的参数个数
						while (eventType != XmlPullParser.END_DOCUMENT ) {

							switch (eventType) {

							case XmlPullParser.START_TAG:
								if (parser.getName().equals("groupId")) {
									collectedIdList.add(parser.nextText());
								} 
								break;
							default:
								break;
							}

							parser.next();
							eventType = parser.getEventType();
						}
					} catch (XmlPullParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		
		return collectedIdList;
		
	}
	
	/**
	 * 删除某个已收藏的groupId
	 */
	public static void cancelCollect(String id){
		ArrayList<String> collectedList=getCollectedList();
		collectedList.remove(id);
		creatXmlFile(collectedList);
	}
	
	/**
	 * 添加收藏
	 */
	public static void collect(String id){
		ArrayList<String> collectedList=getCollectedList();
		
		if(!collectedList.contains(id)){
			collectedList.add(id);
			creatXmlFile(collectedList);
		}
	
	}
	
	
}
