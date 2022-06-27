package cn.itutopia.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;

/**
 * @description: Lucene-Demo
 * 1. 创建索引
 * 2. 查询索引
 * 3. 查看分析器效果
 * 4. IK分词器
 * @author: Junchao_Lee
 * @e-mail: ljch867@163.com
 * @date: 2022/6/9 01:05
 */
public class LuceneDemo {

    /**
     * 创建索引
     *
     * @throws Exception
     */
    @Test
    public void createIndex() throws Exception {
        // 1. 创建一个Directory(目录)对象,指定索引库保存的路径
        Directory directory = FSDirectory.open(new File("/Users/junchao_lee/Documents/itutopia/it_basic/itutopia-lucene/temp/index").toPath());
        // 2. 基于Directory对象创建 IndexWriter对象, 使用标准分析器(StandardAnalyzer),指定分析英文有效.
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig());
        // 3. 读取磁盘上的文件,对应每个文件创建一个文档对象
        File dir = new File("/Users/junchao_lee/Documents/itutopia/it_basic/itutopia-lucene/files");
        File[] files = dir.listFiles();
        for (File file : files) {
            // 文件名
            String fileName = file.getName();
            // 文件路径
            String filePath = file.getPath();
            // 文件内容
            String fileContent = FileUtils.readFileToString(file, "utf-8");
            // 文件大小
            long fileSize = FileUtils.sizeOf(file);

            // 创建Field域
            TextField fieldName = new TextField("name", fileName, Field.Store.YES);
            TextField fieldPath = new TextField("path", filePath, Field.Store.YES);
            TextField fieldContent = new TextField("content", fileContent, Field.Store.YES);
            TextField fieldSize = new TextField("size", fileSize + "", Field.Store.YES);

            // 创建文档对象,文档对象中添加域
            Document document = new Document();
            document.add(fieldName);
            document.add(fieldPath);
            document.add(fieldContent);
            document.add(fieldSize);
            // 5. 把文档对象写入索引库
            indexWriter.addDocument(document);
        }
        // 6. 关闭IndexWriter对象
        indexWriter.close();

    }

    /**
     * 查询索引
     * @throws Exception
     */
    @Test
    public void searchIndex() throws Exception {
        // 1.创建一个Directory对象,指定索引库的路径
        Directory directory = FSDirectory.open(new File("/Users/junchao_lee/Documents/itutopia/it_basic/itutopia-lucene/temp/index").toPath());
        // 2.创建一个IndexReader对象
        DirectoryReader indexReader = DirectoryReader.open(directory);
        // 3.创建一个IndexSearcher对象,构造方法中的参数indexReader对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        // 4. 创建一个Query对象->TermQuery
        Query query = new TermQuery(new Term("name", "规"));
        // 5. 执行查询,得到一个TopDocs对象
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("查询总记录数:" + topDocs.totalHits);
        // 获取文档列表
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc sdoc : scoreDocs) {
            // 获取文档id
            int docId = sdoc.doc;
            // 根据id取文档对象
            Document document = indexSearcher.doc(docId);
            System.out.println(document.get("name"));
            System.out.println(document.get("path"));
            System.out.println(document.get("content"));
            System.out.println(document.get("size"));
            System.out.println("--------分割线----------");
        }
        indexReader.close();
    }

    /**
     * 查看分析器效果
     */
    @Test
    public void testAnalyzerTokenStream() throws Exception {
        // 1. 创建一个Analyzer对象,StandardAnalyzer对象
        StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
        // 2. 使用分析器对象tokenStream方法获取一个TokenStream对象
//        TokenStream tokenStream = standardAnalyzer.tokenStream("", "The Spring Framework provides a comprehensive programming and configuration model.");
        TokenStream tokenStream = standardAnalyzer.tokenStream("title", "lucene 分词器介绍 The Spring Framework provides a comprehensive programming and configuration model.");
        // 3. 向TokenStream对象设置一个引用,相当于一个指针
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        // 4. 调用TokenStream对象reset方法,如果不调用会抛异常
        tokenStream.reset();
        // 5. 使用While循环遍历TokenStream对象
        while (tokenStream.incrementToken()) {
            System.out.println(charTermAttribute.toString());
        }
        // 6.关闭TokenStream对象
        tokenStream.close();
    }


    /**
     * IK分词器
     * @param args
     */
    public static void main(String[] args) {

    }
}
