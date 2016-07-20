package jp.co.sbps.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/*
 * tree_diagramテーブルへのアクセスをするDaoクラス
*/

@Component
public class TreeDiagramDao {
	
	@Autowired
	NeuronDao neuronDao;
	
	@Autowired
	private JdbcTemplate jdbc;
	
	// 木構造の生成
	public void generateTreeDiagram(Integer id, Integer youngestId) {
		jdbc.update("INSERT INTO tree_diagram (ancestor, descendant) "
				+ "(SELECT ancestor, ? FROM tree_diagram WHERE descendant = ?) UNION (SELECT ?, ?)",
				youngestId, id, youngestId, youngestId);
	}
	
	// 木構造の削除
	public void extinctTreeDiagram(Integer id) {
		jdbc.update("DELETE FROM tree_diagram WHERE descendant IN (SELECT descendant FROM tree_diagram WHERE ancestor = ?)", id);
	}
	
	// 木構造の挿入
	public void insertTreeDiagram(Integer id, Integer parentId, Integer youngestId) {
		generateTreeDiagram(parentId, youngestId);
		jdbc.update("INSERT INTO tree_diagram (ancestor, descendant) "
				+ "(SELECT ?, descendant FROM tree_diagram WHERE ancestor = ?)", youngestId, id);
	}
}
