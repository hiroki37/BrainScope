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
				configDao.scope_address(), neuron_level(configDao.scope_address()),
				left_edge(configDao.scope_address()), right_edge(configDao.scope_address()));
	}

	// ニューロンの更新
	public void update(Integer id, String title, String content) {
		jdbc.update("UPDATE neuron SET title = ?, content = ?, update_date = current_timestamp WHERE id = ?", title, content, id);
	}

	// ニューロンの生成
	public void generate(Integer id) {
		// 生成時の初期タイトル・コンテンツ
		String new_title = "新しいニューロン";
		String new_content = "";

		jdbc.update("INSERT INTO neuron (title, content, neuron_level, left_edge, right_edge, create_date, update_date)"
				+ "VALUES("
				+ "?,"
				+ "?,"
				+ "? + 1,"
				+ "?,"
				+ "?,"
				+ "current_timestamp,"
				+ "current_timestamp)",
				new_title, new_content, neuron_level(id), generate_left(id), generate_right(id));
	}

	// ニューロンの削除
	public void delete(Integer id) {
		jdbc.update("DELETE FROM neuron WHERE id = ? OR left_edge " + "BETWEEN ? AND ?", id, left_edge(id),
				right_edge(id));
	}

	// ニューロンの挿入（親ニューロンと子ニューロンの間）
	public void insert(Integer id) {
		// 生成時の初期タイトル・コンテンツ
		String new_title = "挿入されたニューロン";
		String new_content = "";
		
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
				new_title, new_content, neuron_level(id), insert_left(id), insert_right(id));
		
		// 自身と子ニューロンを１つ深くする
		jdbc.update("UPDATE neuron SET neuron_level = neuron_level + 1 WHERE left_edge BETWEEN ? AND ?",
				left_edge(id), right_edge(id));
	}

	// ＝＝＝＝＝ＳＱＬパーツ＝＝＝＝＝

	// 生成する際の、left_edge座標
	public Float generate_left(Integer id) {
		Float max_right_edge = jdbc.queryForObject("SELECT MAX(right_edge) FROM neuron WHERE right_edge < ?",
				Float.class, right_edge(id));

		return  greatest(left_edge(id), max_right_edge) + (right_edge(id) - greatest(left_edge(id), max_right_edge)) / 3;
	}

	// 生成する際の、right_edge座標
	public Float generate_right(Integer id) {
		Float max_right_edge = jdbc.queryForObject("SELECT MAX(right_edge) FROM neuron WHERE right_edge < ?",
				Float.class, right_edge(id));
		
		return greatest(left_edge(id), max_right_edge) + (right_edge(id) - greatest(left_edge(id), max_right_edge)) * 2 / 3;
	}
	
	// 挿入する際の、left_edge座標
	public Float insert_left(Integer id) {
		Float max_right_edge = jdbc.queryForObject("SELECT GREATEST(?, ?)",
				Float.class, max_right_edge(left_edge(id)), parent_left_edge(id));
		
		return max_right_edge + (left_edge(id) - max_right_edge) / 2;
	}
	
	// 挿入する際の、right_edge座標
	public Float insert_right(Integer id) {
		Float max_left_edge = jdbc.queryForObject("SELECT LEAST(?, ?)",
				Float.class, min_left_edge(right_edge(id)), parent_right_edge(id));
		
		return right_edge(id) + (max_left_edge - right_edge(id)) / 2;
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
	public Integer neuron_level(Integer id) {
		return jdbc.queryForObject("SELECT neuron_level FROM neuron WHERE id = ?", Integer.class, id);
	}

	// ニューロンの左端
	public Float left_edge(Integer id) {
		return jdbc.queryForObject("SELECT left_edge FROM neuron WHERE id = ?", Float.class, id);
	}

	// ニューロンの右端
	public Float right_edge(Integer id) {
		return jdbc.queryForObject("SELECT right_edge FROM neuron WHERE id = ?", Float.class, id);
	}

	// ２つの値の最大値
	public Float greatest(Float var1, Float var2) {
		return jdbc.queryForObject("SELECT GREATEST(?, ?)", Float.class, var1, var2);
	}
	
	// right_edge以上で最少のleft_edge
	public Float min_left_edge(Float right_edge) {
		return jdbc.queryForObject("SELECT MIN(left_edge) FROM neuron WHERE left_edge > ?", Float.class, right_edge);
	}
	
	// left_edge以下で最大のright_edge
	public Float max_right_edge(Float left_edge) {
		return jdbc.queryForObject("SELECT MAX(right_edge) FROM neuron WHERE right_edge < ?", Float.class, left_edge);
	}

	// 親のleft_edge
	public Float parent_left_edge(Integer id) {
		return jdbc.queryForObject("SELECT MAX(left_edge) FROM neuron WHERE left_edge < ? AND neuron_level = ? - 1",
				Float.class, left_edge(id), neuron_level(id));
	}

	// 親のright_edge
	public Float parent_right_edge(Integer id) {
		return jdbc.queryForObject("SELECT MIN(right_edge) FROM neuron WHERE right_edge > ? AND neuron_level = ? - 1",
				Float.class, right_edge(id), neuron_level(id));
	}
}
