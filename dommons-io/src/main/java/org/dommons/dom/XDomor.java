/*
 * @(#)XDomor.java     2011-11-1
 */
package org.dommons.dom;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import org.dommons.core.Assertor;
import org.dommons.core.convert.Converter;
import org.dommons.dom.bean.XDocument;
import org.dommons.dom.bean.XFormat;
import org.dommons.io.Pathfinder;
import org.dommons.io.file.ContentWriter;
import org.dommons.io.file.FileRoboter;
import org.xml.sax.SAXException;

/**
 * XML 操作工具类
 * @author Demon 2011-11-1
 */
public final class XDomor {

	/**
	 * 创建 XML 文档
	 * @return XML 文档
	 */
	public static XDocument create() {
		return XDocumentFactory.getInstance().create();
	}

	/**
	 * 解析 XML
	 * @param file XML 文件
	 * @return XML 文档对象
	 * @throws SAXException 解析出错
	 * @throws IOException 读取出错
	 */
	public static XDocument parse(File file) throws SAXException, IOException {
		return XDocumentFactory.getInstance().parse(file);
	}

	/**
	 * 解析 XML
	 * @param file XML 文件
	 * @param validation 校验文件
	 * @param type 校验类型{@link XValidation}
	 * @return XML 文档对象
	 * @throws SAXException 解析出错
	 * @throws IOException 读取出错
	 */
	public static XDocument parse(File file, URL validation, int type) throws SAXException, IOException {
		return XDocumentFactory.getInstance().parse(file, validation, type);
	}

	/**
	 * 解析 XML
	 * @param is 输入流
	 * @return XML 文档对象
	 * @throws SAXException 解析出错
	 * @throws IOException 读取出错
	 */
	public static XDocument parse(InputStream is) throws SAXException, IOException {
		return XDocumentFactory.getInstance().parse(is);
	}

	/**
	 * 解析 XML
	 * @param is 输入流
	 * @param validation 格式校验文件
	 * @param type 校验类型 {@link XValidation}
	 * @return XML 文档对象
	 * @throws SAXException 解析出错
	 * @throws IOException 读取出错
	 */
	public static XDocument parse(InputStream is, URL validation, int type) throws SAXException, IOException {
		return XDocumentFactory.getInstance().parse(is, validation, type);
	}

	/**
	 * 解析 XML
	 * @param reader 内容读取器
	 * @return XML 文档对象
	 * @throws SAXException
	 * @throws IOException
	 */
	public static XDocument parse(Reader reader) throws SAXException, IOException {
		return XDocumentFactory.getInstance().parse(reader);
	}

	/**
	 * 解析 XML
	 * @param url 文件URL
	 * @return XML 文档对象
	 * @throws SAXException 解析出错
	 * @throws IOException 读取出错
	 */
	public static XDocument parse(URL url) throws SAXException, IOException {
		return XDocumentFactory.getInstance().parse(url);
	}

	/**
	 * 解析 XML
	 * @param url 文件URL
	 * @param validation 格式校验文件
	 * @param type 校验类型 {@link XValidation}
	 * @return XML 文档对象
	 * @throws SAXException 解析出错
	 * @throws IOException 读取出错
	 */
	public static XDocument parse(URL url, URL validation, int type) throws SAXException, IOException {
		return XDocumentFactory.getInstance().parse(url, validation, type);
	}

	/**
	 * 保存 XML 文档
	 * @param doc XML 文档
	 * @param file 保存文件
	 * @param format 保存格式
	 * @throws IOException
	 */
	public static void store(final XDocument doc, File file, final XFormat format) throws IOException {
		Assertor.F.notNull(doc, "The document is must not be null!");
		Assertor.F.notNull(file, "The file is must not be null!");
		FileRoboter.write(file, new ContentWriter() {
			public void write(OutputStream out) throws IOException {
				store(doc, out, format);
			}
		});
	}

	/**
	 * 保存 XML 文档
	 * @param doc XML 文档
	 * @param out 输出流
	 * @param format 保存格式
	 * @throws IOException
	 */
	public static void store(XDocument doc, OutputStream out, XFormat format) throws IOException {
		Assertor.F.notNull(doc, "The document is must not be null!");
		doc.store(out, format);
	}

	/**
	 * 保存 XML 文档
	 * @param doc XML 文档
	 * @param path 文件路径
	 * @param format 保存格式
	 * @throws IOException
	 */
	public static void store(XDocument doc, String path, XFormat format) throws IOException {
		store(doc, Pathfinder.findFile(path), format);
	}

	/**
	 * 保存 XML 文档
	 * @param doc XML 文档
	 * @param url 文件路径
	 * @param format 保存格式
	 * @throws IOException
	 */
	public static void store(XDocument doc, URL url, XFormat format) throws IOException {
		store(doc, Pathfinder.getFile(url), format);
	}

	/**
	 * 保存 XML 文档
	 * @param doc XML 文档
	 * @param writer 文档写入器
	 * @param format 保存格式
	 * @throws IOException
	 */
	public static void store(XDocument doc, Writer writer, XFormat format) throws IOException {
		Assertor.F.notNull(doc, "The document is must not be null!");
		doc.store(writer, format);
	}

	/**
	 * 转换 XML 字符串
	 * @param doc XML 文档
	 * @param format 转换格式
	 * @return XML 字符串
	 */
	public static String string(XDocument doc, XFormat format) {
		StringWriter sw = new StringWriter();
		try {
			try {
				store(doc, sw, format);
			} finally {
				sw.close();
			}
		} catch (IOException e) {
			throw Converter.F.convert(e, RuntimeException.class);
		}
		return sw.toString();
	}

	private XDomor() {
	}
}
