package ssedit.GUI;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

public class History {
    public static JComboBox makeComboBox(String str) {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        // 初期値をセット
        model.addElement(str);
        return new JComboBox(model);
    }
	
	
	public static boolean addItem(JComboBox combo, String str, int max) {
        if(str==null || str.length()==0) return false;
        combo.setVisible(false);
        DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();
        model.removeElement(str);
        model.insertElementAt(str, 0);
        if(model.getSize()>max) {
            model.removeElementAt(max);
        }
//        for(int i = 0; i < model.getSize(); i++){
//        	if(model.getElementAt(i).equals(""))
        		model.removeElement("");
//        }
        combo.setSelectedIndex(0);
        combo.setVisible(true);
        return true;
    }

	public static String saveHistory(String target, JComboBox combo){
		// 作業フォルダの履歴の数を取得
		int count = combo.getItemCount();
		int h_count = 1;
		String history = "";
		for (int i = 0; i < count; i++) {
			// 作業フォルダ以外を抽出
			if (!combo.getItemAt(i).equals(target)) {
				if(h_count == 1){
					history += combo.getItemAt(i);
				} else {
					history += ("," + combo.getItemAt(i));
				}
				h_count++;
			}
		}
		return history;
	}
}
