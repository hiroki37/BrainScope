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

import jp.co.sbps.dao.NeuronDao;

/*
 * NeuronDaoが適切に動作しているかを確認するプログラム
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
		jdbc.update("INSERT INTO neuron VALUES (1, 'ニューロン１', 'コンテンツ１', 1, false, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (2, 'ニューロン２', 'コンテンツ２', 2, false, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (3, 'ニューロン３', 'コンテンツ３', 3, false, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (4, 'ニューロン４', 'コンテンツ４', 3, false, '2016-04-01', '2016-04-01')");
		
		// tree_diagramテーブルの初期化
		jdbc.update("INSERT INTO tree_diagram VALUES (1, 1)");
		jdbc.update("INSERT INTO tree_diagram VALUES (1, 2)");
		jdbc.update("INSERT INTO tree_diagram VALUES (1, 3)");
		jdbc.update("INSERT INTO tree_diagram VALUES (1, 4)");
		jdbc.update("INSERT INTO tree_diagram VALUES (2, 2)");
		jdbc.update("INSERT INTO tree_diagram VALUES (2, 3)");
		jdbc.update("INSERT INTO tree_diagram VALUES (2, 4)");
		jdbc.update("INSERT INTO tree_diagram VALUES (3, 3)");
		jdbc.update("INSERT INTO tree_diagram VALUES (4, 4)");
		
		// configテーブルの初期化
		jdbc.update("DELETE FROM config");
		jdbc.update("INSERT INTO config VALUES (1, 2)");
	}
	
	@Test
	public void returnNeuron_現在のスコープアドレスのニューロンとそれより１つ深いニューロンのすべてを返すことを確認する() {
		// SetUp
		String expected = "[{id=1, title=ニューロン１, content=コンテンツ１, neuron_level=1, active=false, create_date=2016-04-01 00:00:00.0, update_date=2016-04-01 00:00:00.0}, "
				+ "{id=2, title=ニューロン２, content=コンテンツ２, neuron_level=2, active=false, create_date=2016-04-01 00:00:00.0, update_date=2016-04-01 00:00:00.0}]";
		
		// Exercise
		String actual = String.valueOf(neuronDao.returnNeuron());
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void returnAllNeuron_すべてのニューロンを返すことを確かめる() {
		// SetUp
		String expected = "[{id=1, title=ニューロン１, content=コンテンツ１, neuron_level=1, active=false, create_date=2016-04-01 00:00:00.0, update_date=2016-04-01 00:00:00.0}, "
				+ "{id=2, title=ニューロン２, content=コンテンツ２, neuron_level=2, active=false, create_date=2016-04-01 00:00:00.0, update_date=2016-04-01 00:00:00.0}, "
				+ "{id=3, title=ニューロン３, content=コンテンツ３, neuron_level=3, active=false, create_date=2016-04-01 00:00:00.0, update_date=2016-04-01 00:00:00.0}, "
				+ "{id=4, title=ニューロン４, content=コンテンツ４, neuron_level=3, active=false, create_date=2016-04-01 00:00:00.0, update_date=2016-04-01 00:00:00.0}]";
		
		// Exercise
		String actual = String.valueOf(neuronDao.returnAllNeuron());
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void updateNeuron_ニューロンが更新されることを確認する() {
		// SetUp
		Integer id = 1;
		String title = "更新されたタイトル";
		String content = "更新されたコンテンツ";
		
		String expected = "[{id=" + id + ", title=" + title + ", content=" + content + "}]";
		
		// Exercise
		neuronDao.updateNeuron(id, title, content);
		String actual = String.valueOf(jdbc.queryForList("SELECT id, title, content FROM neuron WHERE id = ?",id));
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void generateNeuron_ニューロンが生成されることを確認する() {
		// SetUp
		Integer id = 1;
		String title = "新しいニューロン";
		String content = "";
		Integer neuronLevel = 2;
		
		String expected = "[{title=" + title + ", content=" + content + ", neuron_level=" + neuronLevel + "}]";
		
		// Exercise
		neuronDao.generateNeuron(id);
		String actual = String.valueOf(jdbc.queryForList("SELECT title, content, neuron_level FROM neuron WHERE id = ?", neuronDao.youngestId()));
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void extinctNeuron_ニューロンが削除されることを確認する() {
		// SetUp
		Integer id = 2;
		
		String expected = "[]";
		
		// Exercise
		neuronDao.extinctNeuron(id);
		String actual = String.valueOf(jdbc.queryForList("SELECT * FROM neuron "
				+ "WHERE id IN (SELECT descendant FROM tree_diagram WHERE ancestor = ?)", id));
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void insertNeuron_ニューロンが挿入されることを確認する() {
		// SetUp
		Integer id = 2;
		String title = "挿入されたニューロン";
		String content = "";
		Integer neuronLevel = 2;
		
		String expected = "[{title=" + title + ", content=" + content + ", neuron_level=" + neuronLevel + "}]";
		
		// Exercise
		neuronDao.insertNeuron(id);
		String actual = String.valueOf(jdbc.queryForList("SELECT title, content, neuron_level FROM neuron WHERE id = ?", neuronDao.youngestId()));
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void activateNeuron_ニューロンが活性化されることを確認する() {
		// SetUp
		Integer id = 1;
		
		Boolean expected = true;
		
		// Exercise
		neuronDao.activateNeuron(id);
		Boolean actual = jdbc.queryForObject("SELECT active FROM neuron WHERE id = ?", Boolean.class, id);
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void activateNeuron_ニューロンが非活性化されることを確認する() {
		// SetUp
		Integer id = 1;
		jdbc.update("UPDATE neuron SET active = true");
		
		Boolean expected = false;
		
		// Exercise
		neuronDao.activateNeuron(id);
		Boolean actual = jdbc.queryForObject("SELECT active FROM neuron WHERE id = ?", Boolean.class, id);
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void parentId_親ニューロンのidが出力されることを確認する() {
		// SetUp
		Integer id = 2;
		
		Integer expected = 1;
		
		// Exercise
		Integer actual = neuronDao.parentId(id);
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void yougestId_最も新しいニューロンのidが出力されることを確認する() {
		// SetUp
		Integer expected = 4;
		
		// Exercise
		Integer actual = neuronDao.youngestId();
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void insertNeuronLevel_ニューロンレベルが調整されることを確認する() {
		// SetUp
		Integer id = 2;
		Integer id2NeuronLevel = 3;
		Integer id3NeuronLevel = 4;
		Integer id4NeuronLevel = 4;
		
		String expected = "[{neuron_level=" + id2NeuronLevel + "}, {neuron_level=" + id3NeuronLevel + "}, {neuron_level=" + id4NeuronLevel + "}]";
		
		// Exercise
		neuronDao.insertNeuronLevel(id);
		String actual = String.valueOf(jdbc.queryForList("SELECT neuron_level FROM neuron WHERE id IN (SELECT descendant FROM tree_diagram WHERE ancestor = ?)", id));
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void neuronLevel_ニューロンレベルが出力されることを確認する() {
		// SetUp
		Integer id = 1;
		
		Integer expected = 1;
				
		// Exercise
		Integer actual = neuronDao.neuronLevel(id);
		
		// Verify
		assertThat(actual, is(expected));
	}
}