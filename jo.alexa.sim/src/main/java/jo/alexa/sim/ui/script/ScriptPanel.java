package jo.alexa.sim.ui.script;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import jo.alexa.sim.ui.data.RuntimeBean;
import jo.alexa.sim.ui.data.ScriptTransactionBean;
import jo.alexa.sim.ui.logic.RuntimeLogic;
import jo.alexa.sim.ui.logic.ScriptLogic;

public class ScriptPanel extends JPanel implements PropertyChangeListener
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean mRuntime;
    
    // status
    private JLabel      mName;
    private JLabel      mStatus;
    // commands
    private JButton     mRun;
    private JButton     mInsertHistory;
    private JButton     mLoadDisk;
    private JButton     mSaveDisk;
    private JButton     mClearScript;
    // edits
    private JButton     mDelete;
    private JButton     mMoveUp;
    private JButton     mMoveDown;
    private JButton     mRename;
    private JButton     mEditExpected;
    private JButton     mToggleExpectation;
    private JButton     mUpdateExpected;
    private JButton     mClearResults;
    private JList<ScriptTransactionBean> mScript;
    
    public ScriptPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        // status
        mName = new JLabel();
        mName.setFont(mName.getFont().deriveFont(Font.BOLD));
        mStatus = new JLabel();
        // commands
        mRun = new JButton("Run");
        mRun.setToolTipText("Run current script");
        mInsertHistory = new JButton("Insert History");
        mInsertHistory.setToolTipText("Insert current history before selected point in script");
        mLoadDisk = new JButton("Load...");
        mLoadDisk.setToolTipText("Read a script from the disk");
        mSaveDisk = new JButton("Save...");
        mSaveDisk.setToolTipText("Save script to disk");
        mClearScript = new JButton("Clear Script");
        mClearScript.setToolTipText("Delete all lines from script");
        // edits
        mDelete = new JButton("Del");
        mDelete.setToolTipText("Delete selected line from script");
        mMoveUp = new JButton("\u2191");
        mMoveUp.setToolTipText("Move selected lines up");
        mMoveDown = new JButton("\u2193");
        mMoveDown.setToolTipText("Move selected lines down");
        mRename = new JButton("Rename");
        mRename.setToolTipText("Rename script");
        mEditExpected = new JButton("Edit");
        mEditExpected.setToolTipText("Edit expected text");
        mToggleExpectation = new JButton("Toggle");
        mToggleExpectation.setToolTipText("Toggle if expected text is pass/fail/don't care");
        mUpdateExpected = new JButton("Update");
        mUpdateExpected.setToolTipText("Update expected with actual");
        mClearResults = new JButton("Clear Results");
        mClearResults.setToolTipText("Clear results");
        mScript = new JList<ScriptTransactionBean>(new ScriptModel(mRuntime));
        mScript.setCellRenderer(new ScriptCellRenderer());
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("Center", new JScrollPane(mScript));
        JPanel topBar = new JPanel();
        topBar.setLayout(new GridLayout(2, 1));
        JPanel statusLine = new JPanel();
        topBar.add(statusLine);
        statusLine.setLayout(new BorderLayout());
        statusLine.add("Center", mStatus);
        statusLine.add("West", mName);
        JPanel commandBar = new JPanel();
        topBar.add(commandBar);
        add("North", topBar);
        commandBar.setLayout(new FlowLayout());
        commandBar.add(mRun);
        commandBar.add(mInsertHistory);
        commandBar.add(mLoadDisk);
        commandBar.add(mSaveDisk);
        commandBar.add(mClearScript);
        JPanel editBar = new JPanel();
        add("South", editBar);
        editBar.setLayout(new FlowLayout());
        editBar.add(mDelete);
        editBar.add(mMoveUp);
        editBar.add(mMoveDown);
        editBar.add(mRename);
        editBar.add(mEditExpected);
        editBar.add(mToggleExpectation);
        editBar.add(mUpdateExpected);
        commandBar.add(mClearResults);
    }

    private void initLink()
    {
        mRun.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doRun();
            }
        });
        mClearScript.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doClearScript();
            }
        });
        mClearResults.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doClearResults();
            }
        });
        mInsertHistory.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doLoadHistory();
            }
        });
        mLoadDisk.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doLoadDisk();
            }
        });
        mSaveDisk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doSaveDisk();
            }
        });
        mDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doDelete();
            }
        });
        mToggleExpectation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doToggleExpectation();
            }
        });
        mEditExpected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doEditExpected();
            }
        });
        mRename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doRename();
            }
        });
        mUpdateExpected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doUpdateExpected();
            }
        });
        mMoveUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doMoveUp();
            }
        });
        mMoveDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doMoveDown();
            }
        });
        mRuntime.addPropertyChangeListener(this);
        mRuntime.getApp().addPropertyChangeListener(this);
    }

    private void doRun()
    {
        ScriptLogic.run(mRuntime);
    }

    private void doClearScript()
    {
        ScriptLogic.clearScript(mRuntime);
    }

    private void doClearResults()
    {
        ScriptLogic.clearResults(mRuntime);
    }
    
    private void doLoadHistory()
    {
        int idx = mScript.getSelectedIndex();
        if (idx < 0)
            idx = 0;
        ScriptLogic.insertHistory(mRuntime, idx);
    }
    
    private void doLoadDisk()
    {
        FileDialog fd = new FileDialog(getFrame(), "Load Script", FileDialog.LOAD);
        fd.setDirectory(RuntimeLogic.getProp("script.file.dir"));
        fd.setFile(RuntimeLogic.getProp("script.file.file"));
        fd.setVisible(true);
        if (fd.getDirectory() == null)
            return;
        String historyFile = fd.getDirectory()+System.getProperty("file.separator")+fd.getFile();
        if ((historyFile == null) || (historyFile.length() == 0))
            return;
        try
        {
            ScriptLogic.load(mRuntime, new File(historyFile));
            RuntimeLogic.setProp("script.file.dir", fd.getDirectory());
            RuntimeLogic.setProp("script.file.file", fd.getFile());
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error reading "+historyFile, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doSaveDisk()
    {
        FileDialog fd = new FileDialog(getFrame(), "Save Script", FileDialog.SAVE);
        fd.setDirectory(RuntimeLogic.getProp("script.file.dir"));
        fd.setFile(RuntimeLogic.getProp("script.file.file"));
        fd.setVisible(true);
        if (fd.getDirectory() == null)
            return;
        String historyFile = fd.getDirectory()+System.getProperty("file.separator")+fd.getFile();
        if ((historyFile == null) || (historyFile.length() == 0))
            return;
        try
        {
            ScriptLogic.saveScript(mRuntime, new File(historyFile));
            RuntimeLogic.setProp("script.file.dir", fd.getDirectory());
            RuntimeLogic.setProp("script.file.file", fd.getFile());
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error reading "+historyFile, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void doDelete()
    {
        List<ScriptTransactionBean> sel = mScript.getSelectedValuesList();
        ScriptLogic.deleteLines(mRuntime, sel);
    }
    
    private void doRename()
    {
        String newValue = (String)JOptionPane.showInputDialog(this, "Enter in new script name", "Script Name", 
                JOptionPane.QUESTION_MESSAGE, null, null, mRuntime.getScript().getName());
        if (newValue != null)
            ScriptLogic.setScriptName(mRuntime, newValue);
    }
    
    private void doEditExpected()
    {
        ScriptTransactionBean sel = mScript.getSelectedValue();
        String newValue = (String)JOptionPane.showInputDialog(this, "Enter in expected value", "Expected Value", 
                JOptionPane.QUESTION_MESSAGE, null, null, sel.getOutputText());
        if (newValue != null)
            ScriptLogic.setExpected(mRuntime, sel, newValue);
    }
    
    private void doToggleExpectation()
    {
        List<ScriptTransactionBean> sel = mScript.getSelectedValuesList();
        ScriptLogic.toggleExpectations(mRuntime, sel);
    }
    
    private void doUpdateExpected()
    {
        List<ScriptTransactionBean> sel = mScript.getSelectedValuesList();
        ScriptLogic.updateExpectated(mRuntime, sel);
    }
    
    private void doMoveUp()
    {
        ScriptLogic.moveUp(mRuntime, mScript.getSelectedIndices());
    }
    
    private void doMoveDown()
    {
        ScriptLogic.moveDown(mRuntime, mScript.getSelectedIndices());
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (mRuntime.isScriptRunning())
            mStatus.setText("Script running...");
        else
        {
            Boolean result = ScriptLogic.pass(mRuntime.getScript());
            if (result == null)
                mStatus.setText("Indeterminate script result");
            else if (result)
                mStatus.setText("Script passed");
            else
                mStatus.setText("Script failed");
        }
        mName.setText(mRuntime.getScript().getName());
    }
    
    private Frame getFrame()
    {
        for (Container c = getParent(); c != null; c = c.getParent())
            if (c instanceof Frame)
                return (Frame)c;
        return null;
    }
}
