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
		String title = "新しいニューロン";
		Integer neuronLevel = 2;
		Float leftEdge = (float) 1008;
		Float rightEdge = (float) 1016;
		
		String expected = "[{title=" + title + ", neuron_level=" + neuronLevel + ", left_edge=" + leftEdge + ", right_edge=" + rightEdge + "}]";
		
		// Exercise
		neuronDao.generate(id);
		String actual = String.valueOf(jdbc.queryForList("SELECT title, neuron_level, left_edge, right_edge FROM neuron WHERE title = ?", title));
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void extinct_ニューロンが削除されることを確認する() {
		// SetUp
		Integer id = 2;
		Float leftEdge = (float) 201;
		Float rightEdge = (float) 624;
		
		String expected = "[]";
		
		// Exercise
		neuronDao.extinct(id);
		String actual = String.valueOf(jdbc.queryForList("SELECT * FROM neuron WHERE left_edge BETWEEN ? AND ?", leftEdge, rightEdge));
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void insert_ニューロンが挿入されることを確認する() {
		// 挿入されたニューロンが正しいことの確認
			// SetUp
			Integer id = 2;
			String title = "挿入されたニューロン";
			Integer neuronLevel = 2;
			Float leftEdge = (float) 101;
			Float rightEdge = (float) 724;
			
			String expected = "[{title=" + title + ", neuron_level=" + neuronLevel + ", left_edge=" + leftEdge + ", right_edge=" + rightEdge + "}]";
			
			// Exercise
			neuronDao.insert(id);
			String actual = String.valueOf(jdbc.queryForList("SELECT title, neuron_level, left_edge, right_edge FROM neuron WHERE title = ?", title));
			
			// Verify
			assertThat(actual, is(expected));
		
		// 挿入されたニューロンの子ニューロンが１つずつ深くなったことの確認
			// SetUp
			Integer id2NeuronLevel = 3;
			Integer id4NeuronLevel = 4;
			Integer id5NeuronLevel = 4;
			
			expected = "[{neuron_level=" + id2NeuronLevel + "}, {neuron_level=" + id4NeuronLevel + "}, {neuron_level=" + id5NeuronLevel + "}]";
			
			// Exercise
			actual = String.valueOf(jdbc.queryForList("SELECT neuron_level FROM neuron WHERE (left_edge > ?) AND (right_edge < ?)", leftEdge, rightEdge));
			
			// Verify
			assertThat(actual, is(expected));
	}
	
	@Test
	public void generateLeft_生成する際の左端座標が正しいことを確認する() {
		// 親ニューロンの左端と生成するニューロンの左端の間にニューロンが存在する場合
			// SetUp
			Integer id = 1;
			
			Float expected =  (float) 1008;
			
			// Exercise
			Float actual = neuronDao.generateLeft(id);
			
			// Verify
			assertThat(actual, is(expected));
			
		// 親ニューロンの左端と生成するニューロンの左端の間にニューロンが存在しない場合
			// SetUp
			neuronDao.extinct(2);
			neuronDao.extinct(3);
			
			expected =  (float) 342;
			
			// Exercise
			actual = neuronDao.generateLeft(id);
			
			// Verify
			assertThat(actual, is(expected));
	}
	
	@Test
	public void generateRight_生成する際の右端座標が正しいことを確認する() {
		// SetUp
		Integer id = 1;
		
		Float expected =  (float) 1016;
		
		// Exercise
		Float actual = neuronDao.generateRight(id);
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void insertLeft_挿入する際の左端座標が正しいことを確認する１() {
		// 親ニューロンの左端と挿入するニューロンの左端の間にニューロンが存在する場合
			// SetUp
			Integer id = 3;
			
			Float expected = (float) 724;
			
			// Exercise
			Float actual = neuronDao.insertLeft(id); 
			
			// Verify
			assertThat(actual, is(expected));
	}
	
	@Test
	public void insertLeft_挿入する際の左端座標が正しいことを確認する２() {
		// 親ニューロンの左端と挿入するニューロンの左端の間にニューロンが存在しない場合
		// SetUp
		Integer id = 2;
		
		Float expected = (float) 101;
		
		// Exercise
		Float actual = neuronDao.insertLeft(id); 
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void insertRihgt_挿入する際の右端座標が正しいことを確認する１() {
		// 親ニューロンの右端と挿入するニューロンの右端の間にニューロンが存在する場合
			// SetUp
			Integer id = 2;
			
			Float expected = (float) 724;
			
			// Exercise
			Float actual = neuronDao.insertRight(id); 
			
			// Verify
			assertThat(actual, is(expected));
	}
	
	@Test
	public void insertRihgt_挿入する際の右端座標が正しいことを確認する２() {
		// 親ニューロンの右端と挿入するニューロンの右端の間にニューロンが存在しない場合
			// SetUp
			Integer id = 3;
			
			Float expected = (float) 1012;
			
			// Exercise
			Float actual = neuronDao.insertRight(id); 
			
			// Verify
			assertThat(actual, is(expected));
	}
	
	@Test
	public void title_適切なタイトルが出力されることを確認する() {
		// SetUp
		Integer id = 1;
		
		String expected = "ニューロン１";
		
		// Exercise
		String actual = neuronDao.title(id);
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void content_適切なコンテンツが出力されることを確認する() {
		// SetUp
		Integer id = 1;
		
		String expected = "コンテンツ１";
		
		// Exercise
		String actual = neuronDao.content(id);
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void neuronLevel_適切なニューロンの深さが出力されることを確認する() {
		// SetUp
		Integer id = 1;
		
		Integer expected = 1;
				
		// Exercise
		Integer actual = neuronDao.neuronLevel(id);
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void leftEdge_適切な左端座標が出力されることを確認する() {
		// SetUp
		Integer id = 1;
		
		Float expected = (float) 1;
		
		// Exercise
		Float actual = neuronDao.leftEdge(id);
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void rightEdge_適切な右端座標が出力されることを確認する() {
		// SetUp
		Integer id = 1;
		
		Float expected = (float) 1024;
		
		// Exercise
		Float actual = neuronDao.rightEdge(id);
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void greatest_２つの値の最大値が出力されることを確認する() {
		// SetUp
		Float var1 = (float) 1992, var2 = (float) 2016;
		
		// Exercise
		Float actual = neuronDao.greatest(var1, var2);
		
		// Verify
		assertThat(actual, is(var2));
	}
	
	@Test
	public void minLeftEdge() {
		// 対象ニューロンの右側にニューロンがない場合
			// SetUp
			Integer id = 3;
			Float expected = null;
			Float RightEdge = jdbc.queryForObject("SELECT right_edge FROM neuron WHERE id = ?", Float.class, id); 
			
			// Exercise
			Float actual = neuronDao.minLeftEdge(RightEdge);
			
			// Verify
			assertThat(actual, is(expected));
			
		// 対象ニューロンの右側にニューロンがある場合
			// SetUp
			id = 2;
			expected = (float) 824;
			RightEdge = jdbc.queryForObject("SELECT right_edge FROM neuron WHERE id = ?", Float.class, id); 
			
			// Exercise
			actual = neuronDao.minLeftEdge(RightEdge);
			
			// Verify
			assertThat(actual, is(expected));
	}
	
	@Test
	public void maxRightEdge() {
		// 対象ニューロンの左側にニューロンがない場合
			// SetUp
			Integer id = 2;
			Float expected = null;
			Float LeftEdge = jdbc.queryForObject("SELECT left_edge FROM neuron WHERE id = ?", Float.class, id); 
			
			// Exercise
			Float actual = neuronDao.maxRightEdge(LeftEdge);
			
			// Verify
			assertThat(actual, is(expected));
		
		// 対象ニューロンの左側にニューロンがある場合
			// SetUp
			id = 3;
			expected = (float) 624;
			LeftEdge = jdbc.queryForObject("SELECT left_edge FROM neuron WHERE id = ?", Float.class, id); 
			
			// Exercise
			actual = neuronDao.maxRightEdge(LeftEdge);
			
			// Verify
			assertThat(actual, is(expected));
	}
	
	@Test
	public void parentLeftEdge_親ニューロンの左端座標を出力する() {
		// SetUp
		Integer id = 3;
		
		Float expected = (float) 1;
		
		// Exercise
		Float actual = neuronDao.parentLeftEdge(id);
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void parentRightEdge_親ニューロンの右端座標を出力する() {
		// SetUp
		Integer id = 3;
		
		Float expected = (float) 1024;
		
		// Exercise
		Float actual = neuronDao.parentRightEdge(id);
		
		// Verify
		assertThat(actual, is(expected));
	}
}