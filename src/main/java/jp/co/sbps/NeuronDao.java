package jp.co.sbps;

import java.util.HashMap;
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
		List<Map<String, Object>> neurons;

		neurons = jdbc.queryForList(
				"SELECT * FROM neuron WHERE id = ? "
				+ "OR id IN (SELECT id FROM neuron WHERE neuron_level = ? + 1 AND left_edge BETWEEN ? AND ? ) ORDER BY left_edge ASC;",
				configDao.scope_address(), neuron_level(configDao.scope_address()),
				left_edge(configDao.scope_address()), right_edge(configDao.scope_address()));

		@SuppressWarnings("serial")
		final Map<String, Object> nullMap = new HashMap<String, Object>() {
			{
				put("id", "");
				put("title", "");
				put("content", "");
				put("neuron_level", "");
				put("left_edge", "");
				put("right_edge", "");
				put("create_date", "");
				put("update_date", "");
			}
		};

		while (neurons.size() < 3) {
			neurons.add(nullMap);
		}

		return neurons;
	}

	// ニューロンの更新
	public void update(String id, String title, String content) {
		jdbc.update("UPDATE neuron SET title = ?, content = ?, update_date = current_timestamp WHERE id = ?", title, content, id);
	}

	// ニューロンの生成
	public void generate(String id) {
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
	public void delete(String id) {
		jdbc.update("DELETE FROM neuron WHERE id = ? OR left_edge " + "BETWEEN ? AND ?", id, left_edge(id),
				right_edge(id));
	}

	// ニューロンの挿入（親ニューロンと子ニューロンの間）
	public void insert(String id) {
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
	public String generate_left(String id) {
		String max_right_edge = jdbc.queryForObject("SELECT MAX(right_edge) FROM neuron WHERE right_edge < ?",
				String.class, right_edge(id));

		float f = Float.parseFloat(greatest(left_edge(id), max_right_edge))
				+ (Float.parseFloat(right_edge(id)) - Float.parseFloat(greatest(left_edge(id), max_right_edge))) / 3;
		String result = String.valueOf(f);

		return result;
	}

	// 生成する際の、right_edge座標
	public String generate_right(String id) {
		String max_right_edge = jdbc.queryForObject("SELECT MAX(right_edge) FROM neuron WHERE right_edge < ?",
				String.class, right_edge(id));

		float f = Float.parseFloat(greatest(left_edge(id), max_right_edge))
				+ (Float.parseFloat(right_edge(id)) - Float.parseFloat(greatest(left_edge(id), max_right_edge))) * 2 / 3;
		String result = String.valueOf(f);

		return result;
	}
	
	// 挿入する際の、left_edge座標
	public String insert_left(String id) {
		String max_right_edge = jdbc.queryForObject("SELECT GREATEST(?, ?)",
				String.class, max_right_edge(left_edge(id)), parent_left_edge(id));
		
		float f = Float.parseFloat(max_right_edge) + (Float.parseFloat(left_edge(id)) - Float.parseFloat(max_right_edge)) / 2;
		
		String result = String.valueOf(f);
		
		return result;
	}
	
	// 挿入する際の、right_edge座標
	public String insert_right(String id) {
		String max_left_edge = jdbc.queryForObject("SELECT LEAST(?, ?)",
				String.class, min_left_edge(right_edge(id)), parent_right_edge(id));
		
		float f = Float.parseFloat(right_edge(id)) + (Float.parseFloat(max_left_edge) - Float.parseFloat(right_edge(id))) / 2;
		
		String result = String.valueOf(f);
		
		return result;
	}
	
	// ニューロンのタイトル
	public String title(String id) {
		String title = jdbc.queryForObject("SELECT title FROM neuron WHERE id = ?", String.class, id);
		return title;
	}

	// ニューロンのコンテンツ
	public String content(String id) {
		String content = jdbc.queryForObject("SELECT content FROM neuron WHERE id = ?", String.class, id);
		return content;
	}

	// ニューロンの深さ
	public String neuron_level(String id) {
		String neuron_level = jdbc.queryForObject("SELECT neuron_level FROM neuron WHERE id = ?", String.class, id);
		return neuron_level;
	}

	// ニューロンの左端
	public String left_edge(String id) {
		String left_edge = jdbc.queryForObject("SELECT left_edge FROM neuron WHERE id = ?", String.class, id);
		return left_edge;
	}

	// ニューロンの右端
	public String right_edge(String id) {
		String right_edge = jdbc.queryForObject("SELECT right_edge FROM neuron WHERE id = ?", String.class, id);
		return right_edge;
	}

	// ２つの値の最大値
	public String greatest(String var1, String var2) {
		String greatest = jdbc.queryForObject("SELECT GREATEST(?, ?)", String.class, var1, var2);
		return greatest;
	}

	// left_edge以下で最大のright_edge
	public String max_right_edge(String left_edge) {
		String max_right_edge = jdbc.queryForObject("SELECT MAX(right_edge) FROM neuron WHERE right_edge < ?", String.class, left_edge);
		return max_right_edge;
	}

	// right_edge以上で最少のleft_edge
	public String min_left_edge(String right_edge) {
		String min_left_edge = jdbc.queryForObject("SELECT MIN(left_edge) FROM neuron WHERE left_edge > ?", String.class, right_edge);
		return min_left_edge;
	}

	// 親のleft_edge
	public String parent_left_edge(String id) {
		String parent_left_edge = jdbc.queryForObject("SELECT MAX(left_edge) FROM neuron WHERE left_edge < ? AND neuron_level = ? - 1",
				String.class, left_edge(id), neuron_level(id));
		return parent_left_edge;
	}

	// 親のright_edge
	public String parent_right_edge(String id) {
		String parent_right_edge = jdbc.queryForObject("SELECT MIN(right_edge) FROM neuron WHERE right_edge > ? AND neuron_level = ? - 1",
				String.class, right_edge(id), neuron_level(id));
		return parent_right_edge;
	}
}
