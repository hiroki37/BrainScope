package jp.co.sbps;

import static org.hamcrest.Matchers.is;
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
	public void display_現在のスコープアドレスのニューロンとその１つだけ深い子ニューロンを返すことを確認する() {
		// SetUp
		jdbc.update("UPDATE config SET scope_address=1");
		
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
		String actual = String.valueOf(jdbc.queryForList("SELECT * FROM neuron WHERE id = " + id));
		
		// Verify
		assertThat(actual, is("[{id=1, title=" + title + ", content=" + content + ", neuron_level=1, left_edge=1.0, right_edge=1024.0, create_date=2016-04-01, update_date=" + sdf.format(date) + "}]"));
	}
	
	@Test
	public void generate_ニューロンが生成されることを確認する() {
		// SetUp
		Integer id = 4;
		
		// Exercise
		neuronDao.generate(id);
		String actual = String.valueOf(jdbc.queryForList("SELECT title FROM neuron WHERE neuron_level = 4"));
		
		// Verify
		assertThat(actual, is("[{title=新しいニューロン}]"));
	}
	
	@Test
	public void delete_ニューロンが削除されることを確認する() {
		// SetUp
		Integer id = 5;
		
		// Exercise
		neuronDao.delete(id);
		String actual = String.valueOf(jdbc.queryForList("SELECT * FROM neuron WHERE id = " + id));
		
		// Verify
		assertThat(actual, is("[]"));
	}
	
	// @Test
	public void insert_ニューロンが挿入されることを確認する() {
		// SetUp
		
		// Exercise
		
		// Verify

	}
	
	// @Test
	public void generateLeft() {
		// SetUp
		
		// Exercise
		
		// Verify

	}
	
	// @Test
	public void generateRight() {
		// SetUp
		
		// Exercise
		
		// Verify

	}
	
	// @Test
	public void insertLeft() {
		// SetUp
		
		// Exercise
		
		// Verify

	}
	
	// @Test
	public void insertRihgt() {
		// SetUp
		
		// Exercise
		
		// Verify

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
	public void maxRightEdge() {
		// SetUp
		
		// Exercise
		
		// Verify

	}
	
	// @Test
	public void minLeftEdge() {
		// SetUp
		
		// Exercise
		
		// Verify

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