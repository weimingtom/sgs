package dnl.games.stragego.ui.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import dnl.games.stragego.ui.BoardUI;
import dnl.games.stratego.BoardMapReader;
//import dnl.ui.FileChooserUtils;

public class SaveBoardAction extends StrategoUiAction {

	public SaveBoardAction(JFrame mainFrame, BoardUI boardUI) {
		super("Save to File", mainFrame, boardUI);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//File file = FileChooserUtils.selectFileForSaving(mainFrame);
		File file = null;
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(mainFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
        }
		try {
			BoardMapReader.save(boardUI.getBoard(), file);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
