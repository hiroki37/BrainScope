package jp.co.sbps.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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
	
	// ニューロンのリストを返す
	public List<Map<String, Object>> returnNeuron() {
		return jdbc.queryForList("SELECT * FROM neuron "
				+ "WHERE id IN (SELECT descendant FROM tree_diagram WHERE ancestor = ?) "
				+ "AND (neuron_level BETWEEN ? AND ? + 1) ORDER BY neuron_level ASC, create_date ASC",
				configDao.scopeAddress(), neuronLevel(configDao.scopeAddress()), neuronLevel(configDao.scopeAddress()));
	}
	
	// すべてのニューロンのリストを返す
	public List<Map<String, Object>> returnAllNeuron() {
		return jdbc.queryForList("SELECT * FROM neuron ORDER BY neuron_level ASC, create_date ASC");
	}
	
	// ニューロンの更新
	public void updateNeuron(Integer id, String title, String content) {
		jdbc.update("UPDATE neuron SET title = ?, content = ?, update_date = current_timestamp WHERE id = ?", title, content, id);
	}
	
	// ニューロンの生成
	public void generateNeuron(Integer id) {
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
				newTitle, newContent, neuronLevel(id));
	}
	
	// ニューロンの削除
	public void extinctNeuron(Integer id) {
		jdbc.update("DELETE FROM neuron WHERE id IN (SELECT descendant FROM tree_diagram WHERE ancestor = ?)", id);
	}
	
	// ニューロンの挿入
	public void insertNeuron(Integer id) {
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
				newTitle, newContent, neuronLevel(id));
	}
	
	// ニューロンの活性化
	public void activateNeuron(Integer id) {
		if (jdbc.queryForObject("SELECT active FROM neuron WHERE id = ?", Boolean.class, id)) {
			jdbc.update("UPDATE neuron SET active = false WHERE id = ?", id);
		} else {
			jdbc.update("UPDATE neuron SET active = true WHERE id = ?", id);
		}
	}
	
	// 親ニューロンのidを返す
	public Integer parentId(Integer id) {
		return jdbc.queryForObject("SELECT id FROM neuron "
				+ "WHERE id IN (SELECT ancestor FROM tree_diagram WHERE descendant = ?) "
				+ "AND neuron_level = (?-1)", Integer.class, id, neuronLevel(id));
	}
	
	// 最も若いニューロンのidを返す
	public Integer youngestId() {
		return jdbc.queryForObject("SELECT MAX(id) FROM neuron", Integer.class);
	}
	
	// ニューロンレベルの調整
	public void insertNeuronLevel(Integer id) {
		jdbc.update("UPDATE neuron SET neuron_level = neuron_level + 1 WHERE id IN (SELECT descendant FROM tree_diagram WHERE ancestor = ?)", id);
	}
	
	// ニューロンレベルを返す
	public Integer neuronLevel(Integer id) {
		return jdbc.queryForObject("SELECT neuron_level FROM neuron WHERE id = ?", Integer.class, id);
	}
}
