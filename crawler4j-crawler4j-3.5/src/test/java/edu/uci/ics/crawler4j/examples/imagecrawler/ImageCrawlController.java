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

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.io.*;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */

/*
 * IMPORTANT: Make sure that you update crawler4j.properties file and set
 * crawler.include_images to true
 */

public class ImageCrawlController {

	//String cfilename,String filename,String cDomains,String cnum
	public static void work(String cfilename,String filename,String cDomains,String cnum) throws Exception {
		String[] str = new String[10];
		/*
		BufferedReader st=new BufferedReader(new InputStreamReader(System.in));
		System.out.print("请输入  爬取时数据临时存放目录:");
		str[0]=st.readLine();
		//System.out.println(str[0]);
		System.out.print("请输入  爬取线程数:");
		str[1]=st.readLine();
		//System.out.println(str[1]);
		System.out.print("请输入  爬取时图片存放目录:");
		str[2]=st.readLine();
		//System.out.println(str[2]);
		*/
		str[0] = cfilename; 	//数据临时存放目录
		str[1] = cnum; 			//爬虫线程数
		str[2] = filename;		//爬虫文件存放目录
		
		if (str.length < 3) {
			System.out.println("Needed parameters: ");
			System.out.println("\t rootFolder (it will contain intermediate crawl data)");
			System.out.println("\t numberOfCralwers (number of concurrent threads)");
			System.out.println("\t storageFolder (a folder for storing downloaded images)");
			return;
		}
		String rootFolder = str[0];
		int numberOfCrawlers = Integer.parseInt(str[1]);
		String storageFolder = str[2];

		CrawlConfig config = new CrawlConfig();

		config.setCrawlStorageFolder(rootFolder);

		/*
		 * Since images are binary content, we need to set this parameter to
		 * true to make sure they are included in the crawl.
		 */
		config.setIncludeBinaryContentInCrawling(true);
		//待处理
		String[] crawlDomains = new String[1];
		//System.out.print("请输入 要 爬取的域名（http://www.nit.zju.edu.cn/）:");
		//str[3]=st.readLine();
		//crawlDomains[0]= str[3];
		
		crawlDomains[0] = cDomains;
		
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		for (String domain : crawlDomains) {
			controller.addSeed(domain);
		}

		ImageCrawler.configure(crawlDomains, storageFolder);
		
		controller.start(ImageCrawler.class, numberOfCrawlers);
	}
/*
	private static Scanner Scanner(InputStream in) {
		// TODO Auto-generated method stub
		return null;
	}
*/
}
