package cn.itcast.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.DFRSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class IndexManager {

	@Test
	public void testAddIndex() throws Exception {
//		1、执行索引库的目录
		Directory  directory = FSDirectory.open(new File("D:\\class39\\indexRespo"));
		
//		2、指定分词器  --标准分词器
		Analyzer analyzer = new StandardAnalyzer();
		
//		3、创建一个配置对象
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
//		4、创建一个 写入索引对象
		IndexWriter indexWriter = new IndexWriter(directory, config);
		
//		5、写入对象了
		File files = new File("D:\\ITCAST\\Lucene&solr\\Lucene&solr-day01\\资料\\上课用的查询资料searchsource");
		File[] listFiles = files.listFiles();
		for (File file : listFiles) {
			Document doc = new Document();
//			field 域  等同于属性
//			1.create web page.txt   
//			1 
//			create 
//			web 
//			page 
//			txt
//			文件名称
			Field  fileNameField = new TextField("name", file.getName(), Store.YES);
			doc.add(fileNameField);
//			文件路径
			Field  filePathField = new TextField("path", file.getPath(), Store.YES);
			doc.add(filePathField);
//			文件大小  单位 b
			long sizeOf = FileUtils.sizeOf(file);
			Field  fileSizeField = new TextField("size", sizeOf+"", Store.YES);
			doc.add(fileSizeField);
//			文件内容
			String fileContent = FileUtils.readFileToString(file);
			Field  fileContentField = new TextField("content", fileContent, Store.YES);
			doc.add(fileContentField);
			
			indexWriter.addDocument(doc);
		}

//		6、关闭IndexWriter对象
		indexWriter.close();
		
		
	}
	
	@Test
	public void testSearchIndex() throws IOException {
//		1、指定索引库的目录
		Directory  directory = FSDirectory.open(new File("D:\\class39\\indexRespo"));

//		2、创建一个读取索引对象
		IndexReader  indexReader = DirectoryReader.open(directory);
		
//		3、创建一个搜索索引的对象
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//		4、执行查询
		Query query = new TermQuery(new Term("content", "spring is a good project"));
		TopDocs topDocs = indexSearcher.search(query, 100);
		System.out.println("总条数："+topDocs.totalHits);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docId = scoreDoc.doc;
			Document doc = indexSearcher.doc(docId);
			System.out.println(doc.get("name"));
			System.out.println(doc.get("size"));
			System.out.println(doc.get("path"));
//			System.out.println(doc.get("content"));
			System.out.println("----------------------------------------------");
		}
		
//		5、关闭资源
		indexReader.close();
	}
	
	
	@Test
	public void testAnalyzer() throws Exception {
		
//		毛不易
		
//		Analyzer analyzer = new StandardAnalyzer();
//		Analyzer analyzer = new ChineseAnalyzer(); 
//		C J K
//		Analyzer analyzer = new CJKAnalyzer();
//		Analyzer analyzer = new SmartChineseAnalyzer();
		Analyzer analyzer = new IKAnalyzer();
		
//		StandardAnalyzer:中文的分词 一个字一个字
//		ChineseAnalyzer：过时了
//		CJKAnalyzer:两个字两个字  需要导入 lucene-analyzers-smartcn-4.10.3.jar
//		SmartChineseAnalyzer: 中文还算可以，英文会出现少字母的情况 需要导入 lucene-analyzers-smartcn-4.10.3.jar
		
		
//		String str = "The Spring Framework provides a comprehensive programming and configuration model.";
		String str = "传智播客：MyBatis 本是apache的一个开源项目iBatis,法轮功 2010年这个项目由apache software foundation 迁移到了google code，并且改名为MyBatis--by 白面郎君 毛不易";
		
		TokenStream tokenStream = analyzer.tokenStream("test", str);
		
		tokenStream.reset();  //重置指针
//		添加一个引用 字符串
		CharTermAttribute addAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		
		while(tokenStream.incrementToken()) {
			System.out.println(addAttribute);
		}
	}
	
}
