package com.xw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

public class Config {

	File file;
	Document document;
	static Config m_Instance;

	public static String TABLE_1 = "table_1";
	public static String TABLE_2 = "table_2";
	public static String TABLE_3 = "table_3";

	public static Config getInstance() {
		if (m_Instance == null)
			m_Instance = new Config();
		return m_Instance;
	}

	public Config(){
		try {
			SAXReader reader = new SAXReader();
			file = new File("test.xml");
			FileInputStream in = new FileInputStream(file);
			document = reader.read(in);
		} catch (DocumentException | FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("cant read config file!");
		}
	}

	Element get(String path) {
		String[] sp_path = path.split("\\.");

		Element e = document.getRootElement();

		for (int i = 0; i < sp_path.length; i++) {
			Iterator<Element> it = e.elementIterator();
			while (it.hasNext()) {
				Element child = it.next();
				if (child.getName().equals(sp_path[i])) {
					if (i + 1 < sp_path.length) {
						e = child;
						break;
					} else {
						return child;
					}
				}
			}
		}
		return null;
	}

	void save() throws IOException  {
		// System.out.println("Save config");
		FileOutputStream os = new FileOutputStream(file);
		os.write(document.asXML().getBytes("UTF-8"));
		os.flush();
		os.close();
	}

	public HashSet<String> getColumnConfig(String tableName) {
		HashSet<String> m_set = new HashSet<>();

		Element e = get("TableList." + tableName);
		Iterator<Element> it = e.elementIterator();
		while (it.hasNext()) {
			Element child = it.next();
			if (child.getName().equals("column")) {
				m_set.add(child.attributeValue("name"));
			}
		}
		return m_set;
	}

	public void addColumnConfig(String tableName, String columnName) throws IOException {
		Element e = document.getRootElement();
		Element tableList = e.element("TableList");
		Element table = tableList.element(tableName);
		Element column = table.addElement("column");
		column.addAttribute("name", columnName);
		save();
	}

	public void cleanColumnConfig(String tableName) throws IOException  {
		Element table_list = document.getRootElement().element("TableList");
		Element table = table_list.element(tableName);
		if (null == table)
			table = table_list.addElement(tableName);
		Iterator<Element> it = table.elementIterator();
		while (it.hasNext()) {
			Element child = it.next();
			if (child.getName().equals("column")) {
				table.remove(child);
			}
		}
		save();
	}

	public HashSet<String> getStaffSet() throws IOException {
		HashSet<String> name_set = new HashSet<>();

		Element e = document.getRootElement();
		Iterator<Element> it = e.elementIterator();
		while (it.hasNext()) {
			Element child = it.next();
			if (child.getName().equals("Staff")) {
				name_set.add(child.attributeValue("name"));
			}
		}
		return name_set;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Config c = new Config();
			for (String s : c.getColumnConfig(Config.TABLE_1)) {
				System.out.println(s);
			}
			c.addColumnConfig(Config.TABLE_1, "columnName");
			c.cleanColumnConfig(TABLE_1);
			c.save();
			return;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
