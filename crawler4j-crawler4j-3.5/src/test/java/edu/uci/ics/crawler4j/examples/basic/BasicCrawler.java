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

package edu.uci.ics.crawler4j.examples.basic;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.net.URL;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class BasicCrawler extends WebCrawler {


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
                    
					BasicCrawlController.work(Cfilename, Filename, NameURL, Cnum);
					
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
	
	//private static File storageFolder;
	//private static String[] crawlDomains;
	
	
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4"
			+ "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	public static void configure(String[] domain, String storageFolderName) {
		BasicCrawler.crawlDomains = domain;

		storageFolder = new File(storageFolderName);
		if (!storageFolder.exists()) {
			storageFolder.mkdirs();
		}
	}
	
	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches() && href.startsWith(crawlDomains[0]);
	}
	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	*/
	
	
	public void GetURL(Page page) {
		// TODO Auto-generated method stub
		ansURL = page.getWebURL().getURL();
	}
	public String findURL(){
		return ansURL;
	}

	@Override
	public void visit(Page page) {
		/*
		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();
		
		String domain = page.getWebURL().getDomain();
		String path = page.getWebURL().getPath();
		String subDomain = page.getWebURL().getSubDomain();
		String parentUrl = page.getWebURL().getParentUrl();
		String anchor = page.getWebURL().getAnchor();

		System.out.println("Docid: " + docid);
		System.out.println("URL: " + url);
		System.out.println("Domain: '" + domain + "'");
		System.out.println("Sub-domain: '" + subDomain + "'");
		System.out.println("Path: '" + path + "'");
		System.out.println("Parent page: " + parentUrl);
		System.out.println("Anchor text: " + anchor);
	
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			List<WebURL> links = htmlParseData.getOutgoingUrls();

			System.out.println("Text length: " + text.length());
			System.out.println("Html length: " + html.length());
			System.out.println("Number of outgoing links: " + links.size());
		}
		*/
/*
		Header[] responseHeaders = page.getFetchResponseHeaders();
		if (responseHeaders != null) {
			System.out.println("Response headers:");
			for (Header header : responseHeaders) {
				System.out.println("\t" + header.getName() + ": " + header.getValue());
			}
		}
		*/
		String url = page.getWebURL().getURL();
		downloadPage(url,storageFolder.getName());
		
		//System.out.println("=============");
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
