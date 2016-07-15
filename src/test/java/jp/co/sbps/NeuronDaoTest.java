package jp.co.sbps;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
	
	@Before
	public void setup() {
		// neuronテーブルの初期化
		jdbc.update("DELETE FROM neuron");
		jdbc.update("INSERT INTO neuron VALUES (1, 'ニューロン１', 'コンテンツ１', 1, 1, 1024, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (2, 'ニューロン２', 'コンテンツ２', 2, 201, 624, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (3, 'ニューロン３', 'コンテンツ３', 2, 824, 1000, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (4, 'ニューロン４', 'コンテンツ４', 3, 300, 400, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (5, 'ニューロン５', 'コンテンツ５', 3, 500, 600, '2016-04-01', '2016-04-01')");
		
		// configテーブルの初期化
		jdbc.update("DELETE FROM config");
		jdbc.update("INSERT INTO config VALUES (1, 2)");
	}
	
	@Test
	public void display_現在のスコープアドレスのニューロンとそれより１つ深いニューロンのすべてを返すことを確認する() {
		// SetUp
		String expected = "[{id=1, title=ニューロン１, content=コンテンツ１, neuron_level=1, left_edge=1.0, right_edge=1024.0, create_date=2016-04-01, update_date=2016-04-01}, "
				+ "{id=2, title=ニューロン２, content=コンテンツ２, neuron_level=2, left_edge=201.0, right_edge=624.0, create_date=2016-04-01, update_date=2016-04-01}, "
				+ "{id=3, title=ニューロン３, content=コンテンツ３, neuron_level=2, left_edge=824.0, right_edge=1000.0, create_date=2016-04-01, update_date=2016-04-01}]";
		
		// Exercise
		String actual = String.valueOf(neuronDao.display());
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void update_ニューロンが更新されることを確認する() {
		// SetUp
		Integer id = 1;
		String title = "更新されたタイトル";
		String content = "更新されたコンテンツ";
		
		String expected = "[{id=" + id + ", title=" + title + ", content=" + content + "}]";
		
		// Exercise
		neuronDao.update(id, title, content);
		String actual = String.valueOf(jdbc.queryForList("SELECT id, title, content FROM neuron WHERE id = ?",id));
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void generate_ニューロンが生成されることを確認する() {
		// SetUp
		Integer id = 1;
		
		String expectedTitle = "新しいニューロン";
		Integer expectedNeuronLevel = 2;
		Float expectedLeftEdge = (float) 1008;
		Float expectedRightEdge = (float) 1016;
		
		// Exercise
		neuronDao.generate(id);
		String actualTitle = jdbc.queryForObject("SELECT title FROM neuron WHERE title = ?", String.class, expectedTitle);
		Integer actualNeuronLevel = jdbc.queryForObject("SELECT neuron_level FROM neuron WHERE title = ?", Integer.class, expectedTitle);
		Float actualLeftEdge = jdbc.queryForObject("SELECT left_edge FROM neuron WHERE title = ?", Float.class, expectedTitle);;
		Float actualRightEdge = jdbc.queryForObject("SELECT right_edge FROM neuron WHERE title = ?", Float.class, expectedTitle);;
		
		// Verify
		assertThat(actualTitle, is(expectedTitle));
		assertThat(actualNeuronLevel, is(expectedNeuronLevel));
		assertThat(actualLeftEdge, is(expectedLeftEdge));
		assertThat(actualRightEdge, is(expectedRightEdge));
	}
	
	@Test
	public void extinct_ニューロンが削除されることを確認する() {
		// SetUp
		Integer id = 2;
		
		String expected = "[]";
		
		// Exercise
		neuronDao.extinct(id);
		String actual = String.valueOf(jdbc.queryForList("SELECT * FROM neuron WHERE left_edge BETWEEN ? AND ?", (float) 201, (float) 624));
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void insert_ニューロンが挿入されることを確認する() {  
		// SetUp
		Integer id = 2;
		
		String expectedTitle = "挿入されたニューロン";
		Integer expectedNeuronLevel = 2;
		Float expectedLeftEdge = (float) 101;
		Float expectedRightEdge = (float) 724;
		
		Integer expectedId2NeuronLevel = 3;
		Integer expectedId4NeuronLevel = 4;
		Integer expectedId5NeuronLevel = 4;
		
		// Exercise
		neuronDao.insert(id);
		String actualTitle = jdbc.queryForObject("SELECT title FROM neuron WHERE title = ?", String.class, expectedTitle);
		Integer actualNeuronLevel = jdbc.queryForObject("SELECT neuron_level FROM neuron WHERE title = ?", Integer.class, expectedTitle);
		Float actualLeftEdge = jdbc.queryForObject("SELECT left_edge FROM neuron WHERE title = ?", Float.class, expectedTitle);
		Float actualRightEdge = jdbc.queryForObject("SELECT right_edge FROM neuron WHERE title = ?", Float.class, expectedTitle);
		
		Integer actualId2NeuronLevel = jdbc.queryForObject("SELECT neuron_level FROM neuron WHERE id = ?", Integer.class, 2);
		Integer actualId4NeuronLevel = jdbc.queryForObject("SELECT neuron_level FROM neuron WHERE id = ?", Integer.class, 4);
		Integer actualId5NeuronLevel = jdbc.queryForObject("SELECT neuron_level FROM neuron WHERE id = ?", Integer.class, 5);
		
		// Verify
			// 挿入されたニューロンが正しいことの確認
			assertThat(actualTitle, is(expectedTitle));
			assertThat(actualNeuronLevel, is(expectedNeuronLevel));
			assertThat(actualLeftEdge, is(expectedLeftEdge));
			assertThat(actualRightEdge, is(expectedRightEdge));
			
			// 挿入されたニューロンの子ニューロンが１つずつ深くなったことの確認
			assertThat(actualId2NeuronLevel, is(expectedId2NeuronLevel));
			assertThat(actualId4NeuronLevel, is(expectedId4NeuronLevel));
			assertThat(actualId5NeuronLevel, is(expectedId5NeuronLevel));
	}
	
	@Test
	public void generateLeft_生成する際の左端座標が正しいことを確認する() {
		// 親ニューロンの左端と生成するニューロンの左端の間にニューロンが存在する場合
			// SetUp
			Integer id = 1;
			
			Float expectedLeftEdge =  (float) 1008;
			
			// Exercise
			Float actualLeftEdge = neuronDao.generateLeft(id);
			
			// Verify
			assertThat(actualLeftEdge, is(expectedLeftEdge));
			
		// 親ニューロンの左端と生成するニューロンの左端の間にニューロンが存在しない場合
			// SetUp
			neuronDao.extinct(2);
			neuronDao.extinct(3);
			
			expectedLeftEdge =  (float) 342;
			
			// Exercise
			actualLeftEdge = neuronDao.generateLeft(id);
			
			// Verify
			assertThat(actualLeftEdge, is(expectedLeftEdge));
	}
	
	@Test
	public void generateRight_生成する際の右端座標が正しいことを確認する() {
		// SetUp
		Integer id = 1;
		
		Float expectedRightEdge =  (float) 1016;
		
		// Exercise
		Float actualRightEdge = neuronDao.generateRight(id);
		
		// Verify
		assertThat(actualRightEdge, is(expectedRightEdge));
	}
	
	@Test
	public void insertLeft_挿入する際の左端座標が正しいことを確認する() {
	// ※本来３パターン必要。要加筆
		// SetUp
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
	
	@Test
	public void minLeftEdge() {
	// ※値が親の右端座標になる場合を確かめていない。要加筆
		// SetUp
		Integer id = 3;
		Float trueMinLeftEdge = (float) 600;
		Float RightEdge = jdbc.queryForObject("SELECT right_edge FROM neuron WHERE id = ?", Float.class, id); 
		
		// Exercise
		Float actualMinLeftEdge = neuronDao.minLeftEdge(RightEdge);
		
		// Verify
		assertThat(actualMinLeftEdge, is(trueMinLeftEdge));
	}
	
	@Test
	public void maxRightEdge() {
	// ※値が親の左端座標になる場合を確かめていない。要加筆
		// SetUp
		Integer id = 3;
		Float trueMaxRightEdge = (float) 300;
		Float LeftEdge = jdbc.queryForObject("SELECT left_edge FROM neuron WHERE id = ?", Float.class, id); 
		
		// Exercise
		Float actualMaxRightEdge = neuronDao.maxRightEdge(LeftEdge);
		
		// Verify
		assertThat(actualMaxRightEdge, is(trueMaxRightEdge));
	}
	
	@Test
	public void parentLeftEdge_親ニューロンの左端座標を出力する() {
		// SetUp
		Integer id = 3;
		Float trueParentLeftEdge = (float) 1;
		
		// Exercise
		Float actualParentLeftEdge = neuronDao.parentLeftEdge(id);
		
		// Verify
		assertThat(actualParentLeftEdge, is(trueParentLeftEdge));
	}
	
	@Test
	public void parentRightEdge_親ニューロンの右端座標を出力する() {
		// SetUp
Integer id = 3;
		Float trueParentRightEdge = (float) 1024;
		
		// Exercise
		Float actualParentRightEdge = neuronDao.parentRightEdge(id);
		
		// Verify
		assertThat(actualParentRightEdge, is(trueParentRightEdge));
	}
}