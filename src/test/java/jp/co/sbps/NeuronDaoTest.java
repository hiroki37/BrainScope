package jp.co.sbps;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/*
 * neuronDaoが適切に動作しているかを確認するプログラム
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BrainScopeApplication.class)
@Transactional
public class NeuronDaoTest {
	
	@Autowired
	private NeuronDao neuronDao;
	
	@Autowired
	private JdbcTemplate jdbc;
	
	Date date = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Before
	public void setup() {
		// neuronテーブルの初期化
		jdbc.update("DELETE FROM neuron");
		jdbc.update("INSERT INTO neuron VALUES (1, 'ニューロン１', 'コンテンツ１', 1, 1, 1024, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (2, 'ニューロン２', 'コンテンツ２', 2, 200, 800, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (3, 'ニューロン３', 'コンテンツ３', 2, 900, 1000, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (4, 'ニューロン４', 'コンテンツ４', 3, 300, 400, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (5, 'ニューロン５', 'コンテンツ５', 3, 500, 600, '2016-04-01', '2016-04-01')");
		
		// configテーブルの初期化
		jdbc.update("DELETE FROM config");
		jdbc.update("INSERT INTO config VALUES (1, 2)");
	}
	
	@Test
	public void display_現在のスコープアドレスのニューロンとそれより１つ深いニューロンのすべてを返すことを確認する() {
		// SetUp
		Integer scopeAddress = 1;
		jdbc.update("UPDATE config SET scope_address=" + scopeAddress);
		
		// Exercise
		String actual = String.valueOf(neuronDao.display());
		
		// Verify
		assertThat(actual, is("[{id=1, title=ニューロン１, content=コンテンツ１, neuron_level=1, left_edge=1.0, right_edge=1024.0, create_date=2016-04-01, update_date=2016-04-01}, "
				+ "{id=2, title=ニューロン２, content=コンテンツ２, neuron_level=2, left_edge=200.0, right_edge=800.0, create_date=2016-04-01, update_date=2016-04-01}, "
				+ "{id=3, title=ニューロン３, content=コンテンツ３, neuron_level=2, left_edge=900.0, right_edge=1000.0, create_date=2016-04-01, update_date=2016-04-01}]"));
	}
	
	@Test
	public void update_ニューロンが更新されることを確認する() {
		// SetUp
		Integer id = 1;
		String title = "更新されたタイトル";
		String content = "更新されたコンテンツ";
		
		// Exercise
		neuronDao.update(id, title, content);
		String actual = String.valueOf(jdbc.queryForList("SELECT id, title, content FROM neuron WHERE id = ?",id));
		
		// Verify
		assertThat(actual, is("[{id=" + id + ", title=" + title + ", content=" + content + "}]"));
	}
	
	@Test
	public void generate_ニューロンが生成されることを確認する() {
	// 他の子ニューロンより左側であることを確かめていないため、要加筆
		// SetUp
		Integer parentId = 3;
		Integer parentNeuronLevel = jdbc.queryForObject("SELECT neuron_level FROM neuron WHERE id = ?", Integer.class, parentId);
		Float parentLeftEdge = jdbc.queryForObject("SELECT left_edge FROM neuron WHERE id = ?", Float.class, parentId);
		Float parentRightEdge = jdbc.queryForObject("SELECT right_edge FROM neuron WHERE id = ?", Float.class, parentId);
		
		String title = "新しいニューロン"; // neuronDaoのgenerate()参照
		
		// Exercise
		neuronDao.generate(parentId);
		String childTitle = jdbc.queryForObject("SELECT title FROM neuron WHERE title = ?", String.class, title);
		Integer childNeuronLevel = jdbc.queryForObject("SELECT neuron_level FROM neuron WHERE title = ?", Integer.class, title);
		Float childLeftEdge = jdbc.queryForObject("SELECT left_edge FROM neuron WHERE title = ?", Float.class, title);
		Float childRightEdge = jdbc.queryForObject("SELECT right_edge FROM neuron WHERE title = ?", Float.class, title);
		
		// Verify
		assertThat(title, is(childTitle));
		assertThat(childNeuronLevel, is(parentNeuronLevel+1));
		assertThat(childLeftEdge, is(greaterThan(parentLeftEdge)));
		assertThat(childRightEdge, is(lessThan(parentRightEdge)));
	}
	
	@Test
	public void extinct_ニューロンが削除されることを確認する() {
		// SetUp
		Integer id = 5;
		
		// Exercise
		neuronDao.extinct(id);
		String actual = String.valueOf(jdbc.queryForList("SELECT * FROM neuron WHERE id = " + id));
		
		// Verify
		assertThat(actual, is("[]"));
	}
	
	@Test
	// ※他ニューロンの座標の範囲内であることを確かめていないため、要加筆
	// ※子ニューロンの深さを確かめていないため、要加筆
	public void insert_ニューロンが挿入されることを確認する() {  
		// SetUp
		Integer id = 4;
		Integer childNeuronLevel = jdbc.queryForObject("SELECT neuron_level FROM neuron WHERE id = ?", Integer.class, id);
		Float childLeftEdge = jdbc.queryForObject("SELECT left_edge FROM neuron WHERE id = ?", Float.class, id);
		Float childRightEdge = jdbc.queryForObject("SELECT right_edge FROM neuron WHERE id = ?", Float.class, id);
		
		String title = "挿入されたニューロン"; // neuronDaoのinsert()参照
		
		// Exercise
		neuronDao.insert(id);
		String insertedTitle = jdbc.queryForObject("SELECT title FROM neuron WHERE title = ?", String.class, title);
		Integer insertedNeuronLevel = jdbc.queryForObject("SELECT neuron_level FROM neuron WHERE title = ?", Integer.class, title);
		Float insertedLeftEdge = jdbc.queryForObject("SELECT left_edge FROM neuron WHERE title = ?", Float.class, title);
		Float insertedRightEdge = jdbc.queryForObject("SELECT right_edge FROM neuron WHERE title = ?", Float.class, title);
		
		// Verify
		assertThat(title, is(insertedTitle));
		assertThat(insertedNeuronLevel, is(childNeuronLevel));
		assertThat(insertedLeftEdge, is(lessThan(childLeftEdge)));
		assertThat(insertedRightEdge, is(greaterThan(childRightEdge)));
	}
	
	@Test
	public void generateLeft_生成する際の左端座標が正しいことを確認する() {
	// ※本来２パターン必要。要加筆
		// SetUp
		jdbc.update("DELETE FROM neuron");
		jdbc.update("INSERT INTO neuron VALUES (1, 'ニューロン１', 'コンテンツ１', 1, 1, 1024, '2016-04-01', '2016-04-01')");
		
		Integer id = 1;
		Float trueLeftEdge =  (float) 342;
		
		// Exercise
		Float generatedLeft = neuronDao.generateLeft(id);
		
		// Verify
		assertThat(generatedLeft, is(trueLeftEdge));
	}
	
	@Test
	public void generateRight_生成する際の右端座標が正しいことを確認する() {
	// ※本来２パターン必要。要加筆
		// SetUp
		jdbc.update("DELETE FROM neuron");
		jdbc.update("INSERT INTO neuron VALUES (1, 'ニューロン１', 'コンテンツ１', 1, 1, 1024, '2016-04-01', '2016-04-01')");
		
		Integer id = 1;
		Float trueRightEdge =  (float) 683;
		
		// Exercise
		Float generatedRightEdge = neuronDao.generateRight(id);
		
		// Verify
		assertThat(generatedRightEdge, is(trueRightEdge));
	}
	
	@Test
	public void insertLeft_挿入する際の左端座標が正しいことを確認する() {
	// ※本来３パターン必要。要加筆
		// SetUp
		jdbc.update("DELETE FROM neuron");
		jdbc.update("INSERT INTO neuron VALUES (1, 'ニューロン１', 'コンテンツ１', 1, 1, 1024, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (2, 'ニューロン２', 'コンテンツ２', 2, 201, 824, '2016-04-01', '2016-04-01')");
		
		Integer id = 2;
		Float trueLeftEdge = (float) 101;
		
		// Exercise
		Float insertedLeftEdge = neuronDao.insertLeft(id); 
		
		// Verify
		assertThat(insertedLeftEdge, is(trueLeftEdge));
	}
	
	@Test
	public void insertRihgt_挿入する際の右端座標が正しいことを確認する() {
	// ※本来３パターン必要。要加筆
		// SetUp
		jdbc.update("DELETE FROM neuron");
		jdbc.update("INSERT INTO neuron VALUES (1, 'ニューロン１', 'コンテンツ１', 1, 1, 1024, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (2, 'ニューロン２', 'コンテンツ２', 2, 201, 824, '2016-04-01', '2016-04-01')");
		
		Integer id = 2;
		Float trueRightEdge = (float) 924;
		
		// Exercise
		Float insertedRightEdge = neuronDao.insertRight(id); 
		
		// Verify
		assertThat(insertedRightEdge, is(trueRightEdge));
	}
	
	@Test
	public void title_適切なタイトルが出力されることを確認する() {
		// SetUp
		Integer id = 1;
		
		// Exercise
		String actual = neuronDao.title(id);
		
		// Verify
		assertThat(actual, is("ニューロン１"));
	}
	
	@Test
	public void content_適切なコンテンツが出力されることを確認する() {
		// SetUp
		Integer id = 1;
		
		// Exercise
		String actual = neuronDao.content(id);
		
		// Verify
		assertThat(actual, is("コンテンツ１"));
	}
	
	@Test
	public void neuron_level_適切なニューロンの深さが出力されることを確認する() {
		// SetUp
		Integer id = 1;
		
		// Exercise
		Integer actual = neuronDao.neuronLevel(id);
		
		// Verify
		assertThat(actual, is(1));
	}
	
	@Test
	public void left_edge_適切な左端座標が出力されることを確認する() {
		// SetUp
		Integer id = 1;
		
		// Exercise
		Float actual = neuronDao.leftEdge(id);
		Float expected = (float) 1;
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void right_edge_適切な右端座標が出力されることを確認する() {
		// SetUp
		Integer id = 1;
		
		// Exercise
		Float actual = neuronDao.rightEdge(id);
		Float expected = (float) 1024;
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void greatest_２つの値の最大値が出力されることを確認する() {
		// SetUp
		Float var1 = (float) 1024, var2 = (float) 2016;
		
		// Exercise
		Float actual = neuronDao.greatest(var1, var2);
		
		// Verify
		assertThat(actual, is(var2));
	}
	
	// @Test
	public void minLeftEdge() {
		// SetUp
		jdbc.update("DELETE FROM neuron");
		jdbc.update("INSERT INTO neuron VALUES (1, 'ニューロン１', 'コンテンツ１', 1, 1, 1024, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (2, 'ニューロン２', 'コンテンツ２', 2, 200, 300, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (3, 'ニューロン３', 'コンテンツ３', 2, 400, 500, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (4, 'ニューロン４', 'コンテンツ４', 2, 600, 700, '2016-04-01', '2016-04-01')");
		
		Integer id = 2;
		Float trueMinLeftEdge = (float) 600;
		Float rightEdge = jdbc.queryForObject("SELECT right_edge FROM neuron WHERE id = ?", Float.class, id); 
		
		// Exercise
		Float actualMinLeftEdge = neuronDao.minLeftEdge(rightEdge);
		
		// Verify
		assertThat(actualMinLeftEdge, is(trueMinLeftEdge));
	}
	
	// @Test
	public void maxRightEdge() {
		// SetUp
		jdbc.update("DELETE FROM neuron");
		jdbc.update("INSERT INTO neuron VALUES (1, 'ニューロン１', 'コンテンツ１', 1, 1, 1024, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (2, 'ニューロン２', 'コンテンツ２', 2, 200, 300, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (3, 'ニューロン３', 'コンテンツ３', 2, 400, 500, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (4, 'ニューロン４', 'コンテンツ４', 2, 600, 700, '2016-04-01', '2016-04-01')");
		
	}
	
	// @Test
	public void parentLeftEdge() {
		// SetUp
		
		// Exercise
		
		// Verify

	}
	
	// @Test
	public void parentRightEdge() {
		// SetUp
		
		// Exercise
		
		// Verify

	}
}