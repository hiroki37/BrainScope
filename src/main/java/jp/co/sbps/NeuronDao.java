package jp.co.sbps;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/*
 * neuronテーブルへのアクセスをするDaoクラス
*/

@Component
public class NeuronDao {

	@Autowired
	private JdbcTemplate jdbc;

	@Autowired
	ConfigDao configDao;

	// 木構造を返す
	public List<Map<String, Object>> display() {
		return jdbc.queryForList(
				"SELECT * FROM neuron WHERE id = ? "
				+ "OR id IN (SELECT id FROM neuron WHERE neuron_level = ? + 1 AND left_edge BETWEEN ? AND ? ) ORDER BY left_edge ASC;",
				configDao.scopeAddress(), neuronLevel(configDao.scopeAddress()),
				leftEdge(configDao.scopeAddress()), rightEdge(configDao.scopeAddress()));
	}

	// ニューロンの更新
	public void update(Integer id, String title, String content) {
		jdbc.update("UPDATE neuron SET title = ?, content = ?, update_date = current_timestamp WHERE id = ?", title, content, id);
	}

	// ニューロンの生成
	public void generate(Integer id) {
		// 生成時の初期タイトル・コンテンツ
		String newTitle = "新しいニューロン";
		String newContent = "";

		jdbc.update("INSERT INTO neuron (title, content, neuron_level, left_edge, right_edge, create_date, update_date)"
				+ "VALUES("
				+ "?,"
				+ "?,"
				+ "? + 1,"
				+ "?,"
				+ "?,"
				+ "current_timestamp,"
				+ "current_timestamp)",
				newTitle, newContent, neuronLevel(id), generateLeft(id), generateRight(id));
	}

	// ニューロンの削除
	public void delete(Integer id) {
		jdbc.update("DELETE FROM neuron WHERE id = ? OR left_edge " + "BETWEEN ? AND ?", id, leftEdge(id),
				rightEdge(id));
	}

	// ニューロンの挿入（親ニューロンと子ニューロンの間）
	public void insert(Integer id) {
		// 生成時の初期タイトル・コンテンツ
		String newTitle = "挿入されたニューロン";
		String newContent = "";
		
		// 新しいニューロンを挿入
		jdbc.update("INSERT INTO neuron (title, content, neuron_level, left_edge, right_edge, create_date, update_date)"
				+ "VALUES("
				+ "?,"
				+ "?,"
				+ "?,"
				+ "?,"
				+ "?,"
				+ "current_timestamp,"
				+ "current_timestamp)",
				newTitle, newContent, neuronLevel(id), insertLeft(id), insertRight(id));
		
		// 自身と子ニューロンを１つ深くする
		jdbc.update("UPDATE neuron SET neuron_level = neuron_level + 1 WHERE left_edge BETWEEN ? AND ?",
				leftEdge(id), rightEdge(id));
	}

	// ＝＝＝＝＝ＳＱＬパーツ＝＝＝＝＝

	// 生成する際の、左端座標
	public Float generateLeft(Integer id) {
		Float maxRightEdge = jdbc.queryForObject("SELECT MAX(right_edge) FROM neuron WHERE right_edge < ?",
				Float.class, rightEdge(id));

		return  greatest(leftEdge(id), maxRightEdge) + (rightEdge(id) - greatest(leftEdge(id), maxRightEdge)) / 3;
	}

	// 生成する際の、右端座標
	public Float generateRight(Integer id) {
		Float maxRightEdge = jdbc.queryForObject("SELECT MAX(right_edge) FROM neuron WHERE right_edge < ?",
				Float.class, rightEdge(id));
		
		return greatest(leftEdge(id), maxRightEdge) + (rightEdge(id) - greatest(leftEdge(id), maxRightEdge)) * 2 / 3;
	}
	
	// 挿入する際の、左端座標
	public Float insertLeft(Integer id) {
		Float maxRightEdge = jdbc.queryForObject("SELECT GREATEST(COALESCE(?, 0), COALESCE(?, 0))",
				Float.class, maxRightEdge(leftEdge(id)), parentLeftEdge(id));
		
		return maxRightEdge + (leftEdge(id) - maxRightEdge) / 2;
	}
	
	// 挿入する際の、右端座標
	public Float insertRight(Integer id) {
		Float maxLeftEdge = jdbc.queryForObject("SELECT LEAST(COALESCE(?, 1025), COALESCE(?, 1025))",
				Float.class, minLeftEdge(rightEdge(id)), parentRightEdge(id));
		
		return rightEdge(id) + (maxLeftEdge - rightEdge(id)) / 2;
	}
	
	// ニューロンのタイトル
	public String title(Integer id) {
		return jdbc.queryForObject("SELECT title FROM neuron WHERE id = ?", String.class, id);
	}

	// ニューロンのコンテンツ
	public String content(Integer id) { 
		return jdbc.queryForObject("SELECT content FROM neuron WHERE id = ?", String.class, id);
	}

	// ニューロンの深さ
	public Integer neuronLevel(Integer id) {
		return jdbc.queryForObject("SELECT neuron_level FROM neuron WHERE id = ?", Integer.class, id);
	}

	// ニューロンの左端座標
	public Float leftEdge(Integer id) {
		return jdbc.queryForObject("SELECT left_edge FROM neuron WHERE id = ?", Float.class, id);
	}

	// ニューロンの右端座標
	public Float rightEdge(Integer id) {
		return jdbc.queryForObject("SELECT right_edge FROM neuron WHERE id = ?", Float.class, id);
	}

	// ２つの値の最大値
	public Float greatest(Float var1, Float var2) {
		return jdbc.queryForObject("SELECT GREATEST(COALESCE(?, 0), COALESCE(?, 0))", Float.class, var1, var2);
	}

	// 右端座標以上で最少の左端座標
	public Float minLeftEdge(Float rightEdge) {
		return jdbc.queryForObject("SELECT MIN(left_edge) FROM neuron WHERE left_edge > ?", Float.class, rightEdge);
	}
	
	// 左端座標以下で最大の右端座標
	public Float maxRightEdge(Float leftEdge) {
		return jdbc.queryForObject("SELECT MAX(right_edge) FROM neuron WHERE right_edge < ?", Float.class, leftEdge);
	}

	// 親の左端座標
	public Float parentLeftEdge(Integer id) {
		return jdbc.queryForObject("SELECT MAX(left_edge) FROM neuron WHERE left_edge < ? AND neuron_level = ? - 1",
				Float.class, leftEdge(id), neuronLevel(id));
	}

	// 親の右端座標
	public Float parentRightEdge(Integer id) {
		return jdbc.queryForObject("SELECT MIN(right_edge) FROM neuron WHERE right_edge > ? AND neuron_level = ? - 1",
				Float.class, rightEdge(id), neuronLevel(id));
	}
}
