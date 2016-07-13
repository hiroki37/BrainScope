package jp.co.sbps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

	@Autowired
	NeuronDao neuronDao;

	@Autowired
	ConfigDao configDao;
	
	@RequestMapping("brainscope")
	public String brainscope(Integer id, String title, String content, String move_up_flag, String move_down_flag,
			String update_flag, String generate_flag, String delete_flag, String insert_flag, Model model) {

		// スコープアドレスの移動（上り）
		if (id != null && neuronDao.neuron_level(id) - 1 > 0 && move_up_flag != null) {
			configDao.move_up(id);
		}

		// スコープアドレスの移動（下り）
		if (id != null && move_down_flag != null) {
			configDao.move_down(id);
		}

		// ニューロンの生成
		if (generate_flag != null) {
			neuronDao.generate(id);
		}

		// ニューロンの削除
		if (id != null && delete_flag != null) {
			neuronDao.delete(id);
		}

		// ニューロンの更新
		if (id != null && update_flag != null) {
			neuronDao.update(id, title, content);
		}

		// ニューロンの挿入
		if (id != null && insert_flag != null) {
			neuronDao.insert(id);
		}

		// 木構造をモデルに代入		
		model.addAttribute("brainscope", neuronDao.display());

		return "brainscope";
	}
}