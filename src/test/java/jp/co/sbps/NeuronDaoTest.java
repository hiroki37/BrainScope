/*
package jp.co.sbps;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import jp.co.sbps.dao.NeuronDao;
import jp.co.sbps.dao.entity.Neuron;
*/

/*
 * NeuronDaoが適切に動作しているかを確認するプログラム
 */

/*
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
	public void returnNeuronList_現在のスコープアドレスのニューロンとそれより１つ深いニューロンのすべてを返すことを確認する() {
		// SetUp
		Integer id1 = 1, id2 = 2;
		String title1 = "ニューロン１", title2 = "ニューロン２";
		String content1 = "コンテンツ１", content2 = "コンテンツ２";
		Integer neuronLevel1 = 1, neuronLevel2 = 2;
		Boolean active = false;
		// Date createDate = "2016-04-01 00:00:00.0";
		// Date updateDate = "2016-04-01 00:00:00.0";
		
		// Exercise
		List<Neuron> actual = neuronDao.returnNeuronList();
		
		// Verify
		assertThat(actual.get(0).getId(), is(id1));
		assertThat(actual.get(0).getTitle(), is(title1));
		assertThat(actual.get(1).getTitle(), is(title2));
		assertThat(actual.get(0).getContent(), is(content1));
		assertThat(actual.get(1).getContent(), is(content2));
		assertThat(actual.get(0).getNeuronLevel(), is(neuronLevel1));
		assertThat(actual.get(1).getNeuronLevel(), is(neuronLevel2));
		
		assertThat(actual.get(1).getId(), is(id2));
		
		assertThat(actual.get(0).getActive(), is(active));
		// assertThat(actual.get(0).getCreateDate(), is(createDate));
		// assertThat(actual.get(0).getUpdateDate(), is(updateDate));
	}
	
	@Test
	public void returnAllNeuron_すべてのニューロンを返すことを確かめる() {
		// SetUp
		Integer id1 = 1, id2 = 2, id3 = 3, id4 = 4;
		String title1 = "ニューロン１", title2 = "ニューロン２", title3 = "ニューロン３", title4 = "ニューロン４";
		String content1 = "コンテンツ１", content2 = "コンテンツ２", content3 = "コンテンツ３", content4 = "コンテンツ４";
		Integer neuronLevel1 = 1, neuronLevel2 = 2, neuronLevel3 = 3, neuronLevel4 = 3;
		Boolean active = false;
		// Date createDate = "2016-04-01 00:00:00.0";
		// Date updateDate = "2016-04-01 00:00:00.0";
		
		// Exercise
		List<Neuron> actual = neuronDao.returnAllNeuronList();
		
		// Verify
		assertThat(actual.get(0).getId(), is(id1));
		assertThat(actual.get(1).getId(), is(id2));
		assertThat(actual.get(2).getId(), is(id3));
		assertThat(actual.get(3).getId(), is(id4));
		assertThat(actual.get(0).getTitle(), is(title1));
		assertThat(actual.get(1).getTitle(), is(title2));
		assertThat(actual.get(2).getTitle(), is(title3));
		assertThat(actual.get(3).getTitle(), is(title4));
		assertThat(actual.get(0).getContent(), is(content1));
		assertThat(actual.get(1).getContent(), is(content2));
		assertThat(actual.get(2).getContent(), is(content3));
		assertThat(actual.get(3).getContent(), is(content4));
		assertThat(actual.get(0).getNeuronLevel(), is(neuronLevel1));
		assertThat(actual.get(1).getNeuronLevel(), is(neuronLevel2));
		assertThat(actual.get(2).getNeuronLevel(), is(neuronLevel3));
		assertThat(actual.get(3).getNeuronLevel(), is(neuronLevel4));
		assertThat(actual.get(0).getActive(), is(active));
		// assertThat(actual.get(0).getCreateDate(), is(createDate));
		// assertThat(actual.get(0).getUpdateDate(), is(updateDate));
	}
	
	// @Test
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
	
	// @Test
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
	
	// @Test
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
	
	// @Test
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
	
	// @Test
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
	
	// @Test
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
	
	// @Test
	public void parentId_親ニューロンのidが出力されることを確認する() {
		// SetUp
		Integer id = 2;
		
		Integer expected = 1;
		
		// Exercise
		Integer actual = neuronDao.parentId(id);
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	// @Test
	public void yougestId_最も新しいニューロンのidが出力されることを確認する() {
		// SetUp
		Integer expected = 4;
		
		// Exercise
		Integer actual = neuronDao.youngestId();
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	// @Test
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
	
	// @Test
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
*/