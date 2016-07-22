package jp.co.sbps.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jp.co.sbps.entity.Neuron;

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
	public void generateTreeDiagram(Neuron neuron, Neuron youngestNeuron) {
		jdbc.update("INSERT INTO tree_diagram (ancestor, descendant) "
				+ "(SELECT ancestor, ? FROM tree_diagram WHERE descendant = ?) UNION (SELECT ?, ?)",
				youngestNeuron.getId(), neuron.getId(), youngestNeuron.getId(), youngestNeuron.getId());
	}
	
	// 木構造の削除
	public void extinctTreeDiagram(Neuron neuron) {
		jdbc.update("DELETE FROM tree_diagram WHERE descendant IN (SELECT descendant FROM tree_diagram WHERE ancestor = ?)", neuron.getId());
	}
	
	// 木構造の挿入
	public void insertTreeDiagram(Neuron neuron, Neuron youngestNeuron) {
		
		generateTreeDiagram(neuronDao.parentNeuron(neuron), youngestNeuron);
		jdbc.update("INSERT INTO tree_diagram (ancestor, descendant) "
				+ "(SELECT ?, descendant FROM tree_diagram WHERE ancestor = ?)", youngestNeuron.getId(), neuron.getId());
	}
}
