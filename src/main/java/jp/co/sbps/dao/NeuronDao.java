package jp.co.sbps.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jp.co.sbps.dao.entity.Neuron;

/*
 * neuronテーブルへのアクセスをするDaoクラス
*/

// ※※※このDaoでtree_diagramを呼んでいる問題アリ※※※

@Component
public class NeuronDao {
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@Autowired
	ConfigDao configDao;
	
	@Autowired
	TreeDiagramDao treeDiagramDao;
	
	// ニューロンを返す
	public Neuron returnNeuron(Integer scopeAddress) {
		return jdbc.query("SELECT * FROM neuron WHERE id = ?",
				new BeanPropertyRowMapper<>(Neuron.class), scopeAddress).get(0);
	}
	
	// ニューロンのリストを返す
	public List<Neuron> returnNeuronList() {
		Integer scopeAddress = configDao.returnConfig().getScopeAddress();
		
		return jdbc.query("SELECT * FROM neuron "
				+ "WHERE id IN (SELECT descendant FROM tree_diagram WHERE ancestor = ?) "
				+ "AND (neuron_level BETWEEN ? AND ? + 1) ORDER BY neuron_level ASC, create_date ASC",
				new BeanPropertyRowMapper<>(Neuron.class),
				scopeAddress, returnNeuron(scopeAddress).getNeuronLevel(), returnNeuron(scopeAddress).getNeuronLevel());
	}
	
	// すべてのニューロンのリストを返す
	public List<Neuron> returnAllNeuronList() {
		return jdbc.query("SELECT * FROM neuron ORDER BY neuron_level ASC, create_date ASC",
				new BeanPropertyRowMapper<>(Neuron.class));
	}
	
	// ニューロンの更新
	public void updateNeuron(Neuron neuron) {
		jdbc.update("UPDATE neuron SET title = ?, content = ?, update_date = current_timestamp WHERE id = ?",
				neuron.getTitle(), neuron.getContent(), neuron.getId());
	}
	
	// ニューロンの生成
	public void generateNeuron(Neuron neuron) {
		// 生成時の初期タイトル・コンテンツ
		String newTitle = "新しいニューロン";
		String newContent = "";
		
		// ニューロンの生成
		jdbc.update("INSERT INTO neuron (title, content, neuron_level, active, create_date, update_date) "
				+ "VALUES("
				+ "?,"
				+ "?,"
				+ "? + 1,"
				+ "false,"
				+ "current_timestamp,"
				+ "current_timestamp)",
				newTitle, newContent, neuron.getNeuronLevel());
	}
	
	// ニューロンの削除
	public void extinctNeuron(Neuron neuron) {
		jdbc.update("DELETE FROM neuron WHERE id IN (SELECT descendant FROM tree_diagram WHERE ancestor = ?)", neuron.getId());
	}
	
	// ニューロンの挿入
	public void insertNeuron(Neuron neuron) {
		// 挿入時の初期タイトル・コンテンツ
		String newTitle = "挿入されたニューロン";
		String newContent = "";
		
		// ニューロンの挿入
		jdbc.update("INSERT INTO neuron (title, content, neuron_level, active, create_date, update_date)"
				+ "VALUES("
				+ "?,"
				+ "?,"
				+ "?,"
				+ "false,"
				+ "current_timestamp,"
				+ "current_timestamp)",
				newTitle, newContent, neuron.getNeuronLevel());
	}
	
	// ニューロンの活性化/非活性化
	public void activateNeuron(Neuron neuron) {
		if (jdbc.queryForObject("SELECT active FROM neuron WHERE id = ?", Boolean.class, neuron.getId())) {
			jdbc.update("UPDATE neuron SET active = false WHERE id = ?", neuron.getId());
		} else {
			jdbc.update("UPDATE neuron SET active = true WHERE id = ?", neuron.getId());
		}
	}
	
	// 親ニューロンを返す
	public Neuron parentNeuron(Neuron neuron) {
		return jdbc.query("SELECT * FROM neuron "
				+ "WHERE id IN (SELECT ancestor FROM tree_diagram WHERE descendant = ?) "
				+ "AND neuron_level = (?-1)",
				new BeanPropertyRowMapper<>(Neuron.class), neuron.getId(), neuron.getNeuronLevel()).get(0);
	}
	
	// 最も若いニューロンを返す
	public Neuron youngestNeuron() {
		return jdbc.query("SELECT * FROM neuron WHERE id = (SELECT MAX(id) FROM neuron)", new BeanPropertyRowMapper<>(Neuron.class)).get(0);
	}
	
	// ニューロンレベルの調整
	public void insertNeuronLevel(Neuron neuron) {
		jdbc.update("UPDATE neuron SET neuron_level = neuron_level + 1 "
				+ "WHERE id IN (SELECT descendant FROM tree_diagram WHERE ancestor = ?)", neuron.getId());
	}
}
