/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.uci.ics.crawler4j.examples.imagecrawler;

import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.examples.basic.BasicCrawlController;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.uci.ics.crawler4j.util.IO;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */

/*
 * This class shows how you can crawl images on the web and store them in a
 * folder. This is just for demonstration purposes and doesn't scale for large
 * number of images. For crawling millions of images you would need to store
 * downloaded images in a hierarchy of folders
 */
public class ImageCrawler extends WebCrawler {
	

	private static File storageFolder;
	private static String[] crawlDomains;
	private InputStream InURL;
	private String ansURL;
	public String temps;
	private static JTextArea info;
	
	public static void main(String agrs[]) {
		
		String title = "crawler";
		JFrame nw = new JFrame(title);
		nw.setSize(1200, 400);
		//nw.setLocation(100, 100);
		
		JPanel container = new JPanel();//中间容器
		nw.add(container,"North");
		
		final Label nameURL = new Label("URL:");
		final Label cfilename = new Label("记录文件位置:");
		final Label filename = new Label("爬取文件位置:");
		final Label cnum = new Label("爬虫数量:");
		
		final JTextField bnameURL = new JTextField("http://www.nit.zju.edu.cn/",15);
		final JTextField bcfilename = new JTextField("./temp",15);
		final JTextField bfilename = new JTextField("./temp",15);
		final JTextField bcnum = new JTextField("5",15);
		info = new JTextArea();
		
		JButton bsta = new JButton("开始爬虫");
		JButton bclear = new JButton("清除内容");
		info.setLineWrap(true);
		info.setWrapStyleWord(true);
		
		//为JTextArea添加滚动条
		 JScrollPane jsp = new JScrollPane(info);
	     jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	     jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
	     nw.getContentPane().add(jsp,"Center");
	     
		//要爬取的域名
		container.add(nameURL);
		container.add(bnameURL);
		
		//爬虫临时记录文件位置
		container.add(cfilename);
		container.add(bcfilename);
		
		//爬虫文件位置
		container.add(filename);
		container.add(bfilename);
		
		//爬虫数量
		container.add(cnum);
		container.add(bcnum);
		
		container.add(bsta,BorderLayout.CENTER);
		container.add(bclear,BorderLayout.CENTER);
		
		//布局
		//container.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		nw.setVisible(true);
		nw.setDefaultCloseOperation(nw.EXIT_ON_CLOSE);
		
		bsta.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
                try {
                	String NameURL = bnameURL.getText();
                    String Cfilename = bcfilename.getText();
                    String Filename = bfilename.getText();
                    String Cnum = bcnum.getText();
                    
                    ImageCrawlController.work(Cfilename, Filename, NameURL, Cnum);
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
		});
		
		bclear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				info.setText("");
            }
		});
		nw.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
	}
	
	private static final Pattern filters = Pattern.compile(".*(\\.(css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf"
			+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	private static final Pattern imgPatterns = Pattern.compile(".*(\\.(bmp|gif|jpe?g|png|tiff?))$");

	//private static File storageFolder;
	//private static String[] crawlDomains;

	public static void configure(String[] domain, String storageFolderName) {
		ImageCrawler.crawlDomains = domain;

		storageFolder = new File(storageFolderName);
		if (!storageFolder.exists()) {
			storageFolder.mkdirs();
		}
	}

	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		if (filters.matcher(href).matches()) {
			return false;
		}

		if (imgPatterns.matcher(href).matches()) {
			return true;
		}

		for (String domain : crawlDomains) {
			if (href.startsWith(domain)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();

		// We are only interested in processing images
		if (!(page.getParseData() instanceof BinaryParseData)) {
			return;
		}

		if (!imgPatterns.matcher(url).matches()) {
			return;
		}

		// Not interested in very small images
		if (page.getContentData().length < 10 * 1024) {
			return;
		}

		// get a unique name for storing this image
		String extension = url.substring(url.lastIndexOf("."));
		String hashedName = Cryptography.MD5(url) + extension;
		
		// store image
		IO.writeBytesToFile(page.getContentData(), storageFolder.getAbsolutePath() + "/" + hashedName);
		
		downloadPage(url,storageFolder.getName());
		
		//System.out.println("Stored: " + url);
	}
	
	private void downloadPage(String url,String content){
		String names[] = url.split("/");

		if(!storageFolder.exists()){
			storageFolder.mkdirs();
		}
		int index = 0;
		byte bytes[] = new byte[1024*1000];
			try{
				URL tempURL = new URL(url);
				InURL = tempURL.openStream();
				int cont = InURL.read(bytes,index,1024*1000);
				while(cont!=-1){
					index += cont;
					cont = InURL.read(bytes, index, 1);
				}
				
				temps=storageFolder+"\\"+names[names.length-1];
				FileOutputStream outs = new FileOutputStream(temps);
				try {
					info.append("url："+url+"\n");
					info.append("文件位置："+temps+"\n");
					info.paintImmediately(info.getBounds());
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				outs.write(bytes, 0, index);
				//System.out.println(index);
				InURL.close();
				outs.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
}
